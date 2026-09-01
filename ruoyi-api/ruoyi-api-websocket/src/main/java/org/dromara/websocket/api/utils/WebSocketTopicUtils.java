package org.dromara.websocket.api.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * 实时消息主题工具类
 *
 * @author ruoyi
 */
public final class WebSocketTopicUtils {

    private static final String SEPARATOR = "/";

    private static final Pattern SEGMENT_PATTERN = Pattern.compile("^[A-Za-z0-9_-]+$");

    private WebSocketTopicUtils() {
    }

    /**
     * 拼接三级主题：{主类}/{子类}/{具体业务ID}
     *
     * @param module   主类（模块）
     * @param business 子类（业务）
     * @param id       具体业务ID
     * @return 主题
     */
    public static String build(String module, String business, String id) {
        return String.join(SEPARATOR, module, business, id);
    }

    /**
     * 拼接二级主题：{主类}/{子类}
     *
     * @param module   主类（模块）
     * @param business 子类（业务）
     * @return 主题
     */
    public static String build(String module, String business) {
        return String.join(SEPARATOR, module, business);
    }

    /**
     * 校验主题合法性：1~3 段，每段非空且仅含 [A-Za-z0-9_-]
     *
     * @param topic 主题
     * @return 是否合法
     */
    public static boolean validate(String topic) {
        if (topic == null || topic.isBlank()) {
            return false;
        }
        String[] segments = topic.split(SEPARATOR, -1);
        if (segments.length < 1 || segments.length > 3) {
            return false;
        }
        for (String segment : segments) {
            if (segment == null || segment.isBlank() || !SEGMENT_PATTERN.matcher(segment).matches()) {
                return false;
            }
        }
        return true;
    }

    /**
     * 生成消息主题的全部前缀（用于订阅前缀匹配）
     * <p>
     * 例如 his/order/10086 -> [his, his/order, his/order/10086]
     *
     * @param topic 主题
     * @return 前缀列表
     */
    public static List<String> prefixes(String topic) {
        List<String> result = new ArrayList<>();
        if (topic == null || topic.isBlank()) {
            return result;
        }
        String[] segments = topic.split(SEPARATOR, -1);
        StringBuilder sb = new StringBuilder();
        for (String segment : segments) {
            if (segment == null || segment.isEmpty()) {
                break;
            }
            if (sb.length() > 0) {
                sb.append(SEPARATOR);
            }
            sb.append(segment);
            result.add(sb.toString());
        }
        return result;
    }

}
