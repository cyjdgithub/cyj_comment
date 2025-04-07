package com.ncy.y_comment.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.RandomUtil;
import com.ncy.y_comment.dto.LoginFormDTO;
import com.ncy.y_comment.dto.UserDTO;
import com.ncy.y_comment.entity.User;
import com.ncy.y_comment.service.IAuthService;
import com.ncy.y_comment.service.IUserService;
import com.ncy.y_comment.utils.RedisConstants;
import com.ncy.y_comment.utils.RegexUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements IAuthService {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private IUserService userService;

    @Override
    public UserDTO authenticate(LoginFormDTO loginFormDTO) {
        String phone = loginFormDTO.getPhone();
        String code = loginFormDTO.getCode();

        if (!RegexUtils.isPhoneValid(phone)) {
            throw new RuntimeException("手机号格式不合法");
        }

        String cacheCode = stringRedisTemplate.opsForValue().get(RedisConstants.LOGIN_CODE_KEY + phone);
        if (cacheCode == null || !cacheCode.equals(code)) {
            throw new RuntimeException("验证码错误");
        }

        User user = userService.query().eq("phone", phone).one();
        if (user == null) {
            user = new User();
            user.setPhone(phone);
            user.setNickname(RedisConstants.USER_NICK_NAME_PREFIX + RandomUtil.randomString(10));
            userService.save(user);
        }

        return BeanUtil.copyProperties(user, UserDTO.class);

    }
}
