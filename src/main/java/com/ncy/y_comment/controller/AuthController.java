package com.ncy.y_comment.controller;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.ncy.y_comment.dto.LoginFormDTO;
import com.ncy.y_comment.dto.Result;
import com.ncy.y_comment.dto.UserDTO;
import com.ncy.y_comment.service.IAuthService;
import com.ncy.y_comment.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;
import com.ncy.y_comment.utils.RedisConstants;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private IAuthService authService;

    @PostMapping("/login")
    public Result login(@RequestBody LoginFormDTO loginFormDTO){
        UserDTO userDTO = authService.authenticate(loginFormDTO);
        String accessToken = JwtUtil.createToken(userDTO,RedisConstants.ACCESS_TOKEN_TTL);
        String refreshToken = UUID.randomUUID().toString();
        redisTemplate.opsForValue().set(RedisConstants.REFRESH_TOKEN_KEY_PREFIX+refreshToken, JSONUtil.toJsonStr(userDTO),RedisConstants.REFRESH_TOKEN_TTL, TimeUnit.SECONDS);


        Map<String,String> tokens = new HashMap<>();
        tokens.put("access_token",accessToken);
        tokens.put("refresh_token",refreshToken);
        return Result.ok(tokens);
    }

    @PostMapping("/refresh")
    public Result refresh(@RequestParam String refreshToken){
        String key = RedisConstants.REFRESH_TOKEN_KEY_PREFIX+refreshToken;
        String json = redisTemplate.opsForValue().get(key);
        if(StrUtil.isBlank(json)){
            return Result.fail("Refresh token expired");
        }

        UserDTO userDTO = JSONUtil.toBean(json,UserDTO.class);
        String newAccessToken = JwtUtil.createToken(userDTO,RedisConstants.ACCESS_TOKEN_TTL);
        String newRefreshToken = UUID.randomUUID().toString();
        redisTemplate.delete(key);
        redisTemplate.opsForValue().set(RedisConstants.REFRESH_TOKEN_KEY_PREFIX+newRefreshToken,JSONUtil.toJsonStr(userDTO),RedisConstants.REFRESH_TOKEN_TTL,TimeUnit.SECONDS);
        Map<String, String> tokens = new HashMap<>();
        tokens.put("access_token", newAccessToken);
        tokens.put("refresh_token", newRefreshToken);
        return Result.ok(tokens);
    }

    @PostMapping("/logout")
    public Result logout(@RequestParam String refreshToken) {
        redisTemplate.delete(RedisConstants.REFRESH_TOKEN_KEY_PREFIX + refreshToken);
        return Result.ok();
    }


}
