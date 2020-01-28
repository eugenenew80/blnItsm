package bln.itsm.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MessageDto {
    private final String type;
    private final String path;
    private final String message;
}
