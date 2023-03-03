package com.example.reggie.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ShoppingCart implements Serializable {

    private static final Long serialVersionId = 1L;
    private Long id;
    private String name;    //菜品或套餐名称
    private Long userId;//用户id
    private Long dishId;    //菜品id
    private Long setmealId;    //套餐id
    private String dishFlavor;    //口味
    private Integer number;    //数量
    private BigDecimal amount;    //金额
    private String image;    //图片
    private LocalDateTime createTime;
}
