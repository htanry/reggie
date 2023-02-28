package com.example.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.reggie.common.CustomException;
import com.example.reggie.dto.SetmealDto;
import com.example.reggie.entity.Setmeal;
import com.example.reggie.entity.SetmealDish;
import com.example.reggie.mapper.SetmealMapper;
import com.example.reggie.service.SetmealDishService;
import com.example.reggie.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {

    @Autowired
    private SetmealDishService setmealDishService;

    /**
     * 新增套餐，同时需要保存套餐和菜品的关联关系，即向setmeal和setmeal_dish两张表中存储数据
     * @param setmealDto
     */
    @Override
    @Transactional
    public void saveWithDish(SetmealDto setmealDto) {
        //保存套餐的基本信息，操作setmeal表，SetmealDto集成了操作Setmeal类，所以会直接存储Setmeal对象到setmeal表
        this.save(setmealDto);

        //保存套餐和菜品的关联关系，操作setmeal_dish表
        //在前端新增套餐时会选择一些菜品，菜品的id会传过来，而菜品在setmeal_dish中对应哪个套餐(setmealId)还没设置
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes = setmealDishes.stream().map((item) -> {
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());
        setmealDishService.saveBatch(setmealDishes);
    }

    /**
     * 删除套餐，同时需要删除套餐和菜品的关联关系
     * @param id    可能会批量删除
     */
    @Override
    @Transactional
    public void deleteWithDish(List<Long> id) {
        //如果套餐状态是在售状态即status=1，则不能删除
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealLambdaQueryWrapper.in(Setmeal::getId, id);   //批量删除的话是多个id，所以用in
        setmealLambdaQueryWrapper.eq(Setmeal::getStatus, 1);
        if (this.count(setmealLambdaQueryWrapper) > 0)
            throw new CustomException("该套餐在售，不能删除");
        //套餐状态是停售状态，可以删除
        //先删除setmeal表中的记录
        this.removeByIds(id);
        //再删除setmeal_dish表中的记录
        LambdaQueryWrapper<SetmealDish> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        //删除setmeal_dish表中setmealId={id}的记录
        lambdaQueryWrapper.in(SetmealDish::getSetmealId, id);   //批量删除的话是多个id，所以用in
        setmealDishService.remove(lambdaQueryWrapper);
    }
}
