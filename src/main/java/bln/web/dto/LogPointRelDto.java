package bln.web.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of= {"id"})
public class LogPointRelDto {
    private Long id;
    private String name;
}