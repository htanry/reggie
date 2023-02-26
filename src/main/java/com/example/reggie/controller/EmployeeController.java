package com.example.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.reggie.common.Response;
import com.example.reggie.entity.Employee;
import com.example.reggie.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    /**
     * Employee登录检查
     * @param request
     * @param employee
     * @return
     */
    @PostMapping("/login")
    public Response<Employee> login(HttpServletRequest request, @RequestBody Employee employee){
        //1.将页面提交的密码password进行md5加密
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        //2.根据页面提交的用户名username查询数据库
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername, employee.getUsername());
        Employee emp = employeeService.getOne(queryWrapper);
        //3.如果没有查询到则返回登录失败结果
        if (emp == null)
            return Response.error("账号不存在");
        //4.密码比对，如果不一致则返回登录失败结果
        if (!password.equals(emp.getPassword()))
            return Response.error("用户密码错误");
        //5.查看员工状态，如果为已禁用状态，则返回员工已金童结果
        if (emp.getStatus() == 0)
            return Response.error("账号已被禁用");
        //6.登录成功，将员工id存入Session并返回登录成功结果
        request.getSession().setAttribute("employee", emp.getId());
        return Response.success(emp);
    }

    /**
     * employee退出登录
     * @param request
     * @return
     */
    @PostMapping("/logout")
    public Response<String> logout(HttpServletRequest request){
        //清除Session中保存的当前employee的id
        request.getSession().removeAttribute("employee");
        return Response.success("退出成功");
    }

    /**
     * 新增员工
     * @param request
     * @param employee  以json的形式从客户端传到服务器
     * @return
     */
    @PostMapping    //新增员工的请求是/employ，已经在类上标注，所以这里不用写请求路径
    public Response<String> save(HttpServletRequest request, @RequestBody Employee employee){
        log.info("新增员工{}", employee.getName());
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));  //设置默认密码并加密
        employee.setCreateTime(LocalDateTime.now());    //设置创建员工的时间
        employee.setUpdateTime(LocalDateTime.now());
        //获取创建者的id
        Long employeeId = (Long) request.getSession().getAttribute("employee");
        employee.setCreateUser(employeeId);   //设置创建者的id
        employee.setUpdateUser(employeeId);
        employeeService.save(employee);
        return Response.success("新增员工成功");
    }

    /**
     * 员工信息的分页查询
     * @param page 客户端以URL拼接的方式传参数给服务器，所以直接直接声明形参就可以获取到对应的参数值
     * @param pageSize
     * @param name 形参名要与URL中的key一致
     * @return
     */
    @GetMapping("/page")    //请求路径http://localhost:8080/employee/page?page=1&pageSize=10&name=%E5%BC%A0%E4%B8%89
    public Response<Page> page(int page, int pageSize, String name){
        log.info("page = {} pageSize = {} key = {}", page, pageSize, name);
        //创建分页构造器
        Page pageInfo = new Page(page, pageSize);   //page和pageSize传给Page
        //创建条件构造器
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        //添加过滤条件
        queryWrapper.like(StringUtils.isNotEmpty(name), Employee::getName, name);   //当name非空时才添加
        //添加排序条件
        queryWrapper.orderByDesc(Employee::getUpdateTime);  //根据employee.updateTime排序
        employeeService.page(pageInfo, queryWrapper);
        return Response.success(pageInfo);
    }
}
