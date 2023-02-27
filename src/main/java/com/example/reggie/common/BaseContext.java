package com.example.reggie.common;

/**
 * 基于ThreadLocal封装工具类，用于保存和获取当前登录用户的id，在 "LoginCheckFilter" 中保存，在 "MyMetaObjectHandler "中获取
 *
 * 原理：
 * 客户端发送的每次http请求，对应的在服务端都会分配一个新的线程Thread来处理，在处理过程中以下类中的方法都属于相同的一个线程：
 *      LoginCheckFilter的doFilter方法     EmployeeController的update方法     MyMetaObjectHandler的updateFill方法
 * 利用这个特性来帮助实现 “公共字段自动填充” 功能，此外，还需用到ThreadLocal。
 * ThreadLocal是Thread的局部变量，它的作用域是一个线程，也就是，在一个线程中ThreadLocal是唯一的。
 * 因此，可以用ThreadLocal来存储当前登录用户的id。
 * ThreadLocal常用方法：public void set(T value); public T get()
 */
public class BaseContext {

    public static ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    public static void setCurrentId(Long id){
        threadLocal.set(id);
    }

    public static Long getCurrentId(){
        return threadLocal.get();
    }
}
