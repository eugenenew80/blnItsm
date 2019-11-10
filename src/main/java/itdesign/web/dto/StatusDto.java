package itdesign.web.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel(value = "Status", description = "Статус")
@Data
public class StatusDto {
    @ApiModelProperty(value = "Идентификатор", example = "1", position = 0)
    private Long id;

    @ApiModelProperty(value = "Наименование", example = "Группа отчётов 1", position = 1)
    private String name;

}
