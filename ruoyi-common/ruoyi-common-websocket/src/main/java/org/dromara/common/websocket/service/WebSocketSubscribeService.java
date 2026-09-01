package org.dromara.common.websocket.service;

import org.dromara.common.redis.utils.RedisUtils;
import org.dromara.websocket.api.utils.WebSocketTopicUtils;
import org.redisson.api.RSet;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * WebSocket 订阅关系服务
 * <p>
 * 维护 Redis 双向索引：
 * <ul>
 *   <li>ws:sub:{topic} -> Set&lt;userId&gt;（正向索引，路由用）</li>
 *   <li>ws:userSub:{userId} -> Set&lt;topic&gt;（反向索引，断开清理用）</li>
 * </ul>
 *
 * @author ruoyi
 */
public class WebSocketSubscribeService {

    private static final String SUB_PREFIX = "ws:sub:";

    private static final String USER_SUB_PREFIX = "ws:userSub:";

    /**
     * 订阅主题
     *
     * @param userId 用户 id
     * @param types  主题列表
     */
    public void subscribe(Long userId, List<String> types) {
        if (userId == null || types == null || types.isEmpty()) {
            return;
        }
        for (String type : types) {
            if (WebSocketTopicUtils.validate(type)) {
                RSet<Long> subSet = RedisUtils.getClient().getSet(SUB_PREFIX + type);
                subSet.add(userId);
                RSet<String> userSubSet = RedisUtils.getClient().getSet(USER_SUB_PREFIX + userId);
                userSubSet.add(type);
            }
        }
    }

    /**
     * 退订主题
     *
     * @param userId 用户 id
     * @param types  主题列表
     */
    public void unsubscribe(Long userId, List<String> types) {
        if (userId == null || types == null || types.isEmpty()) {
            return;
        }
        for (String type : types) {
            RSet<Long> subSet = RedisUtils.getClient().getSet(SUB_PREFIX + type);
            subSet.remove(userId);
            if (subSet.isEmpty()) {
                subSet.delete();
            }
            RSet<String> userSubSet = RedisUtils.getClient().getSet(USER_SUB_PREFIX + userId);
            userSubSet.remove(type);
        }
    }

    /**
     * 查询用户已订阅的全部主题
     *
     * @param userId 用户 id
     * @return 主题集合
     */
    public Set<String> getSubscription(Long userId) {
        if (userId == null) {
            return new HashSet<>();
        }
        RSet<String> userSubSet = RedisUtils.getClient().getSet(USER_SUB_PREFIX + userId);
        return userSubSet.readAll();
    }

    /**
     * 查询订阅了指定主题（含前缀）的全部用户
     *
     * @param topic 主题
     * @return 用户 id 集合
     */
    public Set<Long> getSubscribedUsers(String topic) {
        Set<Long> users = new HashSet<>();
        for (String prefix : WebSocketTopicUtils.prefixes(topic)) {
            RSet<Long> subSet = RedisUtils.getClient().getSet(SUB_PREFIX + prefix);
            users.addAll(subSet.readAll());
        }
        return users;
    }

    /**
     * 清理用户的全部订阅（断开时调用）
     *
     * @param userId 用户 id
     */
    public void cleanup(Long userId) {
        if (userId == null) {
            return;
        }
        RSet<String> userSubSet = RedisUtils.getClient().getSet(USER_SUB_PREFIX + userId);
        Set<String> topics = userSubSet.readAll();
        for (String topic : topics) {
            RSet<Long> subSet = RedisUtils.getClient().getSet(SUB_PREFIX + topic);
            subSet.remove(userId);
        }
        userSubSet.delete();
    }

}
