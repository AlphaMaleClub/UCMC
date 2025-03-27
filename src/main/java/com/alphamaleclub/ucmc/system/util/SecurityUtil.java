package com.alphamaleclub.ucmc.system.util;

import com.alphamaleclub.ucmc.member.dto.CustomUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;


@Component
public class SecurityUtil {

    public static Long getCurrentMemberId() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getPrincipal() == "anonymousUser") {
            return null;
        }
        return ((CustomUserDetails) authentication.getPrincipal()).getUserId();
    }

}
