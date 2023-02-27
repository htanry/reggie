package com.example.reggie.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class Employee implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long id;
    private String username;
    private String name;
    private String password;
    private String phone;
    private String sex;
    private String idNumber;    //身份证号
    private Integer status;     //用户状态，1表示正常，0表示禁用
    //@TableField是mybatis-plus的注解，为对应属性开启公共字段自动填充功能，fill属性指定自动填充策略，即什么时候会自动填充
    @TableField(fill = FieldFill.INSERT)    //插入时填充字段
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE) //插入和更新时填充字段
    private LocalDateTime updateTime;
    @TableField(fill = FieldFill.INSERT)
    private Long createUser;    //创建一个用户的创建者id
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updateUser;
}
