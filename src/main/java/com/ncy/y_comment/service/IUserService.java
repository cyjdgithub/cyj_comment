package com.ncy.y_comment.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ncy.y_comment.dto.LoginFormDTO;
import com.ncy.y_comment.dto.Result;
import com.ncy.y_comment.entity.User;
import jakarta.servlet.http.HttpSession;

import javax.mail.MessagingException;

public interface IUserService extends IService<User> {
    Result sendCode(String phone, HttpSession session) throws MessagingException;
    Result login(LoginFormDTO loginFormDTO, HttpSession session) throws MessagingException;
    Result sign();
    Result signCount();
}
