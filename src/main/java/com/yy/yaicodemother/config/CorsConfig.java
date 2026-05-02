package com.yy.yaicodemother.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 跨域配置类
 * 用于配置全局跨域访问策略，实现前后端分离项目的跨域请求处理
 */
@Configuration // 标识为配置类，将被Spring容器管理
public class CorsConfig implements WebMvcConfigurer {

    /**
     * 配置跨域映射规则
     * @param registry CORS注册对象，用于添加跨域配置
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // 覆盖所有请求 - 设置全局跨域配置，匹配所有URL路径
        registry.addMapping("/**")
                // 允许发送 Cookie - 设置允许跨域请求携带认证信息（如Cookie）
                .allowCredentials(true)
                // 放行哪些域名（必须用 patterns，否则 * 会和 allowCredentials 冲突）
                // 使用allowedOriginPatterns而不是allowedOrigins，因为allowCredentials为true时不能使用"*"
                .allowedOriginPatterns("*")
                // 允许的请求方法 - 设置允许的HTTP方法
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                // 允许的请求头 - 设置允许的请求头信息
                .allowedHeaders("*")
                // 暴露的响应头 - 设置浏览器可以获取到的响应头
                .exposedHeaders("*");
    }
}
