package com.ncy.y_comment.controller;


import cn.hutool.core.bean.BeanUtil;
import com.ncy.y_comment.dto.LoginFormDTO;
import com.ncy.y_comment.dto.Result;
import com.ncy.y_comment.dto.UserDTO;
import com.ncy.y_comment.entity.User;
import com.ncy.y_comment.entity.UserInfo;
import com.ncy.y_comment.service.IUserInfoService;
import com.ncy.y_comment.service.IUserService;
import com.ncy.y_comment.utils.UserHolder;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;

@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private IUserService userService;

    @Autowired
    private IUserInfoService userInfoService;

    @RequestMapping("code")
    public Result sendCode(@RequestParam("phone")String phone, HttpSession session) throws MessagingException {
        return userService.sendCode(phone,session);
    }

    @RequestMapping("/login")
    public Result login(@RequestBody LoginFormDTO loginFormDTO, HttpSession session) throws MessagingException {
        return userService.login(loginFormDTO,session);
    }

    @GetMapping("/me")
    public Result me(){
        //  获取当前登录的用户并返回
        UserDTO user = UserHolder.getUser();
        System.out.println(user);
        if (user == null) {
            return Result.fail("用户未登录"); // 用户未登录，返回错误
        }
        return Result.ok(user);
    }

    @GetMapping("/{id}")
    public Result queryById(@PathVariable("id") Long userId) {
        // 查询详情
        User user = userService.getById(userId);
        if (user == null) {
            // 没有详情，应该是第一次查看详情
            return Result.ok();
        }
        UserDTO userDTO = BeanUtil.copyProperties(user, UserDTO.class);
        // 返回
        return Result.ok(userDTO);
    }

    @RequestMapping("/info/{id}")
    public Result info(@PathVariable("id") Long userId) throws MessagingException {
        UserInfo userInfo = userInfoService.getById(userId);
        if(userInfo==null){
            return Result.ok();
        }
        userInfo.setCreateTime(null);
        userInfo.setUpdateTime(null);
        return Result.ok(userInfo);

    }
}
