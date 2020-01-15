package bln.itsm.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;
import java.io.IOException;

public class MyErrorHandler implements ResponseErrorHandler {
    private static final Logger logger = LoggerFactory.getLogger(MyErrorHandler.class);

    @Override
    public void handleError(ClientHttpResponse response) throws IOException {
    }

    @Override
    public boolean hasError(ClientHttpResponse response) throws IOException {
        return true;
    }
}
