package org.dromara.system.controller.system;

import lombok.RequiredArgsConstructor;
import org.dromara.common.core.domain.R;
import org.dromara.common.core.utils.PinyinUtils;
import org.dromara.common.web.core.BaseController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/pinyin")
public class SysPinyinController extends BaseController {

    @GetMapping("/{text}")
    public R<PinyinResult> encode(@PathVariable String text) throws Exception {
        if (text == null || text.isEmpty()) {
            return R.ok(new PinyinResult(null, null));
        }
        String pinYin = PinyinUtils.getPinYinCode(text);
        String wubi = PinyinUtils.getWubiCode(text);
        return R.ok(new PinyinResult(pinYin, wubi));
    }

    public record PinyinResult(String pinYin, String wubi) {
    }
}
