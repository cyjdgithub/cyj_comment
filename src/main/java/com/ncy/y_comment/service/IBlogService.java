package com.ncy.y_comment.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ncy.y_comment.dto.Result;
import com.ncy.y_comment.entity.Blog;
import static com.ncy.y_comment.utils.RedisConstants.BLOG_LIKED_KEY;
import static com.ncy.y_comment.utils.RedisConstants.FEED_KEY;

public interface IBlogService extends IService<Blog> {
    Result queryById(Integer id);

    Result queryHotBlog(Integer current);

    Result likeBlog(Long id);

    Result queryBlogLikes(Integer id);

    Result saveBlog(Blog blog);

    Result queryBlogOfFollow(Long max, Integer offset);
}
