package com.example.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.reggie.common.Response;
import com.example.reggie.dto.SetmealDto;
import com.example.reggie.entity.Category;
import com.example.reggie.entity.Setmeal;
import com.example.reggie.service.CategoryService;
import com.example.reggie.service.SetmealDishService;
import com.example.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 套餐管理
 */
@RestController
@RequestMapping("setmeal")
@Slf4j
public class SetmealController {

    @Autowired
    private SetmealService setmealService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private SetmealDishService setmealDishService;

    /**
     * 新增套餐，要同时往setmeal套餐表和setmeal_dish套餐菜品关系表中新增数据
     * @param setmealDto    前端传送的信息包括套餐信息和菜品信息
     * @return
     */
    @PostMapping
    public Response<String> save(@RequestBody SetmealDto setmealDto){
        setmealService.saveWithDish(setmealDto);
        return Response.success("新增套餐成功");
    }

    /**
     * 套餐分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public Response<Page> list(int page, int pageSize, String name){
        Page<Setmeal> setmealPage = new Page<>(page, pageSize);
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.isNotEmpty(name), Setmeal::getName, name);
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);
        setmealService.page(setmealPage, queryWrapper);

        //前端要显示套餐名称，所以要返回DTO对象，即SetmealDto
        Page<SetmealDto> setmealDtoPage = new Page<>(page, pageSize);
        BeanUtils.copyProperties(setmealPage, setmealDtoPage, "records");
        List<Setmeal> setmealRecords = setmealPage.getRecords();
        List<SetmealDto> setmealDtoRecords = setmealRecords.stream().map((item) -> {
            SetmealDto setmealDto = new SetmealDto();
            BeanUtils.copyProperties(item, setmealDto); //SetmealDto继承了Setmeal，此语句会填充SetmealDto中的Setmeal属性
            Category category = categoryService.getById(item.getCategoryId());  //通过Setmeal对象获取分类对象
            if (category != null)
                setmealDto.setCategoryName(category.getName()); //设置分类名称
            return setmealDto;
        }).collect(Collectors.toList());
        setmealDtoPage.setRecords(setmealDtoRecords);
        return Response.success(setmealDtoPage);
    }

    /**
     * 根据id删除套餐
     * @param id    可能会批量删除，必须要使用@RequestParam接收
     * @return
     */
    @DeleteMapping
    public Response<String> delete(@RequestParam List<Long> id){
        setmealService.deleteWithDish(id);
        return Response.success("删除套餐成功");
    }

    /**
     * 修改套餐的状态
     * @param status
     * @param id
     * @return
     */
    @PostMapping("/status/{status}")
    public Response<String> changeStatus(@PathVariable Integer status, @RequestParam List<Long> id){
        //根据id查询套餐
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Setmeal::getId, id);    //id是List类型
        List<Setmeal> setmeals = setmealService.list(queryWrapper);
        setmeals = setmeals.stream().map((item) -> {
            item.setStatus(status);
            return item;
        }).collect(Collectors.toList());
        setmealService.updateBatchById(setmeals);
        return Response.success("套餐状态修改成功");
    }
}
