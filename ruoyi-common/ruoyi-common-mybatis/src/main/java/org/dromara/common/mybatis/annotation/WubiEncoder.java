package org.dromara.common.mybatis.annotation;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface WubiEncoder {

    String target();

    String source();
}