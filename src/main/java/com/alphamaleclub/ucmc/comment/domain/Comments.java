package com.alphamaleclub.ucmc.comment.domain;


import com.alphamaleclub.ucmc.comment.mock.Member;
import com.alphamaleclub.ucmc.comment.mock.Post;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Comments {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String content;

    private LocalDateTime createDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Comments parent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id" )
    private Post post;


    private Comments(String conent, Member member, Post post, Comments parent) {
        this.content = conent;
        this.member = member;
        this.post = post;
        this.parent = parent;
    }

    public static Comments toEntity(String conent, Member member, Post post, Comments parent) {
        Comments comments = new Comments(conent, member, post,parent);
        comments.createDate = LocalDateTime.now();
        return comments;
    }

    public void update(String content) {
        this.content = content;
    }

    public void delete() {}


}
