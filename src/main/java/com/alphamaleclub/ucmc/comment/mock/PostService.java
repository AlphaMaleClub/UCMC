package com.alphamaleclub.ucmc.comment.mock;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    public Post getPostById(Long postId) {
        return postRepository.findById(postId).orElse(null);
    }
}
