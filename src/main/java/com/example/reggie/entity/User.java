package com.example.reggie.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * 移动端用户实体
 */
@Data
public class User implements Serializable {

    private static final Long serialVersionId = 1L;
    private Long id;
    private String name;
    private String phone;
    private String sex; //0 女   1 男
    private String idNumber;   //身份证号
    private String avatar;  //头像
    private Integer status; //状态 0:禁用，1:正常

}
