package com.alphamaleclub.ucmc.member.domain;

import jakarta.persistence.*;
import lombok.*;
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

    @Setter
    private boolean isExpired;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @PrePersist
    private void onCreate(){
        if(this.createdAt == null) this.createdAt = LocalDateTime.now();
        isExpired = false;
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

