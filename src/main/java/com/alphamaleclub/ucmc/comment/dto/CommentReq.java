package com.alphamaleclub.ucmc.comment.dto;

import lombok.Data;

@Data
public class CommentReq {

    private Long author;
    private String content;
    private Long postId;
    private Long parentId;

}
