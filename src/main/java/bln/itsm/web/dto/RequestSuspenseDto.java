package bln.itsm.web.dto;


import lombok.Data;
import java.time.LocalDate;

@Data
public class RequestSuspenseDto {
    private String requestNumber;
    private String stateCode;
    private String suspensionReasonCode;
    private String suspensionReasonDesc;
    private LocalDate renewalDate;
}
