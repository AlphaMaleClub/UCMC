package com.alphamaleclub.ucmc.member.services;

import com.alphamaleclub.ucmc.member.Repositorty.MemberRepository;
import com.alphamaleclub.ucmc.member.domain.Member;
import com.alphamaleclub.ucmc.member.dto.CustomUserDetails;
import com.alphamaleclub.ucmc.system.exception.ExceptionMessage;
import com.alphamaleclub.ucmc.system.exception.member.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberServiceImpl extends DefaultOAuth2UserService implements MemberService, UserDetailsService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Member getMemberById(Long id) {

        return memberRepository.findById(id).orElseThrow(
                () -> new UserNotFoundException(ExceptionMessage.Member.KEY_NUMBER_IS_NOT_FOUND)
        );
    }


    public Member getMemberByEmail(String email){

        return memberRepository.findByEmail(email).orElseThrow(
                ()-> new UserNotFoundException(ExceptionMessage.Member.EMAIL_IS_NOT_FOUND)
        );
    }

    @Override
    public Member getMemberByAccountId(String accountId) {

        return memberRepository.findByAccountId(accountId).orElseThrow(
                () -> new UserNotFoundException(ExceptionMessage.Member.ACCOUNT_ID_IS_NOT_FOUND)
        );
    }

    @Override
    public UserDetails loadUserByUsername(String accountId) throws UsernameNotFoundException {

        Member findMember = this.getMemberByAccountId(accountId);
        return CustomUserDetails.memberToDetails(findMember);
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2User oAuth2User = super.loadUser(userRequest);
        Member findMember = null;

        try {
            findMember = getMemberByEmail(oAuth2User.getAttributes().get("email").toString());
        }catch (UserNotFoundException e){
            log.info(e.getMessage());
            return oAuth2User;
        }

        return CustomUserDetails.memberToDetails(findMember);
    }

}

