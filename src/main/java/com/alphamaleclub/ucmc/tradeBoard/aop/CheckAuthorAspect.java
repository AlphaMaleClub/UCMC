package com.alphamaleclub.ucmc.tradeBoard.aop;


import com.alphamaleclub.ucmc.member.domain.Member;
import com.alphamaleclub.ucmc.member.services.MemberService;
import com.alphamaleclub.ucmc.system.exception.ExceptionMessage;
import com.alphamaleclub.ucmc.system.exception.tradeboard.NotAuthorException;
import com.alphamaleclub.ucmc.system.util.SecurityUtil;
import com.alphamaleclub.ucmc.tradeBoard.anotation.AuthorOnly;
import com.alphamaleclub.ucmc.tradeBoard.domain.TradePost;
import com.alphamaleclub.ucmc.tradeBoard.dto.TradePostAndProductImageResponse;
import com.alphamaleclub.ucmc.tradeBoard.service.TradePostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class CheckAuthorAspect {

    private final MemberService memberService;
    private final TradePostService tradePostService;

    @Around("@annotation(authorOnly)")
    public Object CheckAuthor(ProceedingJoinPoint joinPoint, AuthorOnly authorOnly) throws Throwable {

        log.info("어노테이션 탈취 성공");

        Long postId = null;

        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();

        for (int i = 0; i < method.getParameters().length; i++) {

            String parameterName = method.getParameters()[i].getName();

            if (parameterName.equals("postId")) {
                postId = (Long) joinPoint.getArgs()[i];
                break;
            }

        }

        if (postId == null) {
            throw new NotAuthorException(ExceptionMessage.Trade.POST_ID_IS_NULL);
        }

        Long currentMemberId = SecurityUtil.getCurrentMemberId();
        // 로그인한 멤버
        Member loginedMember = memberService.getMemberById(currentMemberId);
        log.info("로그인 member = {}", loginedMember);
        TradePost targetPost = tradePostService.getTradePost(postId);

        Member targetPostMember = targetPost.getMember();
        log.info("targetPostMember = {}", targetPostMember);

        if (loginedMember.equals(targetPostMember)) {
            log.info("기존 메서드 실행");
            return joinPoint.proceed();
        }

        log.info("실패");
        throw new NotAuthorException(ExceptionMessage.Trade.CHECK_AUTHOR_EXCEPTION);

    }

}
