package bln.itsm.entity;

import bln.itsm.entity.enums.BatchStatusEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(of= {"id"})
@Entity
@Table(name = "ITSM_EVALUATION_INTERFACE")
public class Evaluation {

    @Id
    @SequenceGenerator(name="ITSM_EVALUATION_INTERFACE_S", sequenceName = "ITSM_EVALUATION_INTERFACE_S", allocationSize=1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ITSM_EVALUATION_INTERFACE_S")
    private Long id;

    @Column(name = "request_number")
    private String requestNumber;

    @Column(name = "quality_code")
    private String qualityCode;

    @Column(name = "evaluation_date")
    private LocalDateTime evaluationDate;

    @Column(name = "create_date")
    private LocalDateTime createDate;

    @Column(name = "last_update_date")
    private LocalDateTime lastUpdateDate;

    @Column(name = "request_id")
    private Long requestId;

    @Column(name = "transfer_status")
    @Enumerated(EnumType.STRING)
    private BatchStatusEnum transferStatus;

}
