package bln.itsm.client;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParameterDto {

    @JsonProperty("DataValueType")
    private Integer dataValueType;

    @JsonProperty("Value")
    private String value;
}
