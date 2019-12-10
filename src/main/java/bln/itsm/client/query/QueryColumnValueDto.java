package bln.itsm.client.query;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QueryColumnValueDto {

    @JsonProperty("Items")
    private QueryItemDto item;
}
