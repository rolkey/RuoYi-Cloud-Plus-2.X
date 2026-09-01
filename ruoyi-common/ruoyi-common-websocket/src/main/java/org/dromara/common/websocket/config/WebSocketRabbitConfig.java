package org.dromara.common.websocket.config;

import org.dromara.websocket.api.constant.WebSocketConstants;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.UUID;

/**
 * WebSocket RabbitMQ 配置（fanout 交换机 + 每实例独立队列）
 *
 * @author ruoyi
 */
@Configuration
public class WebSocketRabbitConfig {

    /**
     * fanout 交换机：广播到所有实例
     */
    @Bean
    public FanoutExchange websocketExchange() {
        return new FanoutExchange(WebSocketConstants.WEBSOCKET_EXCHANGE, true, false);
    }

    /**
     * 每实例独立队列：非持久、自动删除、非排他
     */
    @Bean
    public Queue websocketQueue() {
        return new Queue("websocket.queue." + UUID.randomUUID(), false, true, false);
    }

    /**
     * 绑定队列到 fanout 交换机
     */
    @Bean
    public Binding websocketBinding(FanoutExchange websocketExchange, Queue websocketQueue) {
        return BindingBuilder.bind(websocketQueue).to(websocketExchange);
    }

    /**
     * JSON 消息转换器（消息格式为 JSON）
     */
    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

}
