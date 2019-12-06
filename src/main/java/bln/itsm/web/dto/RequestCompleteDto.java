package bln.itsm.web.dto;

import lombok.Data;
import java.util.List;

@Data
public class RequestCompleteDto {
    private String requestNumber;
    private String stateCode;
    private String completeCode;
    private String decisionText;
    private List<RequestFileDto> files;
}
