package com.ncy.y_comment.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ncy.y_comment.dto.Result;
import com.ncy.y_comment.dto.ScrollResult;
import com.ncy.y_comment.dto.UserDTO;
import com.ncy.y_comment.entity.Blog;
import com.ncy.y_comment.entity.Follow;
import com.ncy.y_comment.entity.User;
import com.ncy.y_comment.mapper.BlogMapper;
import com.ncy.y_comment.service.IBlogService;
import com.ncy.y_comment.service.IFollowService;
import com.ncy.y_comment.service.IUserService;
import com.ncy.y_comment.utils.SystemConstants;
import com.ncy.y_comment.utils.UserHolder;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.ncy.y_comment.utils.RedisConstants.BLOG_LIKED_KEY;
import static com.ncy.y_comment.utils.RedisConstants.FEED_KEY;

@Service
public class BlogServiceImpl extends ServiceImpl<BlogMapper, Blog> implements IBlogService {
    @Autowired
    private IUserService userService;

    @Autowired
    private IBlogService blogService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private IFollowService followService;


    private void queryBlogUser(Blog blog) {
        Long userId = blog.getUserId();
        User user = userService.getById(userId);
        blog.setName(user.getNickname());
        blog.setIcon(user.getIcon());
    }

    private void isBlogLiked(Blog blog) {
        UserDTO userDTO = UserHolder.getUser();
        if (userDTO == null) {
            return;
        }

        String key = BLOG_LIKED_KEY + blog.getId();
        Double score = redisTemplate.opsForZSet().score(key, userDTO.getId().toString());
        blog.setIsLike(score != null);
    }

    @Override
    public Result queryById(Integer id) {
        Blog blog = getById(id);
        if(blog == null){
            return Result.fail("does not exist");
        }
        queryBlogUser(blog);
        isBlogLiked(blog);
        return Result.ok(blog);

    }

    @Override
    public Result queryHotBlog(Integer current) {
        Page<Blog> page = query()
                .orderByDesc("liked")
                .page(new Page<>(current, SystemConstants.MAX_PAGE_SIZE));

        List<Blog> records = page.getRecords();
        records.forEach(blog -> {
            queryBlogUser(blog);
            isBlogLiked(blog);
        });
        return  Result.ok(records);
    }

    @Override
    public Result likeBlog(Long id) {
        Long userId = UserHolder.getUser().getId();
        String key = BLOG_LIKED_KEY + id;
        Double score = redisTemplate.opsForZSet().score(key,userId.toString());
        if(score == null){
            boolean success = update().setSql("liked = liked + 1").eq("id",id).update();
            if(success){
                redisTemplate.opsForZSet().add(key,userId.toString(),System.currentTimeMillis());
            }
        }else {
            boolean success = update().setSql("liked = liked - 1").eq("id",id).update();
            if(success){
                redisTemplate.opsForZSet().remove(key,userId.toString());
            }
        }
        return Result.ok();
    }

    @Override
    public Result queryBlogLikes(Integer id) {
        String key = BLOG_LIKED_KEY + id;
        Set<String> top5 = redisTemplate.opsForZSet().range(key,0,4);
        if(top5 == null || top5.isEmpty()){
            return Result.ok(Collections.EMPTY_LIST);
        }
        List<Long> ids = top5.stream().map(Long::valueOf).collect(Collectors.toList());
        String idsStr = StrUtil.join(",", ids);
        List<UserDTO> userDTOS = userService.query().in("id", ids)
                .last("order by field(id," + idsStr + ")")
                .list().stream()
                .map(user -> BeanUtil.copyProperties(user, UserDTO.class))
                .collect(Collectors.toList());
        return Result.ok(userDTOS);
    }

    @Override
    public Result saveBlog(Blog blog) {
        UserDTO user = UserHolder.getUser();
        blog.setUserId(user.getId());
        boolean isSuccess = blogService.save(blog);
        if(!isSuccess){
            return Result.fail("fail!");
        }
        List<Follow> followUsers = followService.query().eq("follow_user_id", user.getId()).list();
        for (Follow follow : followUsers) {
            Long userId = follow.getUserId();
            String key = FEED_KEY+ userId;
            //推送数据,每一个粉丝都有自己的收件箱
            redisTemplate.opsForZSet().add(key, blog.getId().toString(), System.currentTimeMillis());
        }
        return Result.ok(blog.getId());

    }

    @Override
    public Result queryBlogOfFollow(Long max, Integer offset) {
        Long userId = UserHolder.getUser().getId();
        String key = FEED_KEY + userId;
        Set<ZSetOperations.TypedTuple<String>> typeTuples = redisTemplate.opsForZSet()
                .reverseRangeByScoreWithScores(key, 0, max, offset, 2);
        if (typeTuples == null || typeTuples.isEmpty()){
            return Result.ok(Collections.emptyList());
        }
        ArrayList<Long> ids = new ArrayList<>(typeTuples.size());
        long minTime = 0;
        int os = 1;
        for (ZSetOperations.TypedTuple<String> typeTuple : typeTuples) {
            //4.1 获取id
            String id = typeTuple.getValue();
            ids.add(Long.valueOf(id));
            //4.2 获取score（时间戳）
            long time = typeTuple.getScore().longValue();
            if (time == minTime){
                os++;
            }else {
                minTime = time;
                os = 1;
            }
        }
        String idsStr = StrUtil.join(",");

        //5. 根据id查询blog
        List<Blog> blogs = query().in("id", ids).last("ORDER BY FIELD(id," + idsStr + ")").list();

        for (Blog blog : blogs) {
            //5.1 查询发布该blog的用户信息
            queryBlogUser(blog);
            //5.2 查询当前用户是否给该blog点过赞
            isBlogLiked(blog);
        }
        //6. 封装结果并返回
        ScrollResult scrollResult = new ScrollResult();
        scrollResult.setList(blogs);
        scrollResult.setOffset(os);
        scrollResult.setMinTime(minTime);
        return Result.ok(scrollResult);

    }
}
