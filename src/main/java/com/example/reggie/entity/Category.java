package com.example.reggie.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import org.springframework.util.StreamUtils;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 菜品分类和套餐分类
 */
@Data
public class Category implements Serializable {

    private static final long serialVersionId = 1L;
    private Long id;
    private Integer type;   //类型    1：菜品分类  2：套餐分类
    private String name;    //分类名称
    private Integer sort;   //顺序
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    @TableField(fill = FieldFill.INSERT)
    private Long createUser;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updateUser;
//    private Integer isDeleted;  //是否已删除，数据库中没有此字段
}
