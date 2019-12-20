package bln.itsm.client.rating;

import bln.itsm.client.ItemValueDto;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RatingItemDto {

    @JsonProperty("RequestNumber")
    private ItemValueDto requestNumber;

    @JsonProperty("QualityCode")
    private ItemValueDto qualityCode;

    @JsonProperty("EvalutionDate")
    private ItemValueDto evalutionDate;
}
