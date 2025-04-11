package com.alphamaleclub.ucmc.chat.config;


import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
@Getter
public class RabbitMQConfig {

    @Value("${spring.rabbitmq.host}") // RabbitMQ 호스트 주소
    private String host;

    @Value("${spring.rabbitmq.port}") // RABBITMQ의 STOMP 포트
    private int port;

    @Value("${spring.rabbitmq.username}")
    private String username;

    @Value("${spring.rabbitmq.password}")
    private String password;

    @Value("${spring.rabbitmq.stomp-port}")
    private int stompPort;

}
