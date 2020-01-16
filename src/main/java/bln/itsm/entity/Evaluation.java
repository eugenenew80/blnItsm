package bln.itsm.entity;

import bln.itsm.client.ItemValueDto;
import bln.itsm.client.ParameterDto;
import bln.itsm.client.rating.RatingColumnValueDto;
import bln.itsm.client.rating.RatingItemDto;
import bln.itsm.client.rating.RatingRequestDto;
import bln.itsm.entity.enums.BatchStatusEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;
import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
@EqualsAndHashCode(of= {"id"})
@Entity
@Table(name = "itsm_evaluation_interface")
public class Evaluation {
    @Id
    private Long id;

    @Column(name = "request_number")
    private String requestNumber;

    @Column(name = "quality_code")
    private String qualityCode;

    @Column(name = "evaluation_date")
    private LocalDateTime evaluationDate;

    @Column(name = "last_update_date")
    private LocalDateTime lastUpdateDate;

    @Column(name = "transfer_status")
    @Enumerated(EnumType.STRING)
    private BatchStatusEnum transferStatus;

    public RatingRequestDto buildRatingQuery() {
        String dateStr = "\"" + this.getEvaluationDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS")) + "\"";

        ParameterDto requestNumberParameter = new ParameterDto(1, this.getRequestNumber());
        ParameterDto qualityCodeParameter = new ParameterDto(1, this.getQualityCode());
        ParameterDto evaluationDateParameter = new ParameterDto(8, dateStr);

        ItemValueDto requestNumberItemValue = new ItemValueDto(2, requestNumberParameter);
        ItemValueDto qualityCodeItemValue = new ItemValueDto(2, qualityCodeParameter);
        ItemValueDto evaluationDateItemValue = new ItemValueDto(2, evaluationDateParameter);

        RatingItemDto item = new RatingItemDto(requestNumberItemValue, qualityCodeItemValue, evaluationDateItemValue);
        RatingColumnValueDto ratingColumnValueDto = new RatingColumnValueDto(item);
        return new RatingRequestDto("INFBISRating", 1, ratingColumnValueDto);
    }

}
