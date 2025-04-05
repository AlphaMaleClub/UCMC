package com.alphamaleclub.ucmc.comment.repository;

import com.alphamaleclub.ucmc.comment.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface CommentsRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByPostIdAndParentIsNull(Long postId);
}

