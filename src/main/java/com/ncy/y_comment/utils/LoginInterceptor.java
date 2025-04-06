package com.ncy.y_comment.utils;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.ncy.y_comment.dto.UserDTO;
import com.ncy.y_comment.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Map;
import java.util.concurrent.TimeUnit;

public class LoginInterceptor implements HandlerInterceptor {


    private StringRedisTemplate stringRedisTemplate;

    /*    private StringRedisTemplate redisTemplate;

        public LoginInterceptor(StringRedisTemplate redisTemplate) {
            this.redisTemplate = redisTemplate;
        }*/
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {


        if (UserHolder.getUser() == null) {
            response.setStatus(401);
            return false;
        }
        return true;
    }

        /*String token = request.getHeader("authorization");
        if(StrUtil.isBlank(token)) {
            *//*response.setStatus(401);
            return false;*//*
            return true;
        }

        //基于token获取redis中的用户
        String key = RedisConstants.LOGIN_USER_KEY+token;
        Map<Object,Object> userMap = redisTemplate.opsForHash().entries(key);
        if(userMap.isEmpty()) {
            *//*response.setStatus(401);
            return false;*//*
            return true;
        }
        //转换为DTO对象
        UserDTO userDTO = BeanUtil.fillBeanWithMap(userMap, new UserDTO(),false);
        //存在，保存到threadLocal中
        UserHolder.setUser(userDTO);

        //刷新token有效期
        redisTemplate.expire(key,RedisConstants.LOGIN_USER_TTL, TimeUnit.MINUTES);

        return true;


        *//* return HandlerInterceptor.super.preHandle(request, response, handler);
        //获取请求头中的token
        HttpSession session = request.getSession();
        //获取redis中的用户
        Object user = session.getAttribute("user");
        if (user == null) {
            response.setStatus(401);
            return false;
        }

        //将查询到的hash数据转为dto

        UserHolder.setUser((UserDTO) user);
        return true;*/
}

 /*   @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
//        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
        UserHolder.removeUser();
    }
}*/
