package com.ncy.y_comment.utils;

import cn.hutool.core.util.StrUtil;

public class RegexUtils {

    private static boolean isMatch(String regex, String str) {
        if(StrUtil.isBlank(str)){
            return true;
        }
        return regex.matches(str);
    }

    public static boolean isPhoneValid(String phone) {
        return isMatch(phone,RegexPatterns.PHONE_REGEX);
    }

    public static boolean isEmailValid(String email) {
        return isMatch(email,RegexPatterns.EMAIL_REGEX);
    }

    public static boolean isCodeValid(String code) {
        return isMatch(code,RegexPatterns.VERIFY_CODE_REGEX);
    }


}
