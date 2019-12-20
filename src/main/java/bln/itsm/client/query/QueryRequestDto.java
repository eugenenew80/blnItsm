package bln.itsm.client.query;

import bln.itsm.client.InsertQuery;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QueryRequestDto implements InsertQuery {

    @JsonProperty("RootSchemaName")
    private String rootSchemaName;

    @JsonProperty("OperationType")
    private Integer operationType;

    @JsonProperty("ColumnValues")
    private QueryColumnValueDto ColumnValueDto;
}
