package com.alphamaleclub.ucmc.comment.controller;

import com.alphamaleclub.ucmc.comment.dto.CommentReq;
import com.alphamaleclub.ucmc.comment.dto.CommentUpdateReq;
import com.alphamaleclub.ucmc.comment.service.CommentsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class CommentController {
    private final CommentsService commentsService;

    @PostMapping("/comments")
    public String createComment(@RequestBody CommentReq req) {
        commentsService.createComment(req);
        return "ok";
    }

    @PutMapping("/comments")
    public String updateComment(@RequestBody CommentUpdateReq req) {
        commentsService.updateComment(req);
        return "ok";
    }

    @DeleteMapping("/comments/{id}")
    public String deleteComment(@PathVariable Long id) {
        commentsService.deleteComment(id);
        return "ok";
    }
}
