package com.alphamaleclub.ucmc.member.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLRestriction("isExpired = false")
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long tokenId;

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

}

