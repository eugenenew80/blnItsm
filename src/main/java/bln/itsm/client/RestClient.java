package bln.itsm.client;

import bln.itsm.client.login.LoginRequestDto;
import bln.itsm.client.login.LoginResponseDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

@Service
public class RestClient {
    private static final Logger logger = LoggerFactory.getLogger(RestClient.class);

    public ResponseEntity<LoginResponseDto> login(String user, String password) {
        RestTemplate restTemplate = new RestTemplateBuilder().build();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String url = "http://itsm-app-2.corp.kegoc.kz/ServiceModel/AuthService.svc/Login";
        LoginRequestDto request = new LoginRequestDto("temp40a", "Q1w2e3r4t%777");

        ResponseEntity<LoginResponseDto> response = restTemplate.exchange(
            url,
            HttpMethod.POST,
            new HttpEntity<>(request, headers),
            LoginResponseDto.class
        );

        return response;
    }

    public  ResponseEntity<String> request(ResponseEntity<LoginResponseDto> loginResponse, InsertQuery insertQuery) {
        RestTemplate restTemplate = new RestTemplateBuilder().build();
        restTemplate.setErrorHandler(new MyErrorHandler());

        String queryUrl = "http://itsm-app-2.corp.kegoc.kz/0/dataservice/json/reply/InsertQuery";

        HttpHeaders headers = headers(loginResponse);
        ObjectMapper mapper = new ObjectMapper();
        String jsonStr = "";
        try {
            jsonStr = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(insertQuery);
            logger.info(jsonStr);
        }
        catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        logger.info("---headers---");
        logger.info(headers.toString());
        logger.info("");
        logger.info("---body---");
        logger.info(jsonStr);

        ResponseEntity<String> queryResponse = restTemplate.exchange(
            queryUrl,
            HttpMethod.POST,
            new HttpEntity<>(jsonStr, headers),
            String.class
        );

        logger.info("");
        logger.info("---status---");
        logger.info("" + queryResponse.getStatusCodeValue());
        logger.info("---response---");
        logger.info(queryResponse.getBody());
        return queryResponse;
    }

    private HttpHeaders headers(ResponseEntity<LoginResponseDto> loginResponse) {
        //Формируем заголовок запроса
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
}
