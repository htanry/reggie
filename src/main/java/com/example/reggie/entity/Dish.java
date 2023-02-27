package com.example.reggie.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 菜品
 */
@Data
public class Dish implements Serializable {

    private static final Long serialVersionId = 1L;

    private Long id;
    private String name;    //菜品名称
    private Long categoryId;    //菜品分类ID
    private BigDecimal price;   //菜品价格
    private String code;    //菜品码
    private String image;   //菜品图片
    private String description; //菜品描述
    private Integer status;     //0 停售  1 起售
    private Integer sort;   //顺序
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    @TableField(fill = FieldFill.INSERT)
    private Long createUser;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updateUser;
    private Integer isDeleted;
}
