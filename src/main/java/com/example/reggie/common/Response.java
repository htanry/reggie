package com.example.reggie.common;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * 通用返回结果，服务端相应的数据最终都会封装成此类型的对象
 * @param <T>
 */
@Data
public class Response<T> {
    private Integer code;   //响应码：1表示成功，否则为失败
    private T data; //响应数据，存储实体对象
    private String msg; //错误信息
    private Map map = new HashMap();    //动态数据

    public static <T> Response<T> success(T object){
        Response<T> response = new Response<>();
        response.code = 1;
        response.data = object;
        return response;
    }

    public static <T> Response<T> error(String msg){
        Response<T> response = new Response<>();
        response.code = 0;
        response.msg = msg;
        return response;
    }

    public Response<T> add(String key, Object value){
        this.map.put(key, value);
        return this;
    }
}
