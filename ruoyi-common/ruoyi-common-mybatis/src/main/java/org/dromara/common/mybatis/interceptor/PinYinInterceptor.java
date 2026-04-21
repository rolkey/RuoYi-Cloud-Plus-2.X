package org.dromara.common.mybatis.interceptor;

import com.baomidou.mybatisplus.extension.plugins.inner.InnerInterceptor;
import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.util.HashSet;
import java.util.Set;

public class PinYinInterceptor implements InnerInterceptor {

    private static final HanyuPinyinOutputFormat DEFAULT_FORMAT = createFormat();
    private static final char[] WB_CODE = "ggyyyykkkk".toCharArray();

    private static HanyuPinyinOutputFormat createFormat() {
        HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
        format.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        format.setVCharType(HanyuPinyinVCharType.WITH_V);
        return format;
    }

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
        Set<String> fieldsToCheck = getFieldsToCheck(entity.getClass());
        if (fieldsToCheck.isEmpty()) {
            return;
        }
        String[] sourceFields = {"name", "title", "nickName", "realName"};
        for (String fieldName : fieldsToCheck) {
            try {
                Field field = findField(entity.getClass(), fieldName);
                if (field != null) {
                    field.setAccessible(true);
                    Object value = field.get(entity);
                    if (value instanceof String str && str.isEmpty()) {
                        String sourceValue = getSourceFieldValue(entity, sourceFields);
                        if (sourceValue != null && !sourceValue.isEmpty()) {
                            if ("spellCode".equals(fieldName)) {
                                field.set(entity, getPinYinCode(sourceValue));
                            } else if ("strokeCode".equals(fieldName)) {
                                field.set(entity, getWubiCode(sourceValue));
                            }
                        }
                    }
                }
            } catch (Exception ignored) {
            }
        }
    }

    private String getSourceFieldValue(Object entity, String[] sourceFields) {
        for (String fieldName : sourceFields) {
            try {
                Field field = findField(entity.getClass(), fieldName);
                if (field != null) {
                    field.setAccessible(true);
                    Object value = field.get(entity);
                    if (value instanceof String str && !str.isEmpty()) {
                        return str;
                    }
                }
            } catch (Exception ignored) {
            }
        }
        return null;
    }

    private Set<String> getFieldsToCheck(Class<?> clazz) {
        Set<String> fields = new HashSet<>();
        for (Field f : clazz.getDeclaredFields()) {
            String name = f.getName();
            if ("spellCode".equals(name) || "strokeCode".equals(name)) {
                fields.add(name);
            }
        }
        return fields;
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

    private String getPinYinCode(String Chinese) throws BadHanyuPinyinOutputFormatCombination {
        if (Chinese == null || Chinese.isEmpty()) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        for (char c : Chinese.toCharArray()) {
            String[] pinyins = PinyinHelper.toHanyuPinyinStringArray(c, DEFAULT_FORMAT);
            if (pinyins != null && pinyins.length > 0) {
                sb.append(pinyins[0].charAt(0));
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    private String getWubiCode(String Chinese) {
        if (Chinese == null || Chinese.isEmpty()) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        for (char c : Chinese.toCharArray()) {
            if (c < 0x4E00 || c > 0x9FA5) {
                sb.append(c);
            } else {
                int row = (c - 0x4E00) / 94;
                int col = (c - 0x4E00) % 94;
                int code = (row / 10) * 10 + (col / 10);
                if (code < 25) {
                    sb.append(WB_CODE[code]);
                } else {
                    sb.append((char) ('a' + code - 25));
                }
            }
        }
        return sb.toString();
    }
}
