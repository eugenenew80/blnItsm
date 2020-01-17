package bln.itsm.entity;

import bln.itsm.client.ItemValueDto;
import bln.itsm.client.ParameterDto;
import bln.itsm.client.query.QueryColumnValueDto;
import bln.itsm.client.query.QueryItemDto;
import bln.itsm.client.query.QueryRequestDto;
import bln.itsm.converter.jpa.BooleanToIntConverter;
import bln.itsm.entity.enums.BatchStatusEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;
import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
@EqualsAndHashCode(of= {"id"})
@Entity
@Table(name = "itsm_support_requests_interface")

@NamedStoredProcedureQueries({
    @NamedStoredProcedureQuery(
        name = "SupportRequest.updateStatuses",
        procedureName = "sap_interface.itsm_out_update"
    )
})
public class SupportRequest {
    @Id
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

    @Column(name = "is_transferred")
    @Convert(converter = BooleanToIntConverter.class)
    private Boolean isTransferred;

    @Column(name="transferred_to_itsm_state")
    @Enumerated(EnumType.STRING)
    private BatchStatusEnum status;

    @Column(name = "last_update_date")
    private LocalDateTime lastUpdateDate;

    @OneToMany(mappedBy = "supportRequest")
    private List<SupportRequestFile> requestFiles;

    public QueryRequestDto buildRequestQuery() {
        ParameterDto descriptionParam = new ParameterDto(1, this.getDescription());
        ParameterDto authorParam = new ParameterDto(1, this.getAuthor());
        ParameterDto companyParam = new ParameterDto(1, this.getCompany());
        ParameterDto categoryParam = new ParameterDto(1, this.getCategory());
        ParameterDto subjectParam = new ParameterDto(1, this.getSubject());

        ItemValueDto descriptionItemValue = new ItemValueDto(2, descriptionParam);
        ItemValueDto authorItemValue = new ItemValueDto(2, authorParam);
        ItemValueDto companyItemValue = new ItemValueDto(2, companyParam);
        ItemValueDto categoryItemValue = new ItemValueDto(2, categoryParam);
        ItemValueDto subjectItemValue = new ItemValueDto(2, subjectParam);
        QueryItemDto item = new QueryItemDto();

        item.setDescription(descriptionItemValue);
        item.setRequestAuthor(authorItemValue);
        item.setCompany(companyItemValue);
        item.setCategory(categoryItemValue);
        item.setSubject(subjectItemValue);

        QueryColumnValueDto columnValue = new QueryColumnValueDto(item);
        return new QueryRequestDto("INFBISRequest", 1, columnValue);
    }
}
