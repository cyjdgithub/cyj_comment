package com.ncy.y_comment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Result {
    private Boolean success;
    private String errmsg;
    private Object data;
    private Long total;

    public static Result ok(){
        return new Result(true,null,null,null);
    }
    public static Result ok(Object data){
        return new Result(true,null,data,null);
    }
    public static Result ok(List<?> data, Long total){
        return new Result(true,null,data,total);
    }
    public static Result fail(String errmsg){
        return new Result(false,errmsg,null,null);
    }
}
