package com.alphamaleclub.ucmc.member.services;


import com.alphamaleclub.ucmc.system.exception.ExceptionMessage;
import com.alphamaleclub.ucmc.system.exception.auth.CriticalKeyGenerateException;
import jakarta.annotation.PostConstruct;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.security.*;
import java.time.LocalDateTime;
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
                    .build();
        } catch (NoSuchAlgorithmException e) {
            log.error("키 생성 알고리즘이 올바르지 않습니다.");
        }

        return null;

    }

    @Scheduled(fixedRate = 7 * 24 * 60 * 60 * 1000) // 7일마다 실행
    public void updateKeySet() {

        previousKeySet = (currentKeySet != null) ? currentKeySet : null;
        currentKeySet = generateKeySet();

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

    }

}
