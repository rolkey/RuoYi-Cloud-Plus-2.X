package org.dromara.common.websocket.holder;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * WebSocketSession 用于保存当前所有在线的会话信息
 * <p>
 * 同一用户（sessionKey）可能同时存在多个连接（例如同一账号在多个浏览器/标签页登录），
 * 因此采用「一用户多会话」结构：Map&lt;userId, Set&lt;WebSocketSession&gt;&gt;。
 *
 * @author zendwang
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class WebSocketSessionHolder {

    private static final Map<Long, Set<WebSocketSession>> USER_SESSION_MAP = new ConcurrentHashMap<>();

    /**
     * 将 WebSocket 会话添加到用户会话 Map 中
     * <p>
     * 同一用户允许多个会话并存，新会话不会踢掉旧会话。
     *
     * @param sessionKey 会话键，用于检索会话
     * @param session    要添加的 WebSocket 会话
     */
    public static void addSession(Long sessionKey, WebSocketSession session) {
        USER_SESSION_MAP.computeIfAbsent(sessionKey, key -> ConcurrentHashMap.newKeySet()).add(session);
    }

    /**
     * 移除指定用户下的指定会话（按 sessionId 精确移除）
     *
     * @param sessionKey 会话键
     * @param sessionId  要移除的会话 id
     */
    public static void removeSession(Long sessionKey, String sessionId) {
        Set<WebSocketSession> sessions = USER_SESSION_MAP.get(sessionKey);
        if (sessions == null) {
            return;
        }
        sessions.removeIf(session -> sessionId != null && sessionId.equals(session.getId()));
        if (sessions.isEmpty()) {
            USER_SESSION_MAP.remove(sessionKey, sessions);
        }
    }

    /**
     * 从用户会话 Map 中移除并关闭指定会话键对应的全部会话
     *
     * @param sessionKey 要移除的会话键
     */
    public static void removeSession(Long sessionKey) {
        Set<WebSocketSession> sessions = USER_SESSION_MAP.remove(sessionKey);
        if (sessions == null) {
            return;
        }
        for (WebSocketSession session : sessions) {
            try {
                session.close(CloseStatus.BAD_DATA);
            } catch (Exception ignored) {
            }
        }
    }

    /**
     * 根据会话键从用户会话 Map 中获取该用户的全部会话
     *
     * @param sessionKey 要获取的会话键
     * @return 与给定会话键对应的全部 WebSocket 会话，不存在时返回空集合
     */
    public static Set<WebSocketSession> getSessions(Long sessionKey) {
        Set<WebSocketSession> sessions = USER_SESSION_MAP.get(sessionKey);
        return sessions == null ? Collections.emptySet() : sessions;
    }

    /**
     * 获取存储在用户会话 Map 中所有会话键（userId）集合
     *
     * @return 所有会话键集合
     */
    public static Set<Long> getSessionsAll() {
        return USER_SESSION_MAP.keySet();
    }

    /**
     * 检查给定的会话键是否还存在活跃会话
     *
     * @param sessionKey 要检查的会话键
     * @return 如果存在对应的会话，则返回 true；否则返回 false
     */
    public static Boolean existSession(Long sessionKey) {
        Set<WebSocketSession> sessions = USER_SESSION_MAP.get(sessionKey);
        return sessions != null && !sessions.isEmpty();
    }
}
