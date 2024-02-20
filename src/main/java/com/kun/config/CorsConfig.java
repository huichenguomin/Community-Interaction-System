package com.kun.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        //设置允许跨域的路径
        registry.addMapping("/**")
                //设置允许跨区请求的域名
                .allowedOriginPatterns("*")
                //设置允许cookie
                .allowCredentials(true)
                //设置允许的header
                .allowedHeaders("*")
                //设置允许的请求方法
                .allowedMethods("GET","POST","PUT","DELETE")
                //跨域允许时间
                .maxAge(3600);
    }
}
