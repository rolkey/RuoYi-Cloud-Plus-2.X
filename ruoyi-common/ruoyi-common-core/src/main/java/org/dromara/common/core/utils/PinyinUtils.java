package org.dromara.common.core.utils;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;

/**
 * 拼音五笔编码工具类
 *
 * @author Lion Li
 */
public class PinyinUtils {

    private static final HanyuPinyinOutputFormat DEFAULT_FORMAT = createFormat();
    private static final char[] WB_CODE = "ggyyyykkkk".toCharArray();

    private static HanyuPinyinOutputFormat createFormat() {
        HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
        format.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        format.setVCharType(HanyuPinyinVCharType.WITH_V);
        return format;
    }

    private PinyinUtils() {
    }

    /**
     * 获取拼音首字母码
     * 例如：中文 -> zg
     *
     * @param chinese 中文字符串
     * @return 拼音首字母码
     */
    public static String getPinYinCode(String chinese) throws Exception {
        if (StringUtils.isBlank(chinese)) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        for (char c : chinese.toCharArray()) {
            String[] pinyins = PinyinHelper.toHanyuPinyinStringArray(c, DEFAULT_FORMAT);
            if (pinyins != null && pinyins.length > 0) {
                sb.append(pinyins[0].charAt(0));
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    /**
     * 获取完整拼音码
     * 例如：中文 -> zhongwen
     *
     * @param chinese 中文字符串
     * @return 完整拼音码
     */
    public static String getFullPinYinCode(String chinese) throws Exception {
        if (StringUtils.isBlank(chinese)) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        for (char c : chinese.toCharArray()) {
            String[] pinyins = PinyinHelper.toHanyuPinyinStringArray(c, DEFAULT_FORMAT);
            if (pinyins != null && pinyins.length > 0) {
                sb.append(pinyins[0]);
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    /**
     * 获取五笔码
     *
     * @param chinese 中文字符串
     * @return 五笔码
     */
    public static String getWubiCode(String chinese) {
        if (StringUtils.isBlank(chinese)) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        for (char c : chinese.toCharArray()) {
            if (c < 0x4E00 || c > 0x9FA5) {
                // 非汉字直接拼接
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
