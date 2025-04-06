package com.ncy.y_comment.utils;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.function.ToIntBiFunction;

@Data
public class RedisData<T> {
    private LocalDateTime expireTime;
    private T data;
}
