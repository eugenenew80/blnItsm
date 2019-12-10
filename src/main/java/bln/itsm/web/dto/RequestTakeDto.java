package bln.itsm.web.dto;

import lombok.Data;

@Data
public class RequestTakeDto {
    private String id;
    private String requestNumber;
    private String stateCode;
}
