package bln.itsm.schedule;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class RatingItemDto {

    @JsonProperty("RequestNumber")
    private ItemValueDto requestNumber;

    @JsonProperty("QualityCode")
    private ItemValueDto qualityCode;

    @JsonProperty("EvalutionDate")
    private ItemValueDto evalutionDate;
}
