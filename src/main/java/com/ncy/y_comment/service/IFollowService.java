package com.ncy.y_comment.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ncy.y_comment.dto.Result;
import com.ncy.y_comment.entity.Follow;
import com.ncy.y_comment.entity.Voucher;

public interface IFollowService extends IService<Follow> {

    Result isFollow(Long followUserId);

    Result follow(Long followUserId, Boolean isFellow);

    Result followCommons(Long id);
}
