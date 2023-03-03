package com.example.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.example.reggie.common.BaseContext;
import com.example.reggie.common.Response;
import com.example.reggie.entity.AddressBook;
import com.example.reggie.entity.User;
import com.example.reggie.service.AddressBookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/addressBook")
@Slf4j
public class AddressBookController {

    @Autowired
    private AddressBookService addressBookService;

    /**
     * 查询当前登录用户的所有地址信息。前端没有传递任何参数，通过ThreadLocal获取当前登录用户的id
     * @return
     */
    @GetMapping("/list")
    public Response<List<AddressBook>> list(){
        //通过ThreadLocal获取当前登录用户的id
        Long userId = BaseContext.getCurrentId();
        //构造查询器
        LambdaQueryWrapper<AddressBook> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AddressBook::getUserId, userId);
        queryWrapper.orderByDesc(AddressBook::getUpdateTime);
        List<AddressBook> addressBooks = addressBookService.list(queryWrapper);
        return Response.success(addressBooks);
    }

    /**
     * 新增地址。保存到address_book表前要先设置userId字段
     * @param addressBook  前端以json传地址信息到服务器
     * @return
     */
    @PostMapping
    public Response<String> save(@RequestBody AddressBook addressBook){
        //通过ThreadLocal获取当前登录用户的id
        Long userId = BaseContext.getCurrentId();
        addressBook.setUserId(userId);
        addressBookService.save(addressBook);
        return Response.success("保存地址成功");
    }

    /**
     * 根据指定地址id查询地址信息
     * @param id    以REST风格的方式传递地址id，使用@PathVariable接收
     * @return
     */
    @GetMapping("/{id}")
    public Response<AddressBook> getById(@PathVariable("id") Long id){
//        LambdaQueryWrapper<AddressBook> queryWrapper = new LambdaQueryWrapper<>();
//        queryWrapper.eq(AddressBook::getId, id);
//        AddressBook addressBook = addressBookService.getOne(queryWrapper);
//        log.info(addressBook.toString());
        AddressBook addressBook = addressBookService.getById(id);
        if (addressBook != null)
            return Response.success(addressBook);
        return Response.error("没有找到对应地址信息");
    }

    /**
     * 更新地址信息
     * @param addressBook   前端传递新的地址信息到服务器
     * @return
     */
    @PutMapping
    public Response<String> update(@RequestBody AddressBook addressBook){
        addressBook.setUserId(BaseContext.getCurrentId());
        addressBookService.updateById(addressBook);
        return Response.success("更改地址信息成功");
    }

    /**
     * 设置默认收获地址
     * @param addressBook   前端会传送指定地址id到服务器，可以用AddressBook对象接收
     * @return
     */
    @PutMapping("/default")
    public Response<String> setDefault(@RequestBody AddressBook addressBook){
        LambdaUpdateWrapper<AddressBook> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(AddressBook::getUserId, BaseContext.getCurrentId());
        updateWrapper.set(AddressBook::getIsDefault, 0); //先将所有地址设置为不是默认地址
        addressBookService.update(updateWrapper);

        addressBook.setIsDefault(1);    //再设置当前地址是默认地址
        addressBookService.updateById(addressBook);
        return Response.success("设置默认收货地址成功");
    }

    /**
     * 获取默认收货地址
     * @return
     */
    @GetMapping("/default")
    public Response<AddressBook> getDefault(){
        LambdaQueryWrapper<AddressBook> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AddressBook::getUserId, BaseContext.getCurrentId());
        queryWrapper.eq(AddressBook::getIsDefault, 1);
        AddressBook addressBook = addressBookService.getOne(queryWrapper);
        if(addressBook != null)
            return Response.success(addressBook);
        return Response.error("尚未设置默认地址");
    }
}
