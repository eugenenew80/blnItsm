package bln.itsm.client;

import bln.itsm.schedule.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


@Service
public class RestClient {
    private static final Logger logger = LoggerFactory.getLogger(RestClient.class);

    private ResponseEntity<LoginResponseDto> login(String user, String password) {
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

    public  ResponseEntity<QueryResponseDto> request(String user, String password, QueryRequestDto queryDto) {
        ResponseEntity<LoginResponseDto> loginResponse = login(user, password);

        RestTemplate queryRestTemplate = new RestTemplateBuilder().build();
        String queryUrl = "http://itsm-app-2.corp.kegoc.kz/0/dataservice/json/reply/InsertQuery";
        ResponseEntity<QueryResponseDto> queryResponse = queryRestTemplate.exchange(
            queryUrl,
            HttpMethod.POST,
            new HttpEntity<>(queryDto, headers(loginResponse)),
            QueryResponseDto.class
        );

        return queryResponse;
    }

    public void rating(String user, String password, RatingRequestDto ratingDto) {
        ResponseEntity<LoginResponseDto> loginResponse = login(user, password);

        logger.info(ratingDto.toString());

        RestTemplate queryRestTemplate = new RestTemplateBuilder().build();
        String queryUrl = "http://itsm-app-2.corp.kegoc.kz/0/dataservice/json/reply/InsertQuery";
        ResponseEntity<String> queryResponse = queryRestTemplate.exchange(
            queryUrl,
            HttpMethod.POST,
            new HttpEntity<>(ratingDto, headers(loginResponse)),
            String.class
        );

        logger.info("status:" + queryResponse.getStatusCodeValue());
        logger.info("body:" + queryResponse.getBody());
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


}
