package com.alphamaleclub.ucmc.member.dto;

import com.alphamaleclub.ucmc.member.domain.Provider;
import com.alphamaleclub.ucmc.member.validation.EnumValid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;


import java.util.Map;
import java.util.UUID;

@Data
@Builder
@Validated
@AllArgsConstructor
@RequiredArgsConstructor
public class SignUpRequest {

    private String accountId;
    private String password;

    @Pattern(regexp = "^[a-zA-Z0-9가-힣_-]+$", message = "닉네임은 영문자, 숫자, 밑줄(_), 하이픈(-)만 사용할 수 있습니다.")
    @Size(min=2, max=16)
    private String nickname;

    @Email(message = "이메일 형식이 아닙니다.")
    @NotBlank(message = "이메일은 필수입니다.")
    private String email;


    @NotBlank(message = "프로바이더는 필수입니다.")
    @EnumValid(enumClass = Provider.class, message = "지원하지 않는 provider")
    private String provider;

    @NotBlank
    private String tempMemberNumber;

//    private String mobile;

    /*
        이 란은 유효성검사를 좀 더 깔끔하게 하기위해 추가했습니다.
        SignUpRequest 에 필드가 추가된다면, 여기서 맵에 에 추가해줘야합니다.
    */
    public Map<String, String> getFieldMap() {
        return Map.of("accountId", this.accountId,
                "password", this.password,
                "nickname", this.nickname,
                "email", this.email,
                "provider", this.provider,
                "tempMemberNumber", this.tempMemberNumber
//                "mobile", this.mobile

                /*
                    디버깅 메모, Map 의 제네릭이 <String, String> 인데 Request 에서 못받는 값이 있으면 Null 이 들어감.
                    그러면 Map.of() 할 때 mobile 이 null 이고 NPE 가 터짐. Null 허용하려면 new HashMap<String, String> 으로 리턴할 것,
                */
        );

    }

    public static SignUpRequest fromCustomOAuth2UserTestOnly(CustomOAuth2User user) {
        return SignUpRequest.builder()
                .accountId(UUID.randomUUID().toString().replace("-", "").substring(0, 12))
                .password(UUID.randomUUID().toString())
                .nickname("TestName")
                .email(user.getEmail())
                .provider(user.getProvider())
//                .mobile(user.getMobile())
                .build();
    }

}
