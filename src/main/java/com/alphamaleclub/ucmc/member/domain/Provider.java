package com.alphamaleclub.ucmc.member.domain;

import com.alphamaleclub.ucmc.system.exception.ExceptionMessage;
import com.alphamaleclub.ucmc.system.exception.auth.IllegalProviderNameException;

public enum Provider {
    none, google, naver, kakao;

    public static Provider fromString(String value) {
        for (Provider provider : Provider.values()) {
            if (provider.name().equalsIgnoreCase(value)) {
                return provider;
            }
        }
        throw new IllegalProviderNameException(ExceptionMessage.Auth.PROVIDER_IS_NOT_FOUND + value);
    }
}

