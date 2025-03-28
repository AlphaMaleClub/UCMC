package com.alphamaleclub.ucmc.member.dto;

import com.alphamaleclub.ucmc.member.domain.Member;
import com.alphamaleclub.ucmc.member.domain.Role;
import com.alphamaleclub.ucmc.member.domain.Status;
import jakarta.annotation.sql.DataSourceDefinition;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@Getter
public class CustomUserDetails implements UserDetails, OAuth2User {

    //Field
    private Long userId;

    @Setter
    private String role;

    //속성 추가하기
    private Map<String, Object> attributes;

    private String password;
    private String nickname;
    private boolean isLocked; // 벤당한 회원: True
    private boolean isAccountExpired; //계정의 만료는 딱히 두고있지 않으니 항상 True일 것.
    private boolean isPasswordExpired;
    private boolean isEnabled;

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.nickname;
    }

    @Override
    public String getName() {
        return this.nickname;
    }

    @Override
    public boolean isAccountNonExpired() {
        return !this.isAccountExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !this.isLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return !this.isPasswordExpired;
    }

    @Override
    public boolean isEnabled() {
        return this.isEnabled;
    }

    @Override //권한은 여러개 들어갈 수 있다.
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(this.role));
    }

    @Override
    public Map<String, Object> getAttributes() {
        return this.attributes;
    }

    @Builder
    public CustomUserDetails(Long userId, String password, String nickname, boolean isLocked, boolean isAccountExpired, boolean isPasswordExpired, boolean isEnabled, Role role) {
        this.userId = userId;
        this.password = password;
        this.nickname = nickname;
        this.isLocked = isLocked;
        this.isAccountExpired = isAccountExpired;
        this.isPasswordExpired = isPasswordExpired;
        this.isEnabled = isEnabled;
        this.role = role.toString();
    }

    public static CustomUserDetails memberToDetails(Member member){
        return CustomUserDetails.builder()
                .userId(member.getId())
                .password(member.getPassword())
                .nickname(member.getNickName())
                .isLocked(member.getStatus() == Status.locked)
                .isAccountExpired(member.getStatus() == Status.expired)
                .isPasswordExpired(member.getStatus() == Status.passwordExpired)
                .isEnabled(member.getStatus() == Status.active)
                .role(member.getRole())
                .build();
    }

}

