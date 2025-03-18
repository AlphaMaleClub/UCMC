package com.alphamaleclub.ucmc.member.services;

import com.alphamaleclub.ucmc.member.Repositorty.MemberRepository;
import com.alphamaleclub.ucmc.member.domain.Member;
import com.alphamaleclub.ucmc.system.exception.ExceptionMessage;
import com.alphamaleclub.ucmc.system.exception.member.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@Slf4j
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService, UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public Member getMember(Long id) {

        return memberRepository.findById(id).orElseThrow(
                () -> new UserNotFoundException(ExceptionMessage.Member.KEY_NUMBER_IS_NOT_FOUND)
        );
    }

    @Override
    public Member getMember(String accountId) {

        return memberRepository.findByAccountId(accountId).orElseThrow(
                () -> new UserNotFoundException(ExceptionMessage.Member.ACCOUNT_ID_IS_NOT_FOUND)
        );
    }

    @Override
    public UserDetails loadUserByUsername(String accountId) throws UsernameNotFoundException {
        return (UserDetails) getMember(accountId);
    }

}
