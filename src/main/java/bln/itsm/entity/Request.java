package bln.itsm.entity;

import bln.itsm.entity.enums.BatchStatusEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;
import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@EqualsAndHashCode(of= {"id"})
@Entity
@Table(name = "itsm_service_interface")
public class Request {

    @Id
    @SequenceGenerator(name="itsm_service_interface_s", sequenceName = "itsm_service_interface_s", allocationSize=1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "itsm_service_interface_s")
    private Long id;

    @Column(name = "itsm_id")
    private String guid;

    @Column(name = "request_number")
    private String requestNumber;

    @Column(name = "request_state_code")
    private String stateCode;

    @Column(name = "decision_text")
    private String decisionText;

    @Column(name = "complete_code")
    private String completeCode;

    @Column(name = "suspension_reason_code")
    private String suspensionReasonCode;

    @Column(name = "suspension_reason_desc")
    private String suspensionReasonDesc;

    @Column(name = "renewal_date")
    private LocalDateTime renewalDate;

    @Column(name="status")
    @Enumerated(EnumType.STRING)
    private BatchStatusEnum status;

    @Column(name = "create_date")
    private LocalDateTime createDate;

    @Column(name = "last_update_date")
    private LocalDateTime lastUpdateDate;
}
