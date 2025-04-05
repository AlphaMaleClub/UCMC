package com.alphamaleclub.ucmc.comment.controller;



import com.alphamaleclub.ucmc.comment.dto.CommentReq;
import com.alphamaleclub.ucmc.comment.dto.CommentResponse;
import com.alphamaleclub.ucmc.comment.dto.CommentUpdateReq;
import com.alphamaleclub.ucmc.comment.service.CommentsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/posts/{postId}/comments")
@Slf4j
public class CommentController {
    private final CommentsService commentsService;
    @GetMapping
    public ResponseEntity<?> getComment(@PathVariable Long postId) {
        List<CommentResponse> commentResponses = commentsService.readCommentsByPostId(postId);
        log.info("댓글 목록 {}", commentResponses);
        return ResponseEntity.ok(commentResponses);
    }

    @PostMapping
    public String createComment(@RequestBody CommentReq req) {
        log.info("createComment: {}", req);
        commentsService.createComment(req);
        return "ok";
    }

    @PutMapping
    public String updateComment(@RequestBody CommentUpdateReq req) {
        commentsService.updateComment(req);
        return "ok";
    }

    @DeleteMapping("/{commentId}")
    @PreAuthorize("@memberAuthService.commentAuthCheck(authentication,#commentId)")
    public String deleteComment(@PathVariable Long commentId) {
        commentsService.deleteComment(commentId);
        return "ok";
    }
}
