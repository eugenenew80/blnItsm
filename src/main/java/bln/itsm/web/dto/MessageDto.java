package bln.itsm.web.dto;

import lombok.Data;
import javax.persistence.Column;

@Data
public class MessageDto {
    @Column(name="msg_num")
    private String msgNum;

    @Column(name="msg_type")
    private String msgType;

    @Column(name="msg")
    private String msg;
}
