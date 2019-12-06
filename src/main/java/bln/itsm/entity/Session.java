package bln.itsm.entity;

import bln.itsm.entity.enums.BatchStatusEnum;
import bln.itsm.entity.enums.DirectionEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.DynamicUpdate;
import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(of= {"id"})
@Entity
@Table(name = "ws_sessions")
@DynamicUpdate
public class Session {

    @Id
    @SequenceGenerator(name="ws_sessions_s", sequenceName = "ws_sessions_s", allocationSize=1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ws_sessions_s")
    private Long id;

    @Column(name="object_code")
    private String objectCode;

    @Column(name="direction")
    @Enumerated(EnumType.STRING)
    private DirectionEnum direction;

    @Column(name = "start_date")
    private LocalDateTime startDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    @Column(name = "rec_count")
    private Long recCount;

    @Column(name="status")
    @Enumerated(EnumType.STRING)
    private BatchStatusEnum status;

    @Column(name = "err_msg")
    private String errMsg;
}
