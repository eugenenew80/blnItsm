package bln.itsm.client;

import bln.itsm.client.exc.NotAuthorizedException;
import bln.itsm.client.exc.SendRequestException;
import bln.itsm.client.login.LoginRequestDto;
import bln.itsm.client.login.LoginResponseDto;
import bln.itsm.entity.SupportRequestFile;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class RestClient {
    private static final Logger logger = LoggerFactory.getLogger(RestClient.class);
    private static String user = "temp40a";
    private static String password = "Q1w2e3r4t%777";
    private static String baseUrl = "http://itsm-app-2.corp.kegoc.kz";
    private static String loginUrlSeg = "/ServiceModel/AuthService.svc/Login";
    private static String queryUrlSeg = "/0/dataservice/json/reply/InsertQuery";
    private static String uploadUrlSeg = "/0/rest/FileApiService/Upload";
    private static String uploadUrlParams = "?totalFileLength={totalFileLength}&fileId={fileId}&filename={filename}&parentColumnValue={parentColumnValue}&entitySchemaName={entitySchemaName}&parentColumnName={parentColumnName}&columnName={columnName}";

    private boolean isLogin = false;
    private ResponseEntity<String> loginResponse;

    private void login() {
        logger.info("Trying to authorization...");
        this. isLogin = false;
        this.loginResponse = null;

        RestTemplate restTemplate = new RestTemplateBuilder().build();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String url = baseUrl + loginUrlSeg;
        LoginRequestDto request = new LoginRequestDto(user, password);

        ResponseEntity<String> response = restTemplate.exchange(
            url,
            HttpMethod.POST,
            new HttpEntity<>(request, headers),
            String.class
        );

        if (response.getStatusCodeValue() != 200) {
            logger.error("");
            throw new NotAuthorizedException(response);
        }

        this. isLogin = true;
        this.loginResponse = response;
        logger.info("OK!");
    }

    public  ResponseEntity<String> request(InsertQuery insertQuery) {
        if (!isLogin) login();

        //url
        String queryUrl = baseUrl + queryUrlSeg;

        //headers
        HttpHeaders headers = headers(loginResponse);

        //body
        ObjectMapper mapper = new ObjectMapper();
        String jsonStr;
        try {
            jsonStr = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(insertQuery);
        }
        catch (JsonProcessingException e) {
            throw new SendRequestException("Error during serializing query to json string");
        }

        //log request
        logger.trace("---headers---");
        logger.trace(headers.toString());
        logger.trace("");
        logger.trace("---body---");
        logger.trace(jsonStr);

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
        logger.trace("status" + response.getStatusCodeValue());
        logger.trace("response" +response.getBody());
        return response;
    }

    public ResponseEntity<String> fileUpload(SupportRequestFile requestFile) {
        if (!isLogin) login();

        String url = baseUrl + uploadUrlSeg + uploadUrlParams;

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
        HttpEntity<byte[]> requestEntity = new HttpEntity<>(requestFile.getFileContent(), headers);

        //log request
        logger.trace("---url---");
        logger.trace(uri.toString());
        logger.trace("");
        logger.trace("---headers---");
        logger.trace(headers.toString());
        logger.trace("");

        //send request
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(
            uri,
            HttpMethod.POST,
            requestEntity,
            String.class
        );

        //log response
        logger.trace("status: " + response.getStatusCodeValue());
        logger.trace("response:" + response.getBody());
        return response;
    }

    private HttpHeaders headers(ResponseEntity<String> loginResponse) {
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
            throw new SendRequestException("Error during de-serializing json string to object");
        }
    }
}