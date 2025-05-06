package com.alphamaleclub.ucmc.member.services;

import com.alphamaleclub.ucmc.member.domain.Member;
import com.alphamaleclub.ucmc.member.domain.SignUpTempMember;
import com.alphamaleclub.ucmc.member.dto.CustomOAuth2User;
import com.alphamaleclub.ucmc.member.dto.CustomUserDetails;
import com.alphamaleclub.ucmc.member.dto.SignUpRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface MemberService {
    Member getLoginedMember();

    Member getMemberById(Long id);

    Member getMemberByEmail(String email);

    Member getMemberByNickname(String nickname);

    Member getMemberByAccountId(String accountId);

    SignUpTempMember tempUserSave(CustomOAuth2User customOAuth2User);

    SignUpTempMember getTempUserByEmail(String email);

    void signUp(SignUpRequest signUpRequest);

}
