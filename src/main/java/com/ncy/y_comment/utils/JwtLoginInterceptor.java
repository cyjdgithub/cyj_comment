package com.ncy.y_comment.utils;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpStatus;
import com.ncy.y_comment.dto.UserDTO;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class JwtLoginInterceptor implements HandlerInterceptor {


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = request.getHeader("authorization");
        if(StrUtil.isBlank(token)){
            response.setStatus(HttpStatus.HTTP_UNAUTHORIZED);
            return false;
        }

        Claims claims;
        try {
            claims = JwtUtil.parseToken(token);
        } catch (Exception e){
            response.setStatus(HttpStatus.HTTP_UNAUTHORIZED);
            return false;
        }
        UserDTO userDTO = new UserDTO();
        userDTO.setId(Long.valueOf(claims.get("id").toString()));
        userDTO.setNickname(claims.get("nickname").toString());
        UserHolder.setUser(userDTO);
        return true;

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        UserHolder.removeUser();
    }
}
