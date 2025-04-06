package com.ncy.y_comment.utils;

import cn.hutool.core.util.RandomUtil;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.util.Random;

public class PasswordEncoder {
    public static String encode(String password) {
        String salt = RandomUtil.randomString(20);
        return encode(password,salt);
    }
    private static String encode(String password, String salt) {
        // 加密
        return salt + "@" + DigestUtils.md5DigestAsHex((password + salt).getBytes(StandardCharsets.UTF_8));
    }

    public static Boolean validate(String password, String encodedPassword) {
        if (password == null || encodedPassword == null) {
            return false;
        }
        if(!encodedPassword.contains("@")){
            throw new RuntimeException("密码格式不正确！The password format is incorrect!");
        }

        String[] arr = encodedPassword.split("@");
        String salt = arr[0];
        return password.equals(encode(password,salt));
    }
}
