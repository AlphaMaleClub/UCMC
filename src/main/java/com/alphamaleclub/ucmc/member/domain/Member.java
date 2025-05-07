package com.alphamaleclub.ucmc.member.domain;
import com.alphamaleclub.ucmc.chat.entity.ChatRoom;
import com.alphamaleclub.ucmc.chat.entity.UserChatRoom;
import com.alphamaleclub.ucmc.member.dto.SignUpRequest;
import jakarta.persistence.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
    private String nickname;

    @Column(length = 20)
    private String realName;

    @Column(unique = true, length = 20)
    private String mobile;

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

    // 채팅방과의 중간 테이블 매핑
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<UserChatRoom> userChatRooms = new ArrayList<>();

    public List<ChatRoom> getChatRooms() {
        return userChatRooms.stream()
                .map(UserChatRoom::getChatRoom)
                .collect(Collectors.toList());
    };

    @Builder
    public Member(String accountId, String password, String email, String nickname, String realName, String mobile, Role role, Status status, Provider provider, LocalDateTime createdAt) {

        this.accountId = accountId;
        this.password = password;
        this.nickname = nickname;
        this.realName = realName;
        this.mobile = mobile;
        this.email = email;
        this.role = role;
        this.status = status;
        this.provider = provider;
        this.createdAt = createdAt;

    }

    @PrePersist
    private void onCreate(){

        LocalDateTime now = LocalDateTime.now();

        if(this.createdAt == null) this.createdAt = now;
        if(this.lastLoginAt == null) this.lastLoginAt = now;
        if(this.role == null) this.role = Role.MEMBER;
        if(this.status == null) this.status = Status.ACTIVE;
    }

    @PostLoad
    private void onLoad(){ //나중에 UserDetails 에서 커스텀으로다가 save 한번 해줘야 적용됨.
        this.lastLoginAt = LocalDateTime.now();
    }

    @PostPersist
    private void onPostPersist(){
        log.info("{} 회원가입 완료", this.getAccountId()); //추후 이메일로 가입환영 메일 발송.
    }

    public static Member signUpRequestToMember(SignUpRequest signUpRequest) {
        return Member.builder()
                .accountId((signUpRequest.getProvider().equals("none")) ? signUpRequest.getAccountId() : null)
                .password((signUpRequest.getProvider().equals("none")) ? signUpRequest.getPassword() : null)
                .nickname(signUpRequest.getNickname())
                .email(signUpRequest.getEmail())
//                .mobile(signUpRequest.getMobile())
                .provider(Provider.fromString(signUpRequest.getProvider()))
                .build();
    }

}
