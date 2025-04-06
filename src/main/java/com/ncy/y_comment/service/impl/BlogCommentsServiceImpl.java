package com.ncy.y_comment.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ncy.y_comment.entity.BlogComments;
import com.ncy.y_comment.mapper.BlogCommentsMapper;
import com.ncy.y_comment.service.IBlogCommentsService;
import org.springframework.stereotype.Service;

@Service
public class BlogCommentsServiceImpl extends ServiceImpl<BlogCommentsMapper, BlogComments> implements IBlogCommentsService {
}
