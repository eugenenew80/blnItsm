package bln.itsm.client;

import bln.itsm.client.login.LoginRequestDto;
import bln.itsm.client.login.LoginResponseDto;
import bln.itsm.entity.SupportRequestFile;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class RestClient {
    private static final Logger logger = LoggerFactory.getLogger(RestClient.class);
    private boolean isLogin = false;
    private ResponseEntity<LoginResponseDto> loginResponse;
    private String user = "temp40a";
    private String password = "Q1w2e3r4t%777";

    private void login() {
        logger.info("Trying to authorization...");
        this. isLogin = false;
        this.loginResponse = null;

        RestTemplate restTemplate = new RestTemplateBuilder().build();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String url = "http://itsm-app-2.corp.kegoc.kz/ServiceModel/AuthService.svc/Login";
        LoginRequestDto request = new LoginRequestDto(user, password);

        ResponseEntity<LoginResponseDto> response = restTemplate.exchange(
            url,
            HttpMethod.POST,
            new HttpEntity<>(request, headers),
            LoginResponseDto.class
        );

        if (response.getStatusCodeValue() != 200) {
            logger.error("ERROR!}");
            return;
        }

        this. isLogin = true;
        this.loginResponse = response;
        logger.info("OK!");
    }

    public  ResponseEntity<String> request(InsertQuery insertQuery) {
        if (!isLogin)
            login();

        if (!isLogin) {
            logger.error("Sending request cancelled, not authorized");
            return null;
        }


        //url
        String queryUrl = "http://itsm-app-2.corp.kegoc.kz/0/dataservice/json/reply/InsertQuery";

        //headers
        HttpHeaders headers = headers(loginResponse);

        //body
        ObjectMapper mapper = new ObjectMapper();
        String jsonStr = "";
        try {
            jsonStr = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(insertQuery);
            logger.info(jsonStr);
        }
        catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }

        //log request
        logger.info("---headers---");
        logger.info(headers.toString());
        logger.info("");
        logger.info("---body---");
        logger.info(jsonStr);

        //send request
        RestTemplate restTemplate = new RestTemplateBuilder().build();
        restTemplate.setErrorHandler(new MyErrorHandler());
        ResponseEntity<String> response = restTemplate.exchange(
            queryUrl,
            HttpMethod.POST,
            new HttpEntity<>(jsonStr, headers),
            String.class
        );

        //log response
        logger.info("");
        logger.info("---status---");
        logger.info("" + response.getStatusCodeValue());
        logger.info("---response---");
        logger.info(response.getBody());
        return response;
    }


    public ResponseEntity<String> fileUpload(SupportRequestFile requestFile) {
        if (!isLogin)
            login();

        if (!isLogin) {
            logger.error("Sending request cancelled, not authorized");
            return null;
        }

        String params = "totalFileLength={totalFileLength}&fileId={fileId}&filename={filename}&parentColumnValue={parentColumnValue}&entitySchemaName={entitySchemaName}&parentColumnName={parentColumnName}&columnName={columnName}";
        String url = "http://itsm-app-2.corp.kegoc.kz/0/rest/FileApiService/Upload?" + params;

        String fileId = UUID.randomUUID().toString();
        Integer totalLength = new Integer(requestFile.getFileContent().length);
        String fileName = requestFile.getFileName();
        String requestGuid = requestFile.getSupportRequest().getGuid();

        //URL with params
        Map<String, String> urlParams = new HashMap<>();
        urlParams.put("totalFileLength", totalLength.toString());
        urlParams.put("fileId", fileId);
        urlParams.put("filename", fileName);
        urlParams.put("parentColumnValue", requestGuid);
        urlParams.put("entitySchemaName", "INFBISRequestFile");
        urlParams.put("parentColumnName", "INFBISRequest");
        urlParams.put("columnName", "Data");

        URI uri = UriComponentsBuilder.fromUriString(url)
            .buildAndExpand(urlParams)
            .toUri();

        //headers
        HttpHeaders headers = headers(loginResponse);
        headers.setContentType(MediaType.parseMediaType(requestFile.getFileType()));
        headers.add("Content-Range", "bytes 0-" + totalLength.toString() + "/" + totalLength);

        //body
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("filename", fileName);
        body.add("file", new ByteArrayResource(requestFile.getFileContent()));
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        //log request
        logger.info("---url---");
        logger.info(uri.toString());
        logger.info("");
        logger.info("---headers---");
        logger.info(headers.toString());
        logger.info("");

        //send request
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(
            uri,
            HttpMethod.POST,
            requestEntity,
            String.class
        );

        //log response
        logger.info("");
        logger.info("---status---");
        logger.info("" + response.getStatusCodeValue());
        logger.info("---response---");
        logger.info(response.getBody());
        return response;
    }

    private HttpHeaders headers(ResponseEntity<LoginResponseDto> loginResponse) {
        HttpHeaders queryHeaders = new HttpHeaders();
        queryHeaders.setContentType(MediaType.APPLICATION_JSON);
        for (String keyHeader : loginResponse.getHeaders().keySet()) {
            if ("Set-Cookie".equals(keyHeader) ) {
                for (String cookie : loginResponse.getHeaders().get(keyHeader)) {
                    int k = cookie.indexOf("path");
                    String cookieNew = cookie.substring(0, k);
                    queryHeaders.add("Cookie", cookieNew);

                    if (cookie.startsWith("BPMCSRF")) {
                        int n = cookie.indexOf("=");
                        String h_name = cookie.substring(0, n);
                        String h_value = cookie.substring(n + 1);

                        n = h_value.indexOf("path");
                        h_value = h_value.substring(0, n - 2);
                        queryHeaders.set(h_name, h_value);
                    }
                }
            }
        }
        return queryHeaders;
    }

    public <T> T jsonStringToObject(String jsonString, Class<T> clazz) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        try {
            return mapper.readValue(jsonString, clazz);
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    /*
    public String doRequest(ResponseEntity<LoginResponseDto> loginResponse, SupportRequestFile requestFile) {
        String method = "POST";
        String params = "totalFileLength={totalFileLength}&fileId={fileId}&filename={filename}&parentColumnValue={parentColumnValue}&entitySchemaName={entitySchemaName}&parentColumnName={parentColumnName}&columnName={columnName}";
        String urlStr = "http://itsm-app-2.corp.kegoc.kz/0/rest/FileApiService/Upload?" + params;

        String randomUUIDString = UUID.randomUUID().toString();
        Integer totalLength = new Integer(requestFile.getFileContent().length);

        urlStr = urlStr.replace("{totalFileLength}", totalLength.toString());
        urlStr = urlStr.replace("{fileId}", randomUUIDString);
        urlStr = urlStr.replace("{filename}", requestFile.getFileName());
        urlStr = urlStr.replace("{parentColumnValue}", requestFile.getSupportRequest().getGuid());
        urlStr = urlStr.replace("{entitySchemaName}", "INFBISRequestFile");
        urlStr = urlStr.replace("{parentColumnName}", "INFBISRequest");
        urlStr = urlStr.replace("{columnName}", "Data");

        URL url;
        try {
            url = new URL(urlStr);
        }
        catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }

        logger.info("doRequest started");
        logger.info("url: " + urlStr);
        logger.info("method: " + method);

        StringBuffer response = new StringBuffer();
        HttpURLConnection con = null;
        try {
            con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod(method);
            con.setUseCaches(false);
            con.setDoInput(true);
            con.setDoOutput(true);
            con.setConnectTimeout(99999999);
            con.setReadTimeout(99999999);

            HttpHeaders headers = headers(loginResponse);
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.add("Content-Range", "bytes 0-" + totalLength.toString() + "/" + totalLength);
            headers.add("Content-Type", requestFile.getFileType());

            for (String key: headers.keySet()) {
                for (String value : headers.get(key)) {
                    con.setRequestProperty(key, value);
                }
            }
            logger.info("" + con.getRequestProperties());

            try (DataOutputStream wr = new DataOutputStream(con.getOutputStream())) {
                wr.write(requestFile.getFileContent());
                wr.flush();
            }

            try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
                String output;
                while ((output = in.readLine()) != null) {
                    response.append(output);
                }
            }
            logger.info("doRequest successfully completed");
        }

        catch (IOException e) {
            logger.error("doRequest failed: " + e);
            throw new RuntimeException(e);
        }
        finally {
            if (con!=null) con.disconnect();
        }

        logger.info("doRequest successfully completed");
        String responseStr = response.toString();

        logger.info(responseStr);
        return responseStr;
    }
    */
}



