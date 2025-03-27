package com.alphamaleclub.ucmc.member.domain;

import jakarta.persistence.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Entity
@Slf4j
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {

    //기입정보
    @Id
    @Column(name = "member_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 20)
    private String accountId;

    @Column(unique = true, length = 100)
    private String password;

    @Column(nullable = false, unique = true, length = 50)
    private String email;

    @Column(nullable = false, unique = true, length = 20)
    private String nickName;

    //생성정보
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Provider provider;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime lastLoginAt;

    @OneToMany(mappedBy = "member")
    private List<RefreshToken> refreshTokens;


    @Builder
    public Member(String accountId, String password, String email, String nickName, Role role, Status status, Provider provider, LocalDateTime createdAt) {

        this.accountId = accountId;
        this.password = password;
        this.email = email;
        this.nickName = nickName;
        this.role = role;
        this.status = status;
        this.provider = provider;
        this.createdAt = createdAt;

    }

    @PrePersist
    private void onCreate(){
        if(this.createdAt == null) this.createdAt = LocalDateTime.now();
    }

    @PostLoad
    private void onLoad(){ //나중에 UserDetails 에서 커스텀으로다가 save 한번 해줘야 적용됨.
        this.lastLoginAt = LocalDateTime.now();
    }

    @PostPersist
    private void onPostPersist(){
        log.info("{} 회원가입 완료", this.getAccountId()); //추후 이메일로 가입환영 메일 발송.
    }

}
