package com.ncy.y_comment.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ncy.y_comment.dto.LoginFormDTO;
import com.ncy.y_comment.dto.Result;
import com.ncy.y_comment.dto.UserDTO;
import com.ncy.y_comment.entity.User;
import com.ncy.y_comment.mapper.UserMapper;
import com.ncy.y_comment.service.IUserService;
import com.ncy.y_comment.utils.RegexUtils;
import com.ncy.y_comment.utils.UserHolder;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.MessagingException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.ncy.y_comment.utils.RedisConstants.*;
import static com.ncy.y_comment.utils.SystemConstants.USER_NICK_NAME_PREFIX;

@Service
@Transactional
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {


    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public Result sendCode(String phone, HttpSession session) throws MessagingException {

        //校验手机号 符不符合
        if(!RegexUtils.isPhoneValid(phone)){
            return Result.fail("手机号格式错误");
        }

        //生成验证码
        String code = RandomUtil.randomNumbers(6);


        //保存到session
        //session.setAttribute("code", code);
        stringRedisTemplate.opsForValue().set(LOGIN_CODE_KEY+ phone,code,LOGIN_CODE_TTL, TimeUnit.MINUTES);

        //发送验证码
        log.debug("发送验证码成功，验证码"+ code);  // 这通常是对的，前提是框架支持


        //保存
        return Result.ok();
    }

    @Override
    public Result login(LoginFormDTO loginFormDTO, HttpSession session) throws MessagingException {
        //校验手机号
        String phone = loginFormDTO.getPhone();

        if(!RegexUtils.isPhoneValid(phone)){
            return Result.fail("手机号格式错误");
        }

        //校验验证码(从redis获取验证码并校验)

        String cacheCode = stringRedisTemplate.opsForValue().get(LOGIN_CODE_KEY + phone);

        String code = loginFormDTO.getCode();

        if(!code.equals(cacheCode) || cacheCode == null){
            return Result.fail("验证码错误");
        }


        //一致，查询用户

        User user = query().eq("phone", phone).one();

        //判断是否存在
        if(user == null){
            //不存在，创建新用户并保存
            user = createUserWithPhone(phone);
        }

        //保存用户信息到redis

        //随机生成token，作为登录令牌
        String token = UUID.randomUUID().toString();

        //将user对象转为Hash存储
        UserDTO userDTO = BeanUtil.copyProperties(user, UserDTO.class);

        HashMap<String, String > userMap = new HashMap<>();
        userMap.put("id", String.valueOf(userDTO.getId()));
        userMap.put("nickName", userDTO.getNickname());
        userMap.put("icon", userDTO.getIcon());
        System.out.println(userMap);

        /*Map<String, Object> userMap = BeanUtil.beanToMap(userDTO);
        userMap.put("id", String.valueOf(userDTO.getId()));
        userMap.put("nickName", userDTO.getNickname());
        userMap.put("icon", userDTO.getIcon());
        System.out.println(userMap);*/

        //存储
        String tokenKey = LOGIN_USER_KEY+token;
        stringRedisTemplate.opsForHash().putAll(tokenKey,userMap);

        //设置有效期
        stringRedisTemplate.expire(tokenKey,LOGIN_USER_TTL, TimeUnit.MINUTES);


        //返回token给客户端



        //存在保存
        //session.setAttribute("user", BeanUtil.copyProperties(user, UserDTO.class));

        return Result.ok(token);


    }

    private User createUserWithPhone(String phone) {
        User user = new User();
        user.setPhone(phone);
        user.setNickname(USER_NICK_NAME_PREFIX+RandomUtil.randomString(10));
        save(user);
        return user;
    }

    @Override
    public Result sign() {
        /*Long userId = UserHolder.getUser().getId();
        LocalDateTime now = LocalDateTime.now();

        String keySuffix = now.format(DateTimeFormatter.ofPattern(":yyyyMM"));
        String key = USER_SIGN_KEY + userId + keySuffix;

        int dayOfMonth = now.getDayOfMonth();

        stringRedisTemplate.opsForValue().setBit(key,dayOfMonth-1,true);
        return Result.ok();*/
        return null;
    }

    @Override
    public Result signCount() {
        return null;
    }
}
