package com.alphamaleclub.ucmc.comment.service;




import com.alphamaleclub.ucmc.comment.dto.CommentReq;
import com.alphamaleclub.ucmc.comment.dto.CommentResponse;
import com.alphamaleclub.ucmc.comment.dto.CommentUpdateReq;
import com.alphamaleclub.ucmc.comment.dto.MemberPostDto;
import com.alphamaleclub.ucmc.comment.entity.Comment;

import java.util.List;

public interface CommentsService {
    public Comment findCommentsById(Long id);
    public List<Comment> findCommentsByPostId(Long postId);
    public List<CommentResponse> readCommentsByPostId(Long postId);
    public Comment createComment(CommentReq comments);
    public void updateComment(CommentUpdateReq comments);
    public Comment deleteComment(Long comments);
    public MemberPostDto findMemberAndPost(Long postId, Long memberId);
}
