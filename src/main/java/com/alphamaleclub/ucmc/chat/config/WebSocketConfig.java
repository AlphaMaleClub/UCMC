package com.alphamaleclub.ucmc.chat.config;


import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;


@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    @Value("${custom.front.redirect-url}")
    private String redirectUrl;
    private final RabbitMQConfig rabbitMQConfig;

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws") // 클라이언트가 연결할 엔드포인트
                .setAllowedOriginPatterns(redirectUrl) // CORS 허용
                .withSockJS();// SockJS 사용 설정
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
//        registry.enableSimpleBroker("/sub"); // 메세지 브로커 설정 /topic  단일 브로커
        registry.enableStompBrokerRelay("/topic", "queue") //외부 브로커 사용 및 설정
                .setRelayHost(rabbitMQConfig.getHost())         // RabbitMQ 서버의 호스트 주소 설정
                .setRelayPort(rabbitMQConfig.getStompPort())           // RabbitMQ 서버의 STOMP 포트 설정
                .setClientLogin(rabbitMQConfig.getUsername())     // 클라이언트가 RabbitMQ에 접속할 때 사용할 로그인 아이디 설정
                .setClientPasscode(rabbitMQConfig.getPassword()); // 클라이언트가 RabbitMQ에 접속할 때 사용할 비밀번호 설정

        registry.setApplicationDestinationPrefixes("/pub"); // 메시지 전송 prefix 설정 /app
    }

}
