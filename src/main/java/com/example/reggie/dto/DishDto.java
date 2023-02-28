package com.example.reggie.dto;

import com.example.reggie.entity.Dish;
import com.example.reggie.entity.DishFlavor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * DTO，全称Data Transfer Object，即数据传输对象，一般用于表示层和服务层之间的数据传输
 */
@Data
public class DishDto extends Dish {

    private List<DishFlavor> flavors = new ArrayList<>();   //封装菜品口味数据
    private String categoryName;
    private Integer copies;
}
