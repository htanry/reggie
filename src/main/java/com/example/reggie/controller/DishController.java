package com.example.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.reggie.common.Response;
import com.example.reggie.dto.DishDto;
import com.example.reggie.entity.Category;
import com.example.reggie.entity.Dish;
import com.example.reggie.entity.DishFlavor;
import com.example.reggie.service.CategoryService;
import com.example.reggie.service.DishFlavorService;
import com.example.reggie.service.DishService;
import com.example.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 菜品和菜品口味管理
 */
@RestController
@RequestMapping("dish")
@Slf4j
public class DishController {

    @Autowired
    private DishService dishService;

    @Autowired
    private DishFlavorService dishFlavorService;

    @Autowired
    private CategoryService categoryService;

    /**
     * 新增菜品管理
     * @param dishDto 因为前端传送过来的数据比较复杂，包含了Dish和DishFlavor，所以用一个DishDto来接收。以json传输，用@RequestBody接收
     * @return 主要用到了.code提示前端保存成功
     */
    @PostMapping
    public Response<String> save(@RequestBody DishDto dishDto){
        log.info(dishDto.toString());
        dishService.saveWithFlavors(dishDto);
        return Response.success("新增菜品成功");
    }

    /**
     * 菜品分页查询
     * @param page  前端传页码
     * @param pageSize  前端传页面大小
     * @param name
     * @return
     */
    @GetMapping("page")
    public Response<Page> page(int page, int pageSize, String name){
        //创建分页构造器
        Page<Dish> dishPage = new Page<>(page, pageSize);
        Page<DishDto> dishDtoPage = new Page<>(page, pageSize);
        //创建条件构造器
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        //添加过滤条件，使用like模糊查询
        queryWrapper.like(StringUtils.isNotEmpty(name), Dish::getName, name);
        //添加排序条件
        queryWrapper.orderByDesc(Dish::getUpdateTime);
        dishService.page(dishPage, queryWrapper);

        //以下步骤是要设置dishDto中的categoryName属性，以便前端显示
        //对象拷贝，dishPage与dishDtoPage同名的属性会赋值
        BeanUtils.copyProperties(dishPage, dishDtoPage, "records"); //records与实体对应
        List<Dish> pageInfoRecords = dishPage.getRecords();
        List<DishDto> dishDtoRecords = pageInfoRecords.stream().map((item) -> {
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item, dishDto);
            Category category = categoryService.getById(item.getCategoryId());  //通过dish的categoryId获取分类对象
            dishDto.setCategoryName(category.getName());    //设置分类名称categoryName
            return dishDto;
        }).collect(Collectors.toList());
        dishDtoPage.setRecords(dishDtoRecords);
        return Response.success(dishDtoPage);
    }

    /**
     * 根据id查询菜品及相应的口味信息，也就是说要查dish和dish_flavor两张表
     * @param id    前端以REST风格传输id
     * @return
     */
    @GetMapping("{id}")
    public Response<DishDto> getById(@PathVariable Long id){
        DishDto dishDto = dishService.getByIdWithFlavor(id);
        return Response.success(dishDto);
    }

    /**
     * 新增菜品管理
     * @param dishDto 因为前端传送过来的数据比较复杂，包含了Dish和DishFlavor，所以用一个DishDto来接收。以json传输，用@RequestBody接收
     * @return 主要用到了.code提示前端保存成功
     */
    @PutMapping
    public Response<String> update(@RequestBody DishDto dishDto){
        log.info(dishDto.toString());
        dishService.updateWithFlavors(dishDto);
        return Response.success("新增菜品成功");
    }

    /**
     * 修改菜品状态，0 停售  1 起售
     * @param status 前端以REST风格传输，所以使用@PathVariable接收
     * @param id    前端以URL拼接的方式传输，所以形参名同名即可
     * @return  前端要根据服务器的response做出相应处理,主要使用.code
     */
    @PostMapping("status/{status}")
    public Response<String> changeStatus(@PathVariable Integer status, @RequestParam List<Long> id){
        //先从dish表获取对应id的菜品
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Dish::getId, id);   //id是List类型
        List<Dish> dishes = dishService.list(queryWrapper);
        //更新菜品的status
        dishes = dishes.stream().map((item) -> {
            item.setStatus(status);
            return item;
        }).collect(Collectors.toList());
        //更新数据库
        dishService.updateBatchById(dishes);
        return Response.success("更新菜品状态成功");
    }

    /**
     * 根据id删除菜品，同时要删除菜品的口味数据(和包括了要删除菜品的套餐数据，但没有实现菜品与套餐的关联关系表)
     * @param id    前端以URL拼接的方式传输，可能会批量删除
     * @return  前端要根据服务器的response做出相应处理,主要使用.code
     */
    @DeleteMapping
    public Response<String> delete(@RequestParam List<Long> id){
        dishService.deleteWithFlavors(id);
        return Response.success("菜品删除成功");
    }

    /**
     * 在套餐管理中根据条件查询菜品信息
     * @param dish  前端会传输categoryId过来，可以用Long型数据接收，但是使用Dish接收更加通用，即前端传输其他信息也可以被接收到
     * @return  用于前端展示菜品信息
     */
//    @GetMapping("list")
//    public Response<List<Dish>> list(Dish dish){
//        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
//        queryWrapper.eq(dish.getCategoryId() != null, Dish::getCategoryId, dish.getCategoryId());
//        queryWrapper.eq(Dish::getStatus, 1);
//        List<Dish> dishList = dishService.list(queryWrapper);
//        return Response.success(dishList);
//    }

    /**
     * 在套餐管理和移动端购物车中根据条件查询菜品信息。基于上面的方法做出改进：移动端需要同时展示口味信息，所以返回对象改为DishDto
     * @param dish
     * @return
     */
    @GetMapping("list")
    public Response<List<DishDto>> list(Dish dish){
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(dish.getCategoryId() != null, Dish::getCategoryId, dish.getCategoryId());
        queryWrapper.eq(Dish::getStatus, 1);
        List<Dish> dishList = dishService.list(queryWrapper);
        List<DishDto> dishDtoList = dishList.stream().map((item) -> {
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item, dishDto);
            Category category = categoryService.getById(item.getCategoryId());  //通过Dish对象查找对应分类
            dishDto.setCategoryName(category.getName());    //设置分类名
            LambdaQueryWrapper<DishFlavor> dishFlavorLambdaQueryWrapper = new LambdaQueryWrapper<>();
            dishFlavorLambdaQueryWrapper.eq(DishFlavor::getDishId, item.getId());   //根据Dish对象查找口味数据
            List<DishFlavor> dishFlavors = dishFlavorService.list(dishFlavorLambdaQueryWrapper);
            dishDto.setFlavors(dishFlavors);    //设置口味
            return dishDto;
        }).collect(Collectors.toList());
        return Response.success(dishDtoList);
    }
}
