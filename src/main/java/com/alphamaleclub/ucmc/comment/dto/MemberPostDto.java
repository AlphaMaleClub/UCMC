package com.alphamaleclub.ucmc.comment.dto;


import com.alphamaleclub.ucmc.comment.mock.Member;
import com.alphamaleclub.ucmc.comment.mock.Post;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MemberPostDto {
    private Member member;
    private Post post;

}
