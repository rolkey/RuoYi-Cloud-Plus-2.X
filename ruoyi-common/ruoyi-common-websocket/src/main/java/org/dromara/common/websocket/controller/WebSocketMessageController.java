package org.dromara.common.websocket.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import lombok.RequiredArgsConstructor;
import org.dromara.common.core.domain.R;
import org.dromara.common.json.utils.JsonUtils;
import org.dromara.websocket.api.constant.WebSocketConstants;
import org.dromara.websocket.api.dto.WebSocketMessageDto;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * WebSocket 消息发布接口
 *
 * @author ruoyi
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/websocket")
public class WebSocketMessageController {

    private final RabbitTemplate rabbitTemplate;

    /**
     * 发布消息（用于测试推送）
     *
     * @param dto 消息
     */
    @SaCheckLogin
    @PostMapping("/publish")
    public R<Void> publish(@RequestBody WebSocketMessageDto dto) {
        rabbitTemplate.convertAndSend(WebSocketConstants.WEBSOCKET_EXCHANGE, "", JsonUtils.toJsonString(dto));
        return R.ok();
    }
}
