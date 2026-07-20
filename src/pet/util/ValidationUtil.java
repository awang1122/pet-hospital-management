package pet.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * 输入校验工具类
 */
public class ValidationUtil {

    private ValidationUtil() {}

    /** 校验手机号（中国大陆 11 位） */
    public static boolean isValidPhone(String phone) {
        return phone != null && phone.matches("^1[3-9]\\d{9}$");
    }

    /** 校验日期格式 yyyy-MM-dd */
    public static boolean isValidDate(String dateStr) {
        if (dateStr == null || dateStr.isEmpty()) return false;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            sdf.setLenient(false);
            sdf.parse(dateStr);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    /** 校验日期时间格式 yyyy-MM-dd HH:mm */
    public static boolean isValidDateTime(String dateTimeStr) {
        if (dateTimeStr == null || dateTimeStr.isEmpty()) return false;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            sdf.setLenient(false);
            sdf.parse(dateTimeStr);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    /** 校验体重（0 ~ 200 kg 合理范围） */
    public static boolean isValidWeight(double weight) {
        return weight >= 0 && weight <= 200;
    }

    /** 校验非空字符串 */
    public static boolean isNotEmpty(String str) {
        return str != null && !str.trim().isEmpty();
    }

    /** 校验字符串长度 */
    public static boolean isWithinLength(String str, int maxLen) {
        return str == null || str.length() <= maxLen;
    }
}
