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

    public static class Image {
        public static final String IMAGES_FILES_LIMIT_EXCEEDED_EXCEPTION = "파일이 5개 이상입니다";
        public static final String IMAGE_CONVERT_EXCEPTION = "convert 중 예외가 발생 하였습니다";
        public static final String IMAGE_FILES_EMPTY_EXCEPTION = "이미지 파일이 들어 있지 않습니다";
        public static final String IMAGE_FILE_TOO_LARGE_EXCEPTION = "파일의 크기가 허용치보다 큽니다.";
        public static final String IMAGE_DIMENSION_EXCEEDED_EXCEPTION = "파일의 폭 또는 높이가 초과되었습니다.";
        public static final String IMAGE_DIMENSION_TOO_SMALL_EXCEPTION = "파일의 폭 또는 높이가 너무 작습니다.";
        public static final String INVALID_IMAGE_FORMAT_EXCEPTION = "지원되지 않는 형식이거나 손상된 파일일 수 있습니다";
    }

}
