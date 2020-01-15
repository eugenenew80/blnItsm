package bln.itsm.client.query;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
public class QueryResponseDto {
    private String id;
    private boolean success;
    private Integer rowAffected;
    private boolean nextPrcElReady;
}
