package bln.itsm.web.dto;


import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class RequestSuspenseDto {
    private String id;
    private String requestNumber;
    private String stateCode;
    private String suspensionReasonCode;
    private String suspensionReasonDesc;
    private LocalDateTime renewalDate;
}
