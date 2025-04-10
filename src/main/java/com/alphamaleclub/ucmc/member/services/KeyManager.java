package com.alphamaleclub.ucmc.member.services;


import com.alphamaleclub.ucmc.system.exception.ExceptionMessage;
import com.alphamaleclub.ucmc.system.exception.auth.CriticalKeyGenerateException;
import jakarta.annotation.PostConstruct;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cglib.core.Local;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.security.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.UUID;

@Slf4j
@Getter
@Component
@NoArgsConstructor
public class KeyManager {

    private KeySet currentKeySet;
    private KeySet previousKeySet;

    @PostConstruct
    public void init(){

        currentKeySet = generateKeySet();

        log.info("currentKeySet.toString() = {}", currentKeySet);

        if(currentKeySet == null){throw new CriticalKeyGenerateException(ExceptionMessage.Auth.PRIVATE_KEY_MUST_NOT_BE_NULL);}

        previousKeySet = null;

    }


    private KeySet generateKeySet() {

        try {

            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            KeyPair keyPair = keyPairGenerator.generateKeyPair();
            String kid = UUID.randomUUID().toString();
            LocalDateTime now = LocalDateTime.now();

            return KeySet.builder()
                    .kid(kid)
                    .privateKey(keyPair.getPrivate())
                    .publicKey(keyPair.getPublic())
                    .createdAt(now)
                    .build();

        } catch (NoSuchAlgorithmException e) {

            log.error("키 생성 알고리즘이 올바르지 않습니다. 코드변경이 필요합니다.");

        }

        return null;

    }

//    @Scheduled(fixedRate = 10 * 1000) // 테스트용 : 10초마다 실행
    @Scheduled(fixedRate = 7 * 24 * 60 * 60 * 1000) // 7일마다 실행
    public void updateKeySet() {

        previousKeySet = (currentKeySet != null) ? currentKeySet : null;
        currentKeySet = generateKeySet();

        log.info("currentKeySet.toString() = {}", currentKeySet.toString());
        log.info("previousKeySet.toString() = {}", previousKeySet.toString());


    }

    public PrivateKey getPrivateKey() {
        return currentKeySet.getPrivateKey();
    }

    public PrivateKey getPrivateKey(String intent) {
        return (intent.equals("previous")) ? currentKeySet.getPrivateKey() : null;
    }

    public PublicKey getPublicKey() {
        return currentKeySet.getPublicKey();
    }

    public PublicKey getPublicKey(String intent) {
        return (intent.equals("previous")) ? currentKeySet.getPublicKey() : null;
    }

    @Getter
    @Builder
    private static class KeySet {

        private String kid;
        private PrivateKey privateKey;
        private PublicKey publicKey;
        private LocalDateTime createdAt ;

        @Override
        public String toString() {
            return "\n" +"KeySet{" + "\n" +
                    "kid='" + kid + '\'' + "\n" +
                    "privateKey=" + Base64.getEncoder().encodeToString(privateKey.getEncoded()) + "\n" +
                    "publicKey=" + Base64.getEncoder().encodeToString(publicKey.getEncoded()) + "\n" +
                    "createdAt=" + createdAt.format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss")) + "\n" +
                    '}';
        }
    }

}
