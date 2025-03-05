package com.alphamaleclub.ucmc.comment.dto;

import lombok.Data;

@Data
public class CommentUpdateReq {
    private String content;
    private Long commentId;

}


