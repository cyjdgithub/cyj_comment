package com.ncy.y_comment.service;

import com.ncy.y_comment.dto.LoginFormDTO;
import com.ncy.y_comment.dto.UserDTO;

public interface IAuthService {
    UserDTO authenticate(LoginFormDTO loginFormDTO);
}
