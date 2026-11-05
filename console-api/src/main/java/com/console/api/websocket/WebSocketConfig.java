//package com.console.api.websocket;
//
//import com.console.core.handler.WebSocketHandler;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.socket.config.annotation.EnableWebSocket;
//import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
//import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
//import org.springframework.web.socket.server.standard.ServletServerContainerFactoryBean;
//
///**
// * WebSocket配置处理器
// */
//@Configuration
//@EnableWebSocket
//public class WebSocketConfig implements WebSocketConfigurer {
//    @Override
//    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
//        // 使用/websocket而不是/ws，避免与静态资源路径冲突
//        registry.addHandler(new WebSocketHandler(), "/socket").setAllowedOrigins("*"); // 允许所有来源访问
//    }
//
//    @Bean
//    public ServletServerContainerFactoryBean createWebSocketContainer() {
//        ServletServerContainerFactoryBean container = new ServletServerContainerFactoryBean();
//        // 重要配置：设置最大会话空闲超时
//        container.setMaxSessionIdleTimeout(120000L); // 两分钟
//        container.setMaxTextMessageBufferSize(8192);
//        container.setMaxBinaryMessageBufferSize(8192);
//        return container;
//    }
//}
