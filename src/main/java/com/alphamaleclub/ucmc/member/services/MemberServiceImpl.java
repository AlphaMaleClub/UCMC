package com.alphamaleclub.ucmc.member.services;

import com.alphamaleclub.ucmc.member.Repositorty.MemberRepository;
import com.alphamaleclub.ucmc.member.Repositorty.SignUpTempMemberRepository;
import com.alphamaleclub.ucmc.member.domain.Member;
import com.alphamaleclub.ucmc.member.domain.SignUpTempMember;
import com.alphamaleclub.ucmc.member.dto.CustomOAuth2User;
import com.alphamaleclub.ucmc.member.dto.CustomUserDetails;
import com.alphamaleclub.ucmc.member.dto.SignUpRequest;
import com.alphamaleclub.ucmc.member.services.oauth2extractor.Oauth2UserInfoExtractor;
import com.alphamaleclub.ucmc.system.exception.ExceptionMessage;
import com.alphamaleclub.ucmc.system.exception.auth.EmptyRequestException;
import com.alphamaleclub.ucmc.system.exception.auth.InvalidOAuth2ProviderException;
import com.alphamaleclub.ucmc.system.exception.auth.InvalidSignUpRequestException;
import com.alphamaleclub.ucmc.system.exception.member.UserAlreadyExistsException;
import com.alphamaleclub.ucmc.system.exception.member.UserNotFoundException;
import com.alphamaleclub.ucmc.system.util.SecurityUtil;
import jakarta.transaction.Transactional;
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
import java.util.*;


@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class MemberServiceImpl extends DefaultOAuth2UserService implements MemberService, UserDetailsService {

    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;
    private final SignUpTempMemberRepository signUpTempMemberRepository;
    private final List<Oauth2UserInfoExtractor> oauth2UserInfoExtractors;

    @Override
    public Member getLoginedMember() {
        return getMemberById(SecurityUtil.getCurrentMemberId());
    }

    @Override
    public Member getMemberById(Long id) {

        return memberRepository.findById(id).orElseThrow(
                () -> new UserNotFoundException(ExceptionMessage.Member.KEY_NUMBER_IS_NOT_FOUND)
        );

    }

    @Override
    public Member getMemberByEmail(String email){

        return memberRepository.findByEmail(email).orElseThrow(
                ()-> new UserNotFoundException(ExceptionMessage.Member.EMAIL_IS_NOT_FOUND)
        );

    }

    @Override
    public Member getMemberByNickname(String nickname) {
        return memberRepository.findByNickname(nickname).orElseThrow(
                ()-> new UserNotFoundException((ExceptionMessage.Member.MEMBER_NOT_FOUND))
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

        SignUpTempMember tempMember = checkTempUser(signUpRequest);

        checkSignUpIntegrity(signUpRequest);

        signUpRequest.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));

        memberRepository.save(Member.signUpRequestToMember(signUpRequest));

        if(tempMember != null) {
            signUpTempMemberRepository.delete(tempMember);
        }

    }

    private SignUpTempMember checkTempUser(SignUpRequest signUpRequest) {

        //OAuth2.0 그냥 폼 회원가입을 시도한 사람이라면 여기 안탐.
        if (signUpRequest.getProvider().equals("none") && signUpRequest.getTempMemberNumber().equals("-1")) {
            return null;
        }

        Long tempMemberId = Long.valueOf(signUpRequest.getTempMemberNumber());

        SignUpTempMember tempMember = getTempUserById(tempMemberId);

        boolean emailMatch = signUpRequest.getEmail().equals(tempMember.getEmail());
        boolean providerMatch = signUpRequest.getProvider().equals(tempMember.getProvider());

        if (emailMatch && providerMatch) {
            return tempMember;
        }

        throw new InvalidSignUpRequestException(ExceptionMessage.Member.BAD_SIGNUP_REQUEST);

    }


    @Override
    public UserDetails loadUserByUsername(String accountId) throws UsernameNotFoundException {

        Member findMember = this.getMemberByAccountId(accountId);
        String provider = findMember.getProvider().toString();

        if(provider.equals("none")){
            return CustomUserDetails.memberToDetails(findMember, "formLogin");
        }

        throw new UsernameNotFoundException(ExceptionMessage.Auth.DETECTED_INVALID_LOGIN_ROOT);

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

        try {
            findMember = getMemberByEmail(customOauth2User.getEmail());
        }catch (UserNotFoundException e){
            log.info(e.getMessage());
            return customOauth2User;
        }

        return CustomUserDetails.memberToDetails(findMember, "oauth2");
    }

    @Override
    public SignUpTempMember tempUserSave(CustomOAuth2User oAuth2User){

        SignUpTempMember tempUser = SignUpTempMember.builder()
                .provider(oAuth2User.getProvider())
                .realName(oAuth2User.getRealName())
                .nickname(oAuth2User.getNickname())
                .email(oAuth2User.getEmail())
                .mobile(oAuth2User.getMobile())
                .build();

        return signUpTempMemberRepository.save(tempUser);

    };

    @Override
    public SignUpTempMember getTempUserByEmail(String email){

        return signUpTempMemberRepository.findByEmail(email).orElseThrow(
                () -> new UserNotFoundException(ExceptionMessage.Member.EMAIL_IS_NOT_FOUND)
        );

    }


    public SignUpTempMember getTempUserById(Long id){

        return signUpTempMemberRepository.findById(id).orElseThrow(
                () -> new UserNotFoundException(ExceptionMessage.Member.MEMBER_NOT_FOUND)
        );

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

        if(signUpRequest.getProvider().equals("none")){

            //provider 가 none 이면 모든 필드값이 비어있으면 절대 안됨.
            signUpRequest.getFieldMap().forEach((k, v) -> {
                // 문자가 비었는지
                if(v.isEmpty() || v.replaceAll("\\s+", "").isEmpty()){
                    throw new EmptyRequestException(ExceptionMessage.Auth.EMPTY_REQUEST + "EmptyKey: " + k + "EmptyValue: " + v);
                }

                //password 나 nickname 이라면 여기 안해도 됨
                boolean shouldSkipRegex = (k.equals("nickname") || k.equals("password") || k.equals("email"));

                //스킵대상이거나 필드값이 제대로 됐으면 통과
                if(!shouldSkipRegex && !v.matches("^[a-zA-Z0-9_-]*$")){
                        throw new InvalidSignUpRequestException(ExceptionMessage.Member.BAD_SIGNUP_REQUEST + "BadKey: " + k + "BadValue: " + v);
                }

            });

        }

        String accountId = signUpRequest.getAccountId();
        String email = signUpRequest.getEmail();
//        String mobile = signUpRequest.getMobile();

        boolean accountsExists = memberRepository.findByAccountId(accountId).isPresent();
        boolean emailExists = memberRepository.findByEmail(email).isPresent();
//        boolean mobileExists = memberRepository.findByMobile(mobile).isPresent();

        if(accountsExists){
            throw new UserAlreadyExistsException(ExceptionMessage.Member.USER_ALREADY_EXIST + accountId);
        }
        if(emailExists){
            throw new UserAlreadyExistsException(ExceptionMessage.Member.USER_ALREADY_EXIST + email);
        }

//        if(mobile != null && mobileExists){
//            throw new UserAlreadyExistsException(ExceptionMessage.Member.USER_ALREADY_EXIST + mobile);
//        }

    }

}

