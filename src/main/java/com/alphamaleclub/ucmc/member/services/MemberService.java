package com.alphamaleclub.ucmc.member.services;

import com.alphamaleclub.ucmc.member.domain.Member;
import com.alphamaleclub.ucmc.member.dto.SignUpRequest;

public interface MemberService {

    Member getMemberById(Long id);

    Member getMemberByEmail(String email);

    Member getMemberByAccountId(String accountId);

    void signUp(SignUpRequest signUpRequest);
}
