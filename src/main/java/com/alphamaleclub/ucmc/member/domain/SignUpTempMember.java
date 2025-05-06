package com.alphamaleclub.ucmc.member.domain;


import jakarta.persistence.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SignUpTempMember {


    /*
        여기는 임시회원 저장하는 곳입니다.
        계획 상으로는 이 임시회원이 30분이 지난다면, 자동으로 삭제가 될 예정입니다.
    */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String provider;

    private String realName;

    private String nickname;

    @Column(unique = true, nullable = false)
    private String email;

    private String mobile;

    private String role;

    @PrePersist
    public void onCreate(){ this.role = "TempMember"; }

    @Builder
    public SignUpTempMember(String mobile, String email, String nickname, String realName, String provider) {
        this.mobile = mobile;
        this.email = email;
        this.nickname = nickname;
        this.realName = realName;
        this.provider = provider;
    }

}
