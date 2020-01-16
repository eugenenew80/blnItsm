package bln.itsm.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(of= {"id"})
@Entity
@Table(name = "itsm_support_requests_file_interface")
public class SupportRequestFile {
    @Id
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

    @Column(name = "last_update_date")
    private LocalDateTime lastUpdateDate;

    @ManyToOne
    @JoinColumn(name = "request_interface_id")
    private SupportRequest supportRequest;
}
