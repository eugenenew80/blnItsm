package bln.itsm.web.dto;

import lombok.Data;

@Data
public class RequestFileDto {
    private String fileName;
    private String fileType;
    private Long fileSize;
    private String fileContentBase64;
}
