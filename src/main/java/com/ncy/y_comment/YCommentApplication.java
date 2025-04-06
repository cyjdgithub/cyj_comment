package com.ncy.y_comment;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
@MapperScan("com.ncy.y_comment.mapper")
@SpringBootApplication
public class YCommentApplication {

    public static void main(String[] args) {
        SpringApplication.run(YCommentApplication.class, args);
    }

}
