package com.ycyx.shop;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * 粤尝粤鲜 · 鲜果商城 启动类
 */
@SpringBootApplication(exclude = UserDetailsServiceAutoConfiguration.class)
@MapperScan("com.ycyx.shop.mapper")
@EnableAsync
public class ShopApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShopApplication.class, args);
        System.out.println("==============================================");
        System.out.println("  粤尝粤鲜 鲜果商城 启动成功!");
        System.out.println("  接口文档: http://localhost:8080/api/doc.html");
        System.out.println("==============================================");
    }
}