package bln.itsm.schedule;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QueryRequestDto {

    @JsonProperty("RootSchemaName")
    private String rootSchemaName;

    @JsonProperty("OperationType")
    private Integer operationType;

    @JsonProperty("ColumnValues")
    private QueryColumnValueDto ColumnValueDto;
}
