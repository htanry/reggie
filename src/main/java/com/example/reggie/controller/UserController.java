package com.example.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.reggie.common.Response;
import com.example.reggie.entity.User;
import com.example.reggie.service.UserService;
import com.example.reggie.utils.SMSUtils;
import com.example.reggie.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 * 处理移动端请求
 */
@RestController
@RequestMapping("user")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 发送手机短信验证码
     * @param user  主要获取手机号，用User来存储兼容性更好
     * @param session
     * @return
     */
    @PostMapping("/sendMsg")
    public Response<String> sendMsg(@RequestBody User user, HttpSession session){
        //获取手机号
        String phone = user.getPhone();
        //生成随机的4位验证码
        if(StringUtils.isNotEmpty(phone)){
            String code = ValidateCodeUtils.generateValidateCode(4).toString();
            //调用短信服务API发送短信
//            SMSUtils.sendMessage("申请到的签名", "", phone, code);
            log.info("验证码: {}", code);
            //将生成的验证码保存到Session中
            session.setAttribute(phone, code);
            return Response.success("发送验证码成功");
        }

        return Response.success("发送验证码失败");
    }

    /**
     * 移动端用户登录
     * @param map   使用DTO来接收前端的json数据也可以
     * @param session
     * @return
     */
    @PostMapping("/login")
    public Response<User> login(@RequestBody Map map, HttpSession session){
        log.info(map.toString());
        //获取手机号
        String phone = map.get("phone").toString();
        //获取验证码
        String code = map.get("code").toString();
        //从Session中获取保存的验证码
        Object codeInSession = session.getAttribute(phone);
        //进行验证码的比对, 如果能够比对成功，说明登录成功
        if (codeInSession != null && codeInSession.equals(code)){
            //判断当前手机号对应的用户是否为新用户，如果是新用户就自动完成注册
            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getPhone, phone);
            User user = userService.getOne(queryWrapper);
            if (user == null){
                user = new User();
                user.setPhone(phone);
                userService.save(user);
            }
            session.setAttribute("user", user.getId());
            return Response.success(user);
        }

        return Response.error("登录失败");
    }
}
