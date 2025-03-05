package com.alphamaleclub.ucmc.comment.service;


import com.alphamaleclub.ucmc.comment.domain.Comments;
import com.alphamaleclub.ucmc.comment.dto.CommentReq;
import com.alphamaleclub.ucmc.comment.dto.CommentUpdateReq;
import com.alphamaleclub.ucmc.comment.dto.MemberPostDto;

public interface CommentsService {
    public Comments findCommentsById(Long id);
    public Comments createComment(CommentReq comments);
    public void updateComment(CommentUpdateReq comments);
    public Comments deleteComment(Long comments);
    public MemberPostDto findMemberAndPost(Long postId, Long memberId);
}
