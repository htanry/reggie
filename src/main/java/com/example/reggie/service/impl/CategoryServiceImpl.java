package com.example.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.reggie.common.CustomException;
import com.example.reggie.entity.Category;
import com.example.reggie.entity.Dish;
import com.example.reggie.entity.Setmeal;
import com.example.reggie.mapper.CategoryMapper;
import com.example.reggie.service.CategoryService;
import com.example.reggie.service.DishService;
import com.example.reggie.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Autowired
    private DishService dishService;

    @Autowired
    private SetmealService setmealService;

    /**
     *  根据id删除分类，删除之前判断要删除的分类是否关联了菜品或套餐
     * @param id
     */
    @Override
    public void remove(Long id) {
        //查询当前分类是否关联了菜品，如果关联，就抛出一个业务异常
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();   //创建查询构造器
        dishLambdaQueryWrapper.eq(Dish::getCategoryId, id); //添加查询条件，根据分类id进行查询
        if (dishService.count(dishLambdaQueryWrapper) > 0){
            throw new CustomException("当前分类关联了其他菜品，不能删除");  //通过全局异常信息处理器GlobalExceptionHandler可以将此异常反映到浏览器
        }
        //查询当前分类是否关联了套餐，如果关联，就抛出一个业务异常
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealLambdaQueryWrapper.eq(Setmeal::getCategoryId, id);
        if (setmealService.count(setmealLambdaQueryWrapper) > 0){
            throw new CustomException("当前分类关联了其他套餐，不能删除");
        }
        //没有关联菜品或套餐，直接删除分类
        super.removeById(id);
    }
}
