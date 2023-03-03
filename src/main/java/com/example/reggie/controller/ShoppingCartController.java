package com.example.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.reggie.common.BaseContext;
import com.example.reggie.common.Response;
import com.example.reggie.entity.ShoppingCart;
import com.example.reggie.service.ShoppingCartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/shoppingCart")
public class ShoppingCartController {

    @Autowired
    private ShoppingCartService shoppingCartService;

    /**
     * 将菜品或套餐添加到购物车
     * @param shoppingCart  前端以json的形式传递
     * @return
     */
    @PostMapping("/add")
    public Response<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart){
        //获取并设置用户id
        Long useId = BaseContext.getCurrentId();
        shoppingCart.setUserId(useId);
        LambdaQueryWrapper<ShoppingCart> shoppingCartLambdaQueryWrapper = new LambdaQueryWrapper<>();
        //shopping_cart表中的记录根据userId和dishId/setmealId唯一确定
        shoppingCartLambdaQueryWrapper.eq(ShoppingCart::getUserId, useId);

        //判断当前要添加的是菜品还是套餐
        Long dishId = shoppingCart.getDishId();
        if(dishId != null){ //菜品
            shoppingCartLambdaQueryWrapper.eq(ShoppingCart::getDishId, dishId);
        }else{  //套餐
            shoppingCartLambdaQueryWrapper.eq(ShoppingCart::getSetmealId, shoppingCart.getSetmealId());
        }
        ShoppingCart shoppingCart1 = shoppingCartService.getOne(shoppingCartLambdaQueryWrapper);

        //判断数据库中是否已经存在当前要添加的对象
        if(shoppingCart1 != null){
            //已经存在
            shoppingCart1.setNumber(shoppingCart1.getNumber() + 1);
            shoppingCartService.updateById(shoppingCart1); //已经存在就只更新数量
        }else{
            //尚未存在
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCart1 = shoppingCart;
            shoppingCartService.save(shoppingCart1); //尚未存在就新增
        }
        return Response.success(shoppingCart1);
    }

    /**
     * 查询对应用户的购物车信息
     * @return
     */
    @GetMapping("list")
    public Response<List<ShoppingCart>> list(){
        LambdaQueryWrapper<ShoppingCart> shoppingCartLambdaQueryWrapper = new LambdaQueryWrapper<>();
        shoppingCartLambdaQueryWrapper.eq(ShoppingCart::getUserId, BaseContext.getCurrentId());
        shoppingCartLambdaQueryWrapper.orderByDesc(ShoppingCart::getCreateTime);
        List<ShoppingCart> shoppingCartList = shoppingCartService.list(shoppingCartLambdaQueryWrapper);
        return Response.success(shoppingCartList);
    }

    /**
     * 清空对应用户的购物车
     * @return
     */
    @DeleteMapping("clean")
    public Response<String> delete(){
        LambdaQueryWrapper<ShoppingCart> shoppingCartLambdaQueryWrapper = new LambdaQueryWrapper<>();
        shoppingCartLambdaQueryWrapper.eq(ShoppingCart::getUserId, BaseContext.getCurrentId());
        shoppingCartService.remove(shoppingCartLambdaQueryWrapper);
        return Response.success("清空购物车成功");
    }

    /**
     * 在购物车中的菜品或套餐减1
     * @param shoppingCart
     * @return
     */
    @PostMapping("sub")
    public Response<String> sub(@RequestBody ShoppingCart shoppingCart){
        //获取用户id
        Long userId = BaseContext.getCurrentId();
        LambdaQueryWrapper<ShoppingCart> shoppingCartLambdaQueryWrapper = new LambdaQueryWrapper<>();
        shoppingCartLambdaQueryWrapper.eq(ShoppingCart::getUserId, userId);
        Long dishId = shoppingCart.getDishId();
        if (dishId != null){    //是菜品
            shoppingCartLambdaQueryWrapper.eq(ShoppingCart::getDishId, dishId);
        }else{  //是套餐
            shoppingCartLambdaQueryWrapper.eq(ShoppingCart::getSetmealId, shoppingCart.getSetmealId());
        }
        ShoppingCart cartServiceOne = shoppingCartService.getOne(shoppingCartLambdaQueryWrapper);
        cartServiceOne.setNumber(cartServiceOne.getNumber() - 1);
        shoppingCartService.updateById(cartServiceOne); //更新数量

        //重新查询对应用户的购物车数据，以便在前端回显
//        List<ShoppingCart> shoppingCartList = shoppingCartService.list(shoppingCartLambdaQueryWrapper);
        return Response.success("减1成功");
    }
}
