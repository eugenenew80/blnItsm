package bln.itsm.entity;

import bln.itsm.entity.enums.BatchStatusEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;
import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(of= {"id"})
@Entity
@Table(name = "itsm_evaluation_interface")
public class Evaluation {

    @Id
    @SequenceGenerator(name="itsm_evaluation_interface_s", sequenceName = "itsm_evaluation_interface_s", allocationSize=1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "itsm_evaluation_interface_s")
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
