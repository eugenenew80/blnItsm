package bln.itsm.entity;

import bln.itsm.entity.enums.BatchStatusEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;
import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Data
@EqualsAndHashCode(of= {"id"})
@Entity
@Table(name = "itsm_support_requests_interface")
public class SupportRequest {

    @Id
    @SequenceGenerator(name="itsm_support_requests_interface_s", sequenceName = "itsm_support_requests_interface_s", allocationSize=1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "itsm_support_requests_interface_s")
    private Long id;

    @Column(name = "request_author")
    private String author;

    @Column(name = "company")
    private String company;

    @Column(name = "category")
    private String category;

    @Column(name = "subject")
    private String subject;

    @Column(name = "description")
    private String description;

    @Column(name = "itsm_id")
    private String guid;

    @Column(name = "itsm_req_num")
    private String requestNumber;

    @Column(name="transferred_to_itsm_state")
    @Enumerated(EnumType.STRING)
    private BatchStatusEnum status;

    @Column(name = "create_date")
    private LocalDateTime createDate;

    @Column(name = "last_update_date")
    private LocalDateTime lastUpdateDate;

    @OneToMany(mappedBy = "supportRequest")
    private List<SupportRequestFile> requestFiles;
}
