package bln.itsm.client.query;

import lombok.Data;

@Data
public class QueryResponseDto {
    private String id;
    private boolean success;
    private Integer rowAffected;
    private boolean nextPrcElReady;
}
