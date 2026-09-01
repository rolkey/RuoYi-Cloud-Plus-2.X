package org.dromara.common.websocket.config;

import org.dromara.common.websocket.controller.WebSocketSubscribeController;
import org.dromara.common.websocket.service.WebSocketSubscribeService;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;

/**
 * WebSocket 订阅接口配置
 * <p>
 * 订阅接口为 REST 接口，独立于 WebSocket 连接端点，不随 websocket.enabled 开关
 *
 * @author ruoyi
 */
@AutoConfiguration
public class WebSocketSubscribeConfig {

    @Bean
    public WebSocketSubscribeService webSocketSubscribeService() {
        return new WebSocketSubscribeService();
    }

    @Bean
    public WebSocketSubscribeController webSocketSubscribeController(WebSocketSubscribeService webSocketSubscribeService) {
        return new WebSocketSubscribeController(webSocketSubscribeService);
    }
}
