package com.alphamaleclub.ucmc.system.exception;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ExceptionMessage {

    //Ex
    public static class MemberAuth {

        public static final String KAKAO_PROFILE_NOT_PROVIDED = "카카오 계정의 프로필 정보를 가져올 수 없습니다.";
        public static final String UNSUPPORTED_PROVIDER_EXCEPTION = "지원하지 않는 소셜 서비스 입니다.";
        public static final String MEMBER_NOT_FOUND_EXCEPTION = "회원을 찾을 수 없습니다.";
        public static final String MEMBER_NOT_FOUND = "대상 멤버가 없습니다.";
        public static final String NICKNAME_ALREADY_EXIST = "변경하려는 닉네임이 중복됩니다.";
        public static final String INVALID_REFRESH_TOKEN_PROVIDED = "변경하려는 닉네임이 중복됩니다.";
        public static final String EMPTY_REFRESH_TOKEN = "변경하려는 닉네임이 중복됩니다.";
        public static final String EXISTING_AUTHENTICATION_IS_NULL = "인가정보가 존재하지 않습니다.";
        public static final String DELETED_ACCOUNT_EXCEPTION = "탈퇴한 회원 입니다.";
        public static final String BANNED_ACCOUNT_EXCEPTION = "차단된 회원 입니다.";

    }

    public static class Member{

        public static final String ACCOUNT_ID_IS_NOT_FOUND = "ID가 올바르지 않습니다.";
        public static final String KEY_NUMBER_IS_NOT_FOUND = "Key 값이 올바르지 않습니다.";
        public static final String EMAIL_IS_NOT_FOUND = "해당 Email을 가진 유저가 없습니다.";


    }


}
