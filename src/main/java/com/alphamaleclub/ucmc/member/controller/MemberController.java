package com.alphamaleclub.ucmc.member.controller;

import com.alphamaleclub.ucmc.member.domain.Member;
import com.alphamaleclub.ucmc.member.dto.CustomUserDetails;
import com.alphamaleclub.ucmc.member.dto.ReissueAccessTokenResponse;
import com.alphamaleclub.ucmc.member.dto.SignUpRequest;
import com.alphamaleclub.ucmc.member.services.CookiesManager;
import com.alphamaleclub.ucmc.member.services.MemberService;
import com.alphamaleclub.ucmc.member.services.TokenManager;
import com.alphamaleclub.ucmc.system.util.SecurityUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Slf4j
@RestController
@RequestMapping
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final TokenManager tokenManager;
    private final CookiesManager cookiesManager;

    @GetMapping("/oauth2/initiate")
    public ResponseEntity<?> initHandler(@RequestParam("intent") String intent, @RequestParam("provider") String provider, HttpServletResponse response) throws IOException {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        log.info("🔐 인증 객체: {}", auth);
        log.info("🔐 isAuthenticated: {}", auth.isAuthenticated());
        log.info("🔐 인증 클래스: {}", auth.getClass().getSimpleName());

        if (auth.isAuthenticated() && auth instanceof CustomUserDetails) {
            log.warn("❌ 이미 로그인된 사용자입니다. 접근 차단.");
            return ResponseEntity.status(403).body("이미 로그인된 사용자는 접근할 수 없습니다.");
        }

        //리디렉션용 쿠키 생성
        Cookie intentCookie = new Cookie("intent", intent);
        Cookie providerCookie = new Cookie("provider", provider);

        intentCookie.setPath("/");
        intentCookie.setHttpOnly(true);
        intentCookie.setMaxAge(180);

        providerCookie.setPath("/");
        providerCookie.setHttpOnly(true);
        providerCookie.setMaxAge(180);

        //response 에 쿠키 추가
        response.addCookie(intentCookie);
        response.addCookie(providerCookie);

        //리디렉션
        response.sendRedirect("/oauth2/authorization/" + provider);

        return ResponseEntity.ok("redirect ok");
    }

    @PostMapping("/api/signup")
    public ResponseEntity<?> signUp (@RequestBody SignUpRequest signUpRequest){

        memberService.signUp(signUpRequest);

        return ResponseEntity.ok("Signup ok");
    }

    @PostMapping("/api/logout")
    public ResponseEntity<?> logOut (HttpServletResponse response){

        Member member = memberService.getMemberById(SecurityUtil.getCurrentMemberId());

        tokenManager.expireRefreshToken(member);

        Cookie clearRefreshTokenCookie = cookiesManager.makeCookie("refreshToken", "",0);

        response.addCookie(clearRefreshTokenCookie);

        return ResponseEntity.ok("LogOut ok");
    }

    @PostMapping("/api/access-token")
    public ResponseEntity<?> accessTokenReIssue (HttpServletRequest request, HttpServletResponse response) {

        String newAccessToken = tokenManager.accessTokenReIssue(request);

        ReissueAccessTokenResponse reissueAccessTokenResponse = ReissueAccessTokenResponse.builder()
                .accessToken(newAccessToken)
                .build();

        if(newAccessToken != null) {
            return ResponseEntity.ok(reissueAccessTokenResponse);
        }

        return ResponseEntity.status(401).body("Refresh token expired or invalid");


    }



}
