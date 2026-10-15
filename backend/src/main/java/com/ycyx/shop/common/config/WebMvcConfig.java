package com.ycyx.shop.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Paths;

/**
 * Web MVC 配置：将本地上传目录映射为静态资源，供前端直接访问
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Value("${ycyx.upload.path}")
    private String uploadPath;

    @Value("${ycyx.upload.url-prefix}")
    private String urlPrefix;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String location = Paths.get(uploadPath).toAbsolutePath().normalize().toUri().toString();
        registry.addResourceHandler(urlPrefix + "**").addResourceLocations(location);
    }
}