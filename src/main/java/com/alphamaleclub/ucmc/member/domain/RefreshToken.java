package com.alphamaleclub.ucmc.member.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long tokenId;

    @Column(nullable = false, unique = true, length = 2048)
    private String token;

    private LocalDateTime createdAt;

    private Boolean isExpired;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @PrePersist
    private void onCreate(){
        if(this.createdAt == null) this.createdAt = LocalDateTime.now();
        if(this.isExpired == null) this.isExpired = false;
    }

    @Builder
    public RefreshToken(Long tokenId, String token, LocalDateTime createdAt, Boolean isExpired, Member member) {
        this.tokenId = tokenId;
        this.token = token;
        this.createdAt = createdAt;
        this.isExpired = isExpired;
        this.member = member;
    }

}

