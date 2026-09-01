package org.dromara.common.websocket.controller;

import lombok.RequiredArgsConstructor;
import org.dromara.common.core.domain.R;
import org.dromara.common.satoken.utils.LoginHelper;
import org.dromara.common.websocket.service.WebSocketSubscribeService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;

/**
 * WebSocket 订阅接口
 *
 * @author ruoyi
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/websocket")
public class WebSocketSubscribeController {

    private final WebSocketSubscribeService subscribeService;

    /**
     * 订阅主题
     *
     * @param types 主题列表（1~3 级）
     */
    @PostMapping("/subscribe")
    public R<Void> subscribe(@RequestBody List<String> types) {
        subscribeService.subscribe(LoginHelper.getUserId(), types);
        return R.ok();
    }

    /**
     * 退订主题
     *
     * @param types 主题列表（1~3 级）
     */
    @PostMapping("/unsubscribe")
    public R<Void> unsubscribe(@RequestBody List<String> types) {
        subscribeService.unsubscribe(LoginHelper.getUserId(), types);
        return R.ok();
    }

    /**
     * 查询当前用户订阅列表
     */
    @GetMapping("/subscription")
    public R<Set<String>> subscription() {
        return R.ok(subscribeService.getSubscription(LoginHelper.getUserId()));
    }

}
