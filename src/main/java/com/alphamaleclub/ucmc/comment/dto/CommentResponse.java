package com.alphamaleclub.ucmc.comment.dto;

import com.alphamaleclub.ucmc.comment.entity.Comment;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class CommentResponse {
    public Long id;
    public String content;
    public LocalDateTime createTime;
    public String author;
    public List<CommentResponse> children;

    public CommentResponse(Comment comments) {
        this.id = comments.getId();
        this.content = comments.getContent();
        this.author = comments.getMember().getNickName();
        this.createTime = comments.getCreateAt();
        this.children = comments.getChildren().stream()
                .map(CommentResponse::new)
                .collect(Collectors.toList());
    }

}
