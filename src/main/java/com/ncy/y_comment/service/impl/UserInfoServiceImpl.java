package com.ncy.y_comment.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ncy.y_comment.entity.UserInfo;
import com.ncy.y_comment.mapper.UserInfoMapper;
import com.ncy.y_comment.service.IUserInfoService;
import org.springframework.stereotype.Service;

@Service
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo> implements IUserInfoService {
}
