package com.example.whatwillyoube.whatwillyoube_backend.util;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtUtilTest {

    @InjectMocks
    private JwtUtil jwtUtil;

    @Mock
    private HttpServletRequest request; // Mock 객체

    private final String testSecretKey =
            "c3ByaW5nYm9vdC1qd3QtdHV0b3JpYWwtc2VjcmV0LWtleS1mb3ItdGVzdGluZw=="; // Base64 인코딩된 key
    private final long testTokenExpiration = 3600000L; // 1시간
    private final String testEmail = "test@example.com";

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(jwtUtil, "secretKey", testSecretKey);
        ReflectionTestUtils.setField(jwtUtil, "tokenExpiration", testTokenExpiration);
        jwtUtil.init(); // @PostConstruct 수동 호출
    }

    private void mockHeader(String value) {
        when(request.getHeader(JwtUtil.AUTHORIZATION_HEADER)).thenReturn(value);
    }

    @Test
    @DisplayName("JWT 토큰 생성 시 Bearer 접두사와 subject(email), username 포함")
    void createToken_containsEmailAndUsername() {
        // given
        String testEmail = "test@test.com";
        String testUsername = "홍길동";

        // when
        String tokenWithPrefix = jwtUtil.createToken(testEmail, testUsername);
        String token = tokenWithPrefix.substring(7);

        // then
        assertTrue(tokenWithPrefix.startsWith(JwtUtil.BEARER_PREFIX));

        Claims claims = jwtUtil.getUserInfoFromToken(token);
        assertEquals(testEmail, claims.getSubject());            // sub 확인
        assertEquals(testUsername, claims.get("username"));      // username claim 확인
    }

    @Test
    @DisplayName("HTTP 헤더에서 토큰 추출 성공")
    void getTokenFromHeader_success() {
        // given
        String token = "test-jwt-token";
        mockHeader(JwtUtil.BEARER_PREFIX + token);

        // when
        String extractedToken = jwtUtil.getTokenFromHeader(request);

        // then
        assertEquals(token, extractedToken);
    }

    @Test
    @DisplayName("HTTP 헤더가 없거나 형식이 잘못된 경우 null 반환")
    void getTokenFromHeader_failure() {
        // case 1: 헤더가 null
        mockHeader(null);
        assertNull(jwtUtil.getTokenFromHeader(request));

        // case 2: 접두사가 없는 잘못된 값
        mockHeader("invalid-token-format");
        assertNull(jwtUtil.getTokenFromHeader(request));
    }

    @Test
    @DisplayName("JWT 토큰 검증 성공")
    void validateToken_success() {
        // given
        String token = jwtUtil.createToken("test@test.com", "홍길동").substring(7);

        // when
        boolean isValid = jwtUtil.validateToken(token);

        // then
        assertTrue(isValid);
    }

    @Test
    @DisplayName("유효하지 않은 JWT 토큰 검증 실패")
    void validateToken_failure() {
        // given
        String invalidToken = "this-is-an-invalid-jwt-token";

        // when
        boolean isValid = jwtUtil.validateToken(invalidToken);

        // then
        assertFalse(isValid);
    }

    @Test
    @DisplayName("만료된 JWT 토큰 검증 실패")
    void validateToken_expiredToken() throws InterruptedException {
        // given: 만료 시간을 아주 짧게 설정
        ReflectionTestUtils.setField(jwtUtil, "tokenExpiration", 1L);
        jwtUtil.init();

        String token = jwtUtil.createToken("test@test.com", "홍길동").substring(7);
        Thread.sleep(5); // 만료될 때까지 잠깐 대기

        // when
        boolean isValid = jwtUtil.validateToken(token);

        // then
        assertFalse(isValid);
    }

    @Test
    @DisplayName("JWT 토큰에서 사용자 정보 추출 성공")
    void getUserInfoFromToken_success() {
        // given
        String testEmail = "test@test.com";
        String testUsername = "홍길동";
        String token = jwtUtil.createToken(testEmail, testUsername).substring(7);

        // when
        Claims claims = jwtUtil.getUserInfoFromToken(token);

        // then
        assertNotNull(claims);
        assertEquals(testEmail, claims.getSubject());           // 이메일 확인
        assertEquals(testUsername, claims.get("username"));     // 이름 확인
    }

    @Test
    @DisplayName("유효하지 않은 토큰에서 사용자 정보 추출 실패 시 null 반환")
    void getUserInfoFromToken_failure() {
        // given
        String invalidToken = "this-is-an-invalid-jwt-token";

        // when
        Claims claims = jwtUtil.getUserInfoFromToken(invalidToken);

        // then
        assertNull(claims);
    }
}