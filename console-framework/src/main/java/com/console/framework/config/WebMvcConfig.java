package com.console.framework.config;

import com.console.framework.interceptor.AuthInterceptor;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.CacheControl;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.concurrent.TimeUnit;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    @Value("${console.fileUpload.baseFilePath}")
    private String baseFilePath;

    private static final String UPLOAD_PREFIX = "/uploads/**";

    @Resource
    private AuthInterceptor authInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor)
                .excludePathPatterns("/backendUser/login", "/backendUser/refreshToken", "/login");
    }

    // 静态资源展示
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // PS：注意文件路径最后的斜杠（文件分隔符），如果缺少了，就不能够正确的映射到相应的目录
        String basePath = "file:" + baseFilePath;
        registry.addResourceHandler(UPLOAD_PREFIX)
                .addResourceLocations(basePath)
                .setCacheControl(CacheControl.maxAge(3600, TimeUnit.SECONDS).cachePublic());
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }
}
