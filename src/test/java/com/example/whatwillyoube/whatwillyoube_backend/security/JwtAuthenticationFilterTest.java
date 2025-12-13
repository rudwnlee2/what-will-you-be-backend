package com.example.whatwillyoube.whatwillyoube_backend.security;

import com.example.whatwillyoube.whatwillyoube_backend.domain.Gender;
import com.example.whatwillyoube.whatwillyoube_backend.domain.Member;
import com.example.whatwillyoube.whatwillyoube_backend.util.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    private final String email = "test@example.com";
    private final String validToken = "valid-token";

    @BeforeEach
    void clearContext() {
        // 매 테스트 실행 전 SecurityContext 초기화
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("유효한 토큰이 있으면 SecurityContext에 인증 정보가 저장된다")
    void doFilterInternal_validToken_setsAuthentication() throws Exception {
        // given
        Claims claims = mock(Claims.class);
        when(claims.getSubject()).thenReturn(email);

        when(jwtUtil.getTokenFromHeader(request)).thenReturn(validToken);
        when(jwtUtil.validateToken(validToken)).thenReturn(true);
        when(jwtUtil.getUserInfoFromToken(validToken)).thenReturn(claims);

        Member member = Member.builder()
                .loginId("testId")
                .password("encodedPassword")
                .name("테스터")
                .email(email)
                .birth(LocalDate.now())
                .gender(Gender.MALE)
                .phone("010-0000-0000")
                .school("테스트학교")
                .build();

        UserDetails userDetails = new UserDetailsImpl(member);
        when(userDetailsService.loadUserByUsername(email)).thenReturn(userDetails);

        // when
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // then
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(authentication);
        assertEquals(email, authentication.getName());
        verify(userDetailsService, times(1)).loadUserByUsername(email);
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    @DisplayName("토큰이 없으면 인증을 시도하지 않고 다음 필터로 넘어간다")
    void doFilterInternal_noToken_doesNotAuthenticate() throws Exception {
        // given
        when(jwtUtil.getTokenFromHeader(request)).thenReturn(null);

        // when
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // then
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(userDetailsService, never()).loadUserByUsername(anyString());
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    @DisplayName("토큰이 유효하지 않으면 인증을 시도하지 않고 다음 필터로 넘어간다")
    void doFilterInternal_invalidToken_doesNotAuthenticate() throws Exception {
        // given
        when(jwtUtil.getTokenFromHeader(request)).thenReturn(validToken);
        when(jwtUtil.validateToken(validToken)).thenReturn(false);

        // when
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // then
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(userDetailsService, never()).loadUserByUsername(anyString());
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    @DisplayName("UserDetailsService 호출 중 예외 발생 시 401 응답 반환")
    void doFilterInternal_userDetailsServiceThrowsException_setsUnauthorized() throws Exception {
        // given
        Claims claims = mock(Claims.class);
        claims.setSubject(email);

        when(jwtUtil.getTokenFromHeader(request)).thenReturn(validToken);
        when(jwtUtil.validateToken(validToken)).thenReturn(true);
        when(jwtUtil.getUserInfoFromToken(validToken)).thenReturn(claims);
        when(userDetailsService.loadUserByUsername(email)).thenThrow(new RuntimeException("DB error"));

        // when
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // then
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(response, times(1)).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verify(filterChain, never()).doFilter(request, response);
    }
}