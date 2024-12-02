package cn.example.base.utils;

public class StringUtil {
    public static boolean isEmptyOrNull(String str) {
        if ("".equals(str) || null == str || "null".equals(str) || " ".equals(str)) {
            return true;
        }
        return false;
    }
}
