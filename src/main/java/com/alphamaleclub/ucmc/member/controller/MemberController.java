package com.alphamaleclub.ucmc.member.controller;

import com.alphamaleclub.ucmc.member.dto.SignUpRequest;
import com.alphamaleclub.ucmc.member.services.MemberService;
import com.alphamaleclub.ucmc.member.services.TokenManager;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Slf4j
@RestController
@RequestMapping
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final TokenManager tokenManager;

    @GetMapping("/oauth2/initiate")
    public ResponseEntity<?> initHandler(@RequestParam("intent") String intent, @RequestParam("provider") String provider, HttpServletResponse response) throws IOException {

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

    @PostMapping("/logout")
    public ResponseEntity<?> logOut (){

        tokenManager.expireRefreshToken();

        return ResponseEntity.ok("LogOut ok");
    }

    @PostMapping("/api/access-token")
    public ResponseEntity<?> accessTokenReIssue (HttpServletRequest request, HttpServletResponse response) {

        tokenManager.refreshTokenReIssue(request, response);

        return ResponseEntity.ok("Access Token ok");
    }



}
