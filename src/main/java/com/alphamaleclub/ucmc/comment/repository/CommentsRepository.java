package com.alphamaleclub.ucmc.comment.repository;

import com.alphamaleclub.ucmc.comment.domain.Comments;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


public interface CommentsRepository extends JpaRepository<Comments, Long> {

}
