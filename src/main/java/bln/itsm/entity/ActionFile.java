package bln.itsm.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(of= {"id"})
@Entity
@Table(name = "itsm_file_interface")
public class ActionFile {
    @Id
    @SequenceGenerator(name="itsm_file_interface_s", sequenceName = "itsm_file_interface_s", allocationSize=1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "itsm_file_interface_s")
    private Long id;

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "file_type")
    private String fileType;

    @Column(name = "file_size")
    private Long fileSize;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(name = "file_content", columnDefinition = "BLOB")
    private byte[] fileContent;

    @Column(name = "create_date")
    private LocalDateTime createDate;

    @Column(name = "last_update_date")
    private LocalDateTime lastUpdateDate;

    @ManyToOne
    @JoinColumn(name = "service_interface_id")
    private ActionRequest actionRequest;
}
