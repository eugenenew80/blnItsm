package bln.itsm.client.exc;

import lombok.Getter;
import org.springframework.http.ResponseEntity;

public class NotAuthorizedException extends RuntimeException {
    @Getter
    private ResponseEntity response;

    public NotAuthorizedException(String msg) {
        super(msg);
    }

    public NotAuthorizedException(ResponseEntity<String> response) {
        super("Not authorized");
        this.response = response;
    }
}
