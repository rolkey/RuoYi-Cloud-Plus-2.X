package org.dromara.websocket.api.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 实时消息 DTO
 *
 * @author ruoyi
 */
@Data
public class WebSocketMessageDto implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 消息类型（三级主题：主类/子类/具体业务ID），用于订阅前缀匹配
     */
    private String type;

    /**
     * 需要推送到的用户 id 列表（定向推送，优先级最高）
     */
    private List<Long> sessionKeys;

    /**
     * 需要发送的消息（JSON 字符串，服务端原样透传）
     */
    private String message;

    /**
     * 消息来源服务名
     */
    private String source;

}
