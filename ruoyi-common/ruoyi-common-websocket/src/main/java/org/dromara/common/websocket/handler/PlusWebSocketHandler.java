package org.dromara.common.websocket.handler;

import cn.hutool.core.util.ObjectUtil;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.websocket.holder.WebSocketSessionHolder;
import org.dromara.common.websocket.service.WebSocketSubscribeService;
import org.dromara.common.websocket.utils.WebSocketUtils;
import org.dromara.system.api.model.LoginUser;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.PongMessage;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;
import org.springframework.web.socket.handler.ConcurrentWebSocketSessionDecorator;

import java.io.IOException;

import static org.dromara.common.websocket.constant.WebSocketConstants.LOGIN_USER_KEY;

/**
 * WebSocketHandler 实现类
 *
 * @author zendwang
 */
@Slf4j
public class PlusWebSocketHandler extends AbstractWebSocketHandler {

    private final WebSocketSubscribeService subscribeService;

    public PlusWebSocketHandler(WebSocketSubscribeService subscribeService) {
        this.subscribeService = subscribeService;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws IOException {
        LoginUser loginUser = (LoginUser) session.getAttributes().get(LOGIN_USER_KEY);
        if (ObjectUtil.isNull(loginUser)) {
            session.close(CloseStatus.BAD_DATA);
            log.info("[connect] invalid token received. sessionId: {}", session.getId());
            return;
        }
        WebSocketSessionHolder.addSession(loginUser.getUserId(), new ConcurrentWebSocketSessionDecorator(session, 10 * 1000, 64000));
        log.info("[connect] sessionId: {},userId:{},userType:{}", session.getId(), loginUser.getUserId(), loginUser.getUserType());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        // 回显收到的文本消息（用于前端心跳保活）
        WebSocketUtils.sendMessage(session, message.getPayload());
    }

    @Override
    protected void handlePongMessage(WebSocketSession session, PongMessage message) throws Exception {
        WebSocketUtils.sendPongMessage(session);
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        log.error("[transport error] sessionId: {} , exception:{}", session.getId(), exception.getMessage());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        LoginUser loginUser = (LoginUser) session.getAttributes().get(LOGIN_USER_KEY);
        if (ObjectUtil.isNull(loginUser)) {
            log.info("[disconnect] invalid token received. sessionId: {}", session.getId());
            return;
        }
        WebSocketSessionHolder.removeSession(loginUser.getUserId());
        subscribeService.cleanup(loginUser.getUserId());
        log.info("[disconnect] sessionId: {},userId:{},userType:{}", session.getId(), loginUser.getUserId(), loginUser.getUserType());
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }
}
