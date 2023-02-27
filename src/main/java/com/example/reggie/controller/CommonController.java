package com.example.reggie.controller;

import com.example.reggie.common.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.UUID;

/**
 * 文件上传与下载
 */
@Slf4j
@RestController
@RequestMapping("common")
public class CommonController {

    @Value("${reggie.path}")    //与全局配置文件中的属性一致
    private String basePath;
    /**
     * 文件上传
     * @param file  形参名要与前端传给服务器的name一致
     * @return
     */
    @PostMapping("/upload")
    public Response<String> upload(MultipartFile file){
        log.info(file.toString());
        //file是一个临时文件，需要转存到指定位置，否则请求完成后file会被删除
        String originalFilename = file.getOriginalFilename();   //原始文件名
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));  //获取文件后缀
        String fileName = UUID.randomUUID().toString() + suffix;    //使用UUID生成文件名，防止重名
        File dir = new File(basePath);
        if (!dir.exists())
            dir.mkdirs();   //目录不存在就创建
        try {
            file.transferTo(new File(basePath + fileName)); //将文件存储到指定位置
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Response.success(fileName);  //返回文件名用于在浏览器下载并展示对应图片
    }

    /**
     * 文件下载，不下载到本地，直接将图片展示在浏览器
     * @param name  文件上传时对应图片的名称
     * @param response  用于将图片展示在浏览器
     */
    @GetMapping("/download")
    public void download(String name, HttpServletResponse response){
        try {
            //通过输入流读取文件
            FileInputStream fileInputStream = new FileInputStream(new File(basePath + name));
            //通过输出流写文件到浏览器
            ServletOutputStream outputStream = response.getOutputStream();
            response.setContentType("image/jpeg");
            //以下为读写文件的固定形式
            int len = 0;
            byte[] bytes = new byte[1024];
            while((len = fileInputStream.read(bytes)) != -1){
                outputStream.write(bytes, 0, len);
                outputStream.flush();
            }
            outputStream.close();
            fileInputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
