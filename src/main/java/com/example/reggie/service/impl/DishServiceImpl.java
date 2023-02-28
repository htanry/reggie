package com.example.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.reggie.dto.DishDto;
import com.example.reggie.entity.Dish;
import com.example.reggie.entity.DishFlavor;
import com.example.reggie.mapper.DishMapper;
import com.example.reggie.service.DishFlavorService;
import com.example.reggie.service.DishService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {

    @Autowired
    private DishFlavorService dishFlavorService;

    /**
     * 保存菜品的同时，保存菜品口味
     * @param dishDto
     */
    @Override
    @Transactional  //涉及到两张表的保存，需要开启Spring事务管理保证事务的一致性
    // 还要在启动类上开启事务管理机制 @EnableTransactionManagement
    public void saveWithFlavors(DishDto dishDto) {
        this.save(dishDto); //保存菜品的基本信息到菜品表
        Long dishId = dishDto.getId();  //获取菜品id
        List<DishFlavor> dishFlavors = dishDto.getFlavors();    //获取口味数据
        dishFlavors = dishFlavors.stream().map((item) -> {  //在DishFlavor中有一个属性dishId需要设置
            item.setDishId(dishId);
            return item;
        }).collect(Collectors.toList());
        dishFlavorService.saveBatch(dishFlavors);   //设置了dishId之后才将口味数据保存到口味表中
    }

    /**
     * //查询菜品的同时，查询菜品口味。用于在修改菜品信息时回显
     * @param id
     * @return
     */
    @Override
    public DishDto getByIdWithFlavor(Long id) {
        Dish dish = this.getById(id);   //查询菜品，从dish表查询
        DishDto dishDto = new DishDto();
        BeanUtils.copyProperties(dish, dishDto);
        //查询当前菜品的口味，从dish_flavor表查询
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId, id);
        List<DishFlavor> dishFlavors = dishFlavorService.list(queryWrapper);
        dishDto.setFlavors(dishFlavors);
        return dishDto;
    }

    /**
     * 更新菜品的同时，更新菜品口味。在更新菜品口味时是分成了两步：先删除dish_flavor表中原先与当前菜品相对应的口味数据，在插入新的
     * 口味数据，而插入新的口味数据的操作与新增菜品时的操作是一样的
     * @param dishDto
     */
    @Override
    @Transactional  //保证事务的一致性
    public void updateWithFlavors(DishDto dishDto) {
        //更新dish表
        this.updateById(dishDto);
        //删除dish_flavor表中与当前菜品相对应的口味数据 (口味可能发生变化)
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId, dishDto.getId());
        dishFlavorService.remove(queryWrapper);
        //插入dish_flavor表中与当前菜品相对应的口味数据
        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors = flavors.stream().map((item) -> {  ////在DishFlavor中有一个属性dishId需要设置，这一步与新增菜品时一致
            item.setDishId(dishDto.getId());
            return item;
        }).collect(Collectors.toList());
        dishFlavorService.saveBatch(flavors);
    }
}
