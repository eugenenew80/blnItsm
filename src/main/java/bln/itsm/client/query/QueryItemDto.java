package bln.itsm.client.query;

import bln.itsm.client.ItemValueDto;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class QueryItemDto {

    @JsonProperty("Description")
    private ItemValueDto description;

    @JsonProperty("RequestAuthor")
    private ItemValueDto requestAuthor;

    @JsonProperty("Company")
    private ItemValueDto company;

    @JsonProperty("Category")
    private ItemValueDto category;

    @JsonProperty("Subject")
    private ItemValueDto subject;
}
