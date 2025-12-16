package com.example.whatwillyoube.whatwillyoube_backend.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.security.Key;
import java.util.Base64;
import java.util.Date;

/**
 * JWT(Json Web Token) 생성, 검증 및 파싱을 담당하는 유틸리티 클래스
 */
@Component
public class JwtUtil {

    public static final String AUTHORIZATION_HEADER = "Authorization";

    public static final String BEARER_PREFIX = "Bearer ";

    @Value("${jwt.secret.key}")
    private String secretKey;
    @Value("${jwt.token.expiration}")
    private long tokenExpiration;

    private final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
    private Key key;

    @PostConstruct
    public void init() {
        byte[] bytes = Base64.getDecoder().decode(secretKey);
        key = Keys.hmacShaKeyFor(bytes);
    }

    /**
     * 사용자 이메일과 이름을 기반으로 Access Token을 생성
     * @param email 사용자 이메일 (Subject로 사용)
     * @param username 사용자 이름 (Claim에 포함)
     * @return 생성된 JWT 문자열 (Bearer 포함)
     */
    public String createToken(String email, String username) {
        Date now = new Date();
        Date expirationDate = new Date(now.getTime() + tokenExpiration);

        return BEARER_PREFIX +
                Jwts.builder()
                        .setSubject(email)
                        .claim("username", username)
                        .setIssuedAt(now)
                        .setExpiration(expirationDate)
                        .signWith(key, signatureAlgorithm)
                        .compact();
    }

    /**
     * HTTP 요청의 Header에서 JWT 토큰을 추출
     * @param request HTTP 요청 객체
     * @return Bearer를 제외한 순수 토큰 문자열, 없을 경우 null
     */
    public String getTokenFromHeader(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(7);
        }
        return null;
    }

    /**
     * JWT 토큰의 유효성을 검증
     * (위변조, 만료 여부 등 확인)
     * @param token 검증할 토큰
     * @return 유효하면 true, 그렇지 않으면 false
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * 토큰에서 사용자 정보(Claims)를 추출
     * @param token 토큰 문자열
     * @return 사용자 정보가 담긴 Claims 객체
     */
    public Claims getUserInfoFromToken(String token) {
        try {
            return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
        } catch (JwtException | IllegalArgumentException e) {
            return null;
        }
    }

}
