package com.Just_112_More.PicPle.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker // STOMP 기반 메시징 활성화
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    // 메시지 브로커 설정
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // topic으로 시작하는 경로로 보내는 메시지 브로커가 처리
        config.enableSimpleBroker("/topic");
        // app으로 시작하는 경로로 보내는 메시지 컨트롤러 라우팅
        config.setApplicationDestinationPrefixes("/app");
    }

    // 클라이언트가 접속할 endpoint등록
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*") // CORS 허용
                .withSockJS(); // WebSocket 미지원 브라우저 대응
    }
}
