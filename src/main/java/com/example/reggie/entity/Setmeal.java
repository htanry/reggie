package com.example.reggie.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 套餐实体类
 */
@Data
public class Setmeal implements Serializable {

    private static final Long serialVersionId = 1L;

    private Long id;
    private Long categoryId;    //套餐分类ID
    private String name;    //套餐名称
    private BigDecimal price;   //套餐价格
    private Integer status;     //状态: 0 停售  1   在售
    private String code;    //套餐码
    private String description; //套餐描述
    private String image;   //套餐图片
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
