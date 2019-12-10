package bln.itsm.client.login;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class LoginResponseDto {
    @JsonProperty("Code")
    private String code;

    @JsonProperty("Message")
    private String message;

    @JsonProperty("Exception")
    private String exception;

    @JsonProperty("PasswordChangeUrl")
    private String passwordChangeUrl;

    @JsonProperty("RedirectUrl")
    private String redirectUrl;
}
