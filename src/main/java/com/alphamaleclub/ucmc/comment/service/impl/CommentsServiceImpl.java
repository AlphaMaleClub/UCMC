package com.alphamaleclub.ucmc.comment.service.impl;


import com.alphamaleclub.ucmc.comment.domain.Comments;
import com.alphamaleclub.ucmc.comment.dto.CommentReq;
import com.alphamaleclub.ucmc.comment.dto.CommentUpdateReq;
import com.alphamaleclub.ucmc.comment.dto.MemberPostDto;
import com.alphamaleclub.ucmc.comment.mock.Member;
import com.alphamaleclub.ucmc.comment.mock.MemberService;
import com.alphamaleclub.ucmc.comment.mock.Post;
import com.alphamaleclub.ucmc.comment.mock.PostService;
import com.alphamaleclub.ucmc.comment.repository.CommentsRepository;
import com.alphamaleclub.ucmc.comment.service.CommentsService;
import com.alphamaleclub.ucmc.system.exception.comment.CommentNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentsServiceImpl implements CommentsService {
    private final MemberService memberService;
    private final PostService postService;
    private final CommentsRepository commentsRepository;

    @Override
    @Transactional(readOnly = true)
    public Comments findCommentsById(Long id) {
        return commentsRepository.findById(id).orElseThrow(
                () -> new CommentNotFoundException("유효하지 않은 댓글 번호입니다.") // 나중에 에러 핸들러에 메시지 추가
        );
    }

    @Override
    @Transactional
    public Comments createComment(CommentReq comments) {
        MemberPostDto findMemberAndPost = findMemberAndPost(comments.getPostId(), comments.getAuthor());
        if(comments.getParentId() == null) {
            return commentsRepository.save(Comments.toEntity(comments.getContent(),findMemberAndPost.getMember(), findMemberAndPost.getPost(), null));
        } else {
            return commentsRepository.save(Comments.toEntity(comments.getContent(),findMemberAndPost.getMember(), findMemberAndPost.getPost(), findCommentsById(comments.getParentId())));
        }
    }

    @Override
    @Transactional
    public Comments deleteComment(Long id) {
        // 나중에 소프트 delete or 그냥 삭제할지 결졍되면 구현
        return null;
    }

    @Override
    @Transactional
    public void updateComment(CommentUpdateReq comments) {
        findCommentsById(comments.getCommentId()).update(comments.getContent());
    }

    @Override
    @Transactional(readOnly = true)
    public MemberPostDto findMemberAndPost(Long postId, Long memberId) {
        Member member = memberService.getMemberById(memberId);
        Post post = postService.getPostById(postId);
        return new MemberPostDto(member, post);
    }
}
