package com.alphamaleclub.ucmc.member.services;

import com.alphamaleclub.ucmc.member.domain.Member;

public interface MemberService {

    Member getMemberById(Long id);

    Member getMemberByEmail(String email);

    Member getMemberByNickname(String nickname);

    Member getMemberByAccountId(String accountId);

}
