package com.ncy.y_comment.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ncy.y_comment.dto.Result;
import com.ncy.y_comment.dto.UserDTO;
import com.ncy.y_comment.entity.Follow;
import com.ncy.y_comment.mapper.FollowMapper;
import com.ncy.y_comment.service.IFollowService;
import com.ncy.y_comment.service.IUserService;
import com.ncy.y_comment.utils.UserHolder;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class FollowServiceImpl extends ServiceImpl<FollowMapper, Follow> implements IFollowService {
    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private IUserService userService;

    @Override
    public Result isFollow(Long followUserId) {
        Long userId = UserHolder.getUser().getId();
        Long count = query().eq("user_id",userId).eq("follow_user_id",followUserId).count();
        return Result.ok(count>0);
    }

    @Override
    public Result follow(Long followUserId, Boolean isFellow) {
        Long userId = UserHolder.getUser().getId();
        String key = "follows:" + userId;

        if(isFellow){
            Follow follow = new Follow();
            follow.setUserId(userId);
            follow.setFollowUserId(followUserId);
            boolean successed = save(follow);
            if(successed){
                redisTemplate.opsForSet().add(key,followUserId.toString());
            }
        }else {
            boolean successed = remove(new QueryWrapper<Follow>().eq("user_id",userId).eq("follow_user_id",followUserId));
            if (successed){
                redisTemplate.opsForSet().remove(key,followUserId.toString());
            }
        }
        return Result.ok();
    }

    @Override
    public Result followCommons(Long id) {
        Long userId = UserHolder.getUser().getId();
        String key1 = "follows:" + id;
        String key2 = "follows:" + userId;
        Set<String> intersect = redisTemplate.opsForSet().intersect(key1,key2);
        if (intersect == null || intersect.isEmpty()) {
            //无交集就返回个空集合
            return Result.ok(Collections.emptyList());
        }

        List<Long> ids = intersect.stream().map(Long::valueOf).collect(Collectors.toList());

        List<UserDTO> userDTOS = userService.listByIds(ids).stream().map(user ->
                BeanUtil.copyProperties(user,UserDTO.class)).collect(Collectors.toList());

        return Result.ok(userDTOS);
    }
}
