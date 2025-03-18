package com.alphamaleclub.ucmc.member.services;

import com.alphamaleclub.ucmc.member.domain.Member;

public interface MemberService {

    Member getMember(Long id);

    Member getMember(String email);



}
