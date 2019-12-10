package bln.itsm.schedule;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemValueDto {

    @JsonProperty("ExpressionType")
    private Integer expressionType;

    @JsonProperty("Parameter")
    private ParameterDto parameter;
}
