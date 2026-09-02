package org.dromara.common.websocket.listener;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.json.utils.JsonUtils;
import org.dromara.common.websocket.holder.WebSocketSessionHolder;
import org.dromara.common.websocket.service.WebSocketSubscribeService;
import org.dromara.common.websocket.utils.WebSocketUtils;
import org.dromara.websocket.api.dto.WebSocketMessageDto;
import org.springframework.amqp.rabbit.annotation.RabbitListener;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * WebSocket 消息消费者（RabbitMQ）
 *
 * @author ruoyi
 */
@Slf4j
public class WebSocketTopicListener {

    private final WebSocketSubscribeService subscribeService;

    public WebSocketTopicListener(WebSocketSubscribeService subscribeService) {
        this.subscribeService = subscribeService;
    }

    /**
     * 消费 RabbitMQ 消息并路由推送
     * <p>
     * 路由优先级：定向 sessionKeys > 订阅 type（前缀匹配）> 广播
     *
     * @param message 消息（JSON 字符串）
     */
    @RabbitListener(queues = "#{websocketQueue.name}")
    public void onMessage(String message) {
        WebSocketMessageDto dto;
        try {
            dto = JsonUtils.parseObject(message, WebSocketMessageDto.class);
        } catch (Exception e) {
            log.warn("[websocket] 消息解析失败: {}", message, e);
            return;
        }
        if (dto == null || dto.getMessage() == null) {
            return;
        }
        log.info("[websocket] 收到推送消息 type={}, sessionKeys={}, message={}", dto.getType(), dto.getSessionKeys(), dto.getMessage());
        String payload = buildPayload(dto);
        if (CollUtil.isNotEmpty(dto.getSessionKeys())) {
            log.info("[websocket] 定向推送 sessionKeys={}", dto.getSessionKeys());
            for (Object sessionKey : dto.getSessionKeys()) {
                WebSocketUtils.sendMessage(Convert.toLong(sessionKey), payload);
            }
        } else if (StrUtil.isNotBlank(dto.getType())) {
            Set<Long> userIds = subscribeService.getSubscribedUsers(dto.getType());
            log.info("[websocket] 订阅路由 type={} 命中 userIds={}", dto.getType(), userIds);
            for (Long userId : userIds) {
                WebSocketUtils.sendMessage(userId, payload);
            }
        } else {
            log.info("[websocket] 广播推送");
            WebSocketSessionHolder.getSessionsAll().forEach(key -> WebSocketUtils.sendMessage(key, payload));
        }
    }

    private String buildPayload(WebSocketMessageDto dto) {
        Map<String, Object> envelope = new HashMap<>();
        envelope.put("type", dto.getType());
        envelope.put("data", dto.getMessage());
        envelope.put("timestamp", System.currentTimeMillis());
        return JsonUtils.toJsonString(envelope);
    }
}
