package com.example.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.reggie.dto.DishDto;
import com.example.reggie.entity.Dish;

public interface DishService extends IService<Dish> {

    //保存菜品的同时，保存菜品口味
    public void saveWithFlavors(DishDto dishDto);

    //查询菜品的同时，查询菜品口味
    public DishDto getByIdWithFlavor(Long id);

    //更新菜品的同时，更新菜品口味
    public void updateWithFlavors(DishDto dishDto);
}
