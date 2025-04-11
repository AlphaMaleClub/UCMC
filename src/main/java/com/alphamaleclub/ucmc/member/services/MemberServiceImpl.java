package com.alphamaleclub.ucmc.member.services;

import com.alphamaleclub.ucmc.member.Repositorty.MemberRepository;
import com.alphamaleclub.ucmc.member.Repositorty.RefreshTokenRepository;
import com.alphamaleclub.ucmc.member.domain.Member;
import com.alphamaleclub.ucmc.member.dto.CustomOAuth2User;
import com.alphamaleclub.ucmc.member.dto.CustomUserDetails;
import com.alphamaleclub.ucmc.member.dto.SignUpRequest;
import com.alphamaleclub.ucmc.member.services.oauth2extractor.Oauth2UserInfoExtractor;
import com.alphamaleclub.ucmc.system.exception.ExceptionMessage;
import com.alphamaleclub.ucmc.system.exception.auth.InvalidOAuth2ProviderException;
import com.alphamaleclub.ucmc.system.exception.auth.MissingTokenException;
import com.alphamaleclub.ucmc.system.exception.member.UserAlreadyExistsException;
import com.alphamaleclub.ucmc.system.exception.member.UserNotFoundException;
import com.alphamaleclub.ucmc.system.util.SecurityUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.token.TokenService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import java.util.*;


@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class MemberServiceImpl extends DefaultOAuth2UserService implements MemberService, UserDetailsService {

    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;
    private final List<Oauth2UserInfoExtractor> oauth2UserInfoExtractors;

    public Member getLoginedMember() {
        return getMemberById(SecurityUtil.getCurrentMemberId());
    }

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
    public void signUp(SignUpRequest signUpRequest) {

        checkSignUpIntegrity(signUpRequest);

        signUpRequest.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));

        memberRepository.save(Member.signUpRequestToMember(signUpRequest));

    }



    @Override
    public UserDetails loadUserByUsername(String accountId) throws UsernameNotFoundException {

        Member findMember = this.getMemberByAccountId(accountId);
        return CustomUserDetails.memberToDetails(findMember, "formLogin");

    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        Member findMember;
        OAuth2User oAuth2User = super.loadUser(userRequest);
        String provider = extractProvider(userRequest);

        CustomOAuth2User customOauth2User =
                oauth2UserInfoExtractors.stream()
                .filter(e -> e.supports(provider))
                .findFirst()
                .orElseThrow(()-> new InvalidOAuth2ProviderException(ExceptionMessage.Auth.INVALID_OAUTH2_PROVIDER + ": " + provider))
                .extract(oAuth2User, provider);

//        테스트로그
//        showMeTheAttributes(oAuth2User);
//        log.info("Custom OAuth2User : {}", customOauth2User);

        try {
            findMember = getMemberByEmail(customOauth2User.getEmail());
        }catch (UserNotFoundException e){
            return customOauth2User;
        }

        return CustomUserDetails.memberToDetails(findMember, "oauth2");
    }

    private String extractProvider(OAuth2UserRequest userRequest) {
        return userRequest.getClientRegistration().getRegistrationId();
    }

    //각 provider 의 발급한 정보를 꺼내서 로그로 보여주는 메서드(테스트용임)
    private static void showMeTheAttributes(OAuth2User oAuth2User) {
        oAuth2User.getAttributes().keySet().forEach(key -> {
            log.info("keyName = {} , Value = {}",key, Objects.requireNonNull(oAuth2User.getAttribute(key)));
        });
    }

    private void checkSignUpIntegrity(SignUpRequest signUpRequest) {

        String accountId = signUpRequest.getAccountId();
        String email = signUpRequest.getEmail();
        String mobile = signUpRequest.getMobile();

        boolean accountsExists = memberRepository.findByAccountId(accountId).isPresent();
        boolean emailExists = memberRepository.findByEmail(email).isPresent();
        boolean mobileExists = memberRepository.findByMobile(mobile).isPresent();

        if(accountsExists){
            throw new UserAlreadyExistsException(ExceptionMessage.Member.USER_ALREADY_EXIST + accountId);
        }
        if(emailExists){
            throw new UserAlreadyExistsException(ExceptionMessage.Member.USER_ALREADY_EXIST + email);
        }
        if(mobile != null && mobileExists){
            throw new UserAlreadyExistsException(ExceptionMessage.Member.USER_ALREADY_EXIST + mobile);
        }

    }

}

