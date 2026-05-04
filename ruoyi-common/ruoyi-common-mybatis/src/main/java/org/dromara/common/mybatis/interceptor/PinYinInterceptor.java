package org.dromara.common.mybatis.interceptor;

import com.baomidou.mybatisplus.extension.plugins.inner.InnerInterceptor;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.dromara.common.core.utils.PinyinUtils;
import org.dromara.common.mybatis.annotation.PinyinEncoder;
import org.dromara.common.mybatis.annotation.WubiEncoder;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PinYinInterceptor implements InnerInterceptor {

    private static final Map<Class<?>, EncoderConfig> CACHE = new ConcurrentHashMap<>();

    @Override
    public void beforePrepare(StatementHandler sh, Connection cn, Integer transactionTimeout) {
        MetaObject metaObject = SystemMetaObject.forObject(sh);
        MappedStatement ms = (MappedStatement) metaObject.getValue("delegate.mappedStatement");
        if (!"insert".equalsIgnoreCase(ms.getSqlCommandType().name())) {
            return;
        }
        BoundSql boundSql = sh.getBoundSql();
        Object parameterObject = boundSql.getParameterObject();
        if (parameterObject != null) {
            handleEntity(parameterObject);
        }
    }

    private void handleEntity(Object entity) {
        EncoderConfig config = getEncoderConfig(entity.getClass());
        if (config == null || config.isEmpty()) {
            return;
        }
        for (EncoderField ef : config.fields) {
            try {
                Field targetField = findField(entity.getClass(), ef.target);
                Field sourceField = findField(entity.getClass(), ef.source);
                if (targetField == null || sourceField == null) {
                    continue;
                }
                targetField.setAccessible(true);
                sourceField.setAccessible(true);
                Object targetValue = targetField.get(entity);
                if (targetValue instanceof String str && str.isEmpty()) {
                    Object sourceValue = sourceField.get(entity);
                    if (sourceValue instanceof String source && !source.isEmpty()) {
                        if (ef.type == EncoderType.PINYIN) {
                            targetField.set(entity, PinyinUtils.getPinYinCode(source));
                        } else if (ef.type == EncoderType.WUBI) {
                            targetField.set(entity, PinyinUtils.getWubiCode(source));
                        }
                    }
                }
            } catch (Exception ignored) {
            }
        }
    }

    private EncoderConfig getEncoderConfig(Class<?> clazz) {
        return CACHE.computeIfAbsent(clazz, k -> {
            PinyinEncoder pinyin = clazz.getAnnotation(PinyinEncoder.class);
            WubiEncoder wubi = clazz.getAnnotation(WubiEncoder.class);
            if (pinyin == null && wubi == null) {
                return null;
            }
            EncoderConfig config = new EncoderConfig();
            if (pinyin != null) {
                config.fields.add(new EncoderField(EncoderType.PINYIN, pinyin.target(), pinyin.source()));
            }
            if (wubi != null) {
                config.fields.add(new EncoderField(EncoderType.WUBI, wubi.target(), wubi.source()));
            }
            return config;
        });
    }

    private Field findField(Class<?> clazz, String fieldName) {
        try {
            return clazz.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            Class<?> superClass = clazz.getSuperclass();
            if (superClass != null) {
                return findField(superClass, fieldName);
            }
            return null;
        }
    }

    private enum EncoderType {
        PINYIN, WUBI
    }

    private static class EncoderField {
        final EncoderType type;
        final String target;
        final String source;

        EncoderField(EncoderType type, String target, String source) {
            this.type = type;
            this.target = target;
            this.source = source;
        }
    }

    private static class EncoderConfig {
        final List<EncoderField> fields = new ArrayList<>();

        boolean isEmpty() {
            return fields.isEmpty();
        }
    }
}