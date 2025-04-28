package com.alphamaleclub.ucmc.system.exception;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ExceptionMessage {

    public static class Image {
        public static final String IMAGES_FILES_LIMIT_EXCEEDED_EXCEPTION = "파일이 5개 이상입니다";
        public static final String IMAGE_CONVERT_EXCEPTION = "convert 중 예외가 발생 하였습니다";
        public static final String IMAGE_FILES_EMPTY_EXCEPTION = "이미지 파일이 들어 있지 않습니다";
        public static final String IMAGE_FILE_TOO_LARGE_EXCEPTION = "파일의 크기가 허용치보다 큽니다.";
        public static final String IMAGE_DIMENSION_EXCEEDED_EXCEPTION = "파일의 폭 또는 높이가 초과되었습니다.";
        public static final String IMAGE_DIMENSION_TOO_SMALL_EXCEPTION = "파일의 폭 또는 높이가 너무 작습니다.";
        public static final String INVALID_IMAGE_FORMAT_EXCEPTION = "지원되지 않는 형식이거나 손상된 파일일 수 있습니다";
    }

    public static class Auth{

        @Deprecated
        public static final String KEY_LOAD_FAILED_EXCEPTION = "키 파일을 로드하는데 실패했습니다";

        public static final String OAUTH2_CANNOT_FOUND_ATTRIBUTES = "oauth2.0의 attributes 경로가 달라졌습니다. Provider의 Official Docs에 변경사항이 있는지 점검하십시오.";
        public static final String INVALID_OAUTH2_PROVIDER = "유효하지 않은 Provider 입니다.";
        public static final String INVALID_PRINCIPAL_TYPE = "Principal 내부의 값이 유효하지 않은 타입입니다.";
        public static final String INVALID_ACCESS_PATH_EXCEPTION = "허가되지 않은 접속 URL 입니다. 파라미터가 추가된 URL 을 통해 접속되어야 합니다.";
        public static final String PRIVATE_KEY_MUST_NOT_BE_NULL = "키셋이 비어있습니다. 관리자에게 문의해주세요.";
        public static final String ILLEGAL_COOKIE_NAME = "Access, Refresh 이외의 커스텀 쿠키를 만드려면 시간을 같이 써주세요. CookieName: " ;
        public static final String PROVIDER_IS_NOT_FOUND = "유지하는 Provider 목록에서는 해당 항목이 없습니다. Provider: ";
        public static final String TOKEN_NOT_FOUND = "쿠키 파싱 중 해당 토큰을 찾지 못했습니다.";
        public static final String INVALID_REISSUE_REQUEST = "이미 로그아웃처리 되었습니다.";
        public static final String MULTIPLE_ISSUED_REFRESH_TOKENS = "refresh 토큰이 중복 발행되었습니다.";
        public static final String ACCOUNT_ALREADY_EXISTS = "가입된 계정이 이미 존재합니다.";
        public static final String TOKEN_IS_NOT_VALID = "따라서 파싱이 이루어지지 않았습니다.";
        public static final String ACCESS_TOKEN_IS_NOT_VALID = "accessToken 이 유효하지 않습니다.";
        public static final String DETECTED_INVALID_LOGIN_ROOT = "provider 명시되어있는 사용자가 formLogin 을 시도했습니다.";




    }

    public static class Member{

        public static final String ACCOUNT_ID_IS_NOT_FOUND = "ID가 올바르지 않습니다.";
        public static final String KEY_NUMBER_IS_NOT_FOUND = "Key값이 올바르지 않습니다.";
        public static final String EMAIL_IS_NOT_FOUND = "해당 Email을 소유한 등록된 유저가 없습니다.";
        public static final String USER_ALREADY_EXIST = "이미 등록된 유저가 있습니다. Input: ";
        public static final String USER_NOT_FOUND_EXCEPTION = "회원이 존재하지 않습니다.";
        public static final String LOGIN_REQUIRED_EXCEPTION = "로그인 후 이용 가능합니다.";
        public static final String MEMBER_NOT_FOUND = "대상 멤버가 없습니다.";

    }

    public static class Auction {
        public static final String INVALID_START_PRICE_EXCEPTION = "경매 시작 가격을 확인해주세요.";
        public static final String AUCTION_NOT_EDITABLE_EXCEPTION = "이미 입찰자가 있으므로 수정/삭제가 불가능합니다.";
        public static final String BIDDING_TOO_LOW_EXCEPTION = "입찰 금액이 현재 가격보다 낮습니다.";
        public static final String AUCTION_ALREADY_FINISHED_EXCEPTION = "이미 경매가 종료되었습니다.";
        public static final String INVALID_IMAGE_COUNT_EXCEPTION = "첨부 이미지 수는 최소 1개, 최대 5개여야 합니다.";
        public static final String IMAGE_NOT_FOUND_EXCEPTION = "해당 이미지가 존재하지 않습니다.";
        public static final String AUCTION_NOT_EXIST_EXCEPTION = "경매글이 존재하지 않습니다.";

    }

}
