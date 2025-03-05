package com.alphamaleclub.ucmc.comment.mock;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    public Member getMemberById(Long memberId) {
        return memberRepository.findById(memberId).orElse(null);
    }
}
