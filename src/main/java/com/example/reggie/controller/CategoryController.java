package com.example.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.reggie.common.Response;
import com.example.reggie.entity.Category;
import com.example.reggie.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.DeleteProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 菜品和套餐分类管理
 */
@RestController
@RequestMapping("/category")
@Slf4j
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/page")
    public Response<Page> page(int page, int pageSize){
        //分页构造器
        Page<Category> pageInfo = new Page<>(page, pageSize);
        //条件构造器
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        //添加排序条件，根据sort字段排序
        queryWrapper.orderByAsc(Category::getSort);
        //进行分页查询
        categoryService.page(pageInfo, queryWrapper);
        return Response.success(pageInfo);
    }

    /**
     * 新增分类
     * @param category 前端以json的格式传输到服务器,所以使用@RequestBody接收
     * @return
     */
    @PostMapping
    public Response<String> save(@RequestBody Category category){
        log.info("category{}", category);
        categoryService.save(category);
        return Response.success("新增分类成功");
    }

    /**
     * 根据id删除某个分类
     * @param id 前端以url拼接的方式传到服务器
     * @return
     */
    @DeleteMapping
    public Response<String> delete(Long id){
        log.info("删除分类 {}", id);
        //categoryService.removeById(id); //如果当前分类关联了其他菜品或套餐，就不能直接删除当前分类
        categoryService.remove(id); //调用自定义的删除方法
        return Response.success("成功删除分类");
    }

    /**
     * 根据id修改分类信息
     * @param category  前端通过json形式将分类的信息传送到服务器，所以使用@RequestBody接收
     * @return
     */
    @PutMapping
    public Response<String> update(@RequestBody Category category){
        log.info("修改分类信息 {}", category);
        categoryService.updateById(category);
        return Response.success("修改分类信息成功");
    }
}
