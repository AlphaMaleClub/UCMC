package com.alphamaleclub.ucmc.comment.mock;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Long id;
}
