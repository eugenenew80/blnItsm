package bln.itsm.client.login;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequestDto {

    @JsonProperty("UserName")
    private String userName;

    @JsonProperty("UserPassword")
    private String userPassword;
}
