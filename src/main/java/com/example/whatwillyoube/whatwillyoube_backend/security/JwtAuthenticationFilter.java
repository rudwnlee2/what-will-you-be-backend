package com.example.whatwillyoube.whatwillyoube_backend.security;

import com.example.whatwillyoube.whatwillyoube_backend.util.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        // 1. 요청 헤더에서 토큰 추출
        String token = jwtUtil.getTokenFromHeader(request);

        // 2. 토큰이 존재하며 유효한지 확인
        if (token != null && jwtUtil.validateToken(token)) {
            // 3. 토큰에서 사용자 정보(email) 추출
            Claims userInfo = jwtUtil.getUserInfoFromToken(token);
            String email = userInfo.getSubject();

            // 4. SecurityContextHolder에 이미 인증 정보가 있는지 확인
            // (이미 처리된 요청일 경우 다시 처리하지 않기 위함)
            if (SecurityContextHolder.getContext().getAuthentication() == null) {
                // 5. 사용자 정보를 기반으로 인증(Authentication) 객체 생성
                try {
                    UserDetails userDetails = userDetailsService.loadUserByUsername(email);
                    Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                    // 6. SecurityContext에 인증 객체를 저장
                    SecurityContext context = SecurityContextHolder.createEmptyContext();
                    context.setAuthentication(authentication);
                    SecurityContextHolder.setContext(context);
                } catch (Exception e) {
                    // 인증 처리 중 예외 발생 시 처리 (예: 사용자를 찾을 수 없음)
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    return;
                }
            }
        }

        // 다음 필터로 요청 전달
        filterChain.doFilter(request, response);
    }
}
