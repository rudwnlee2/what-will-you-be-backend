package com.example.whatwillyoube.whatwillyoube_backend.controller;

import com.example.whatwillyoube.whatwillyoube_backend.domain.*;
import com.example.whatwillyoube.whatwillyoube_backend.dto.MemberRequestDto;
import com.example.whatwillyoube.whatwillyoube_backend.dto.RecommendationInfoRequestDto;
import com.example.whatwillyoube.whatwillyoube_backend.repository.MemberRepository;
import com.example.whatwillyoube.whatwillyoube_backend.repository.RecommendationInfoRepository;
import com.example.whatwillyoube.whatwillyoube_backend.util.JwtUtil;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RecommendationInfoControllerTest {

    @LocalServerPort
    int port;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private RecommendationInfoRepository recommendationInfoRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private String token;
    private Member member;

    @BeforeEach
    void setup() {
        RestAssured.port = port;

        recommendationInfoRepository.deleteAll();
        memberRepository.deleteAll();

        // 회원 생성
        MemberRequestDto memberRequestDto = new MemberRequestDto(
                "testId",
                "Password!1",
                "테스터",
                "test@test.com",
                LocalDate.of(2000, 1, 1),
                Gender.MALE,
                "010-1234-5678",
                "테스트학교"
        );

        member = memberRepository.saveAndFlush(memberRequestDto.toEntity(passwordEncoder));

        // 테스트 시 반드시 member.setRecommendationInfo()를 이용해 연관관계 설정해야 함.
        // (테스트 중 자동 cascade 저장 확인 목적)
        member.setRecommendationInfo(null);

        // 토큰 생성
        token = jwtUtil.createToken(member.getEmail(), member.getName());
    }

    private RecommendationInfoRequestDto createRequestDto() {
        return new RecommendationInfoRequestDto(
                member.getId(),
                "개발자",
                "코딩을 좋아해서",
                "IT",
                JobValue.STABILITY,
                MBTI.ISTJ,
                "프로그래밍",
                "수학",
                Holland.INVESTIGATIVE
        );
    }

    @Test
    @DisplayName("추천 정보 저장 성공")
    void createRecommendationInfo_success() {
        RecommendationInfoRequestDto requestDto = createRequestDto();

        given()
                .log().all()
                .contentType(ContentType.JSON)
                .header("Authorization", token)
                .body(requestDto)
                .when()
                .put("/api/recommendation-info")
                .then()
                .log().all()
                .statusCode(200)
                .body("dream", equalTo("개발자"))
                .body("interest", equalTo("IT"))
                .body("favoriteSubject", equalTo("수학"));
    }

    @Test
    @DisplayName("추천 정보 조회 성공")
    void getRecommendationInfo_success() {
        // given: 먼저 RecommendationInfo를 member에 연결
        RecommendationInfo info = RecommendationInfo.builder()
                .dream("개발자")
                .dreamReason("코딩을 좋아해서")
                .interest("IT")
                .jobValue(JobValue.STABILITY)
                .mbti(MBTI.ISTJ)
                .hobby("프로그래밍")
                .favoriteSubject("수학")
                .holland(Holland.INVESTIGATIVE)
                .member(member)
                .build();

        member.setRecommendationInfo(info);
        memberRepository.saveAndFlush(member);

        given()
                .log().all()
                .header("Authorization", token)
                .when()
                .get("/api/recommendation-info")
                .then()
                .log().all()
                .statusCode(200)
                .body("dream", equalTo("개발자"))
                .body("interest", equalTo("IT"));
    }

    @Test
    @DisplayName("추천 정보 수정 성공")
    void updateRecommendationInfo_success() {
        // given: 기존 데이터 저장 (member 기준 cascade)
        RecommendationInfo info = RecommendationInfo.builder()
                .dream("개발자")
                .dreamReason("코딩을 좋아해서")
                .interest("IT")
                .jobValue(JobValue.STABILITY)
                .mbti(MBTI.ISTJ)
                .hobby("프로그래밍")
                .favoriteSubject("수학")
                .holland(Holland.INVESTIGATIVE)
                .member(member)
                .build();

        member.setRecommendationInfo(info);
        memberRepository.saveAndFlush(member);

        // 수정용 DTO
        RecommendationInfoRequestDto updateDto = new RecommendationInfoRequestDto(
                member.getId(),
                "데이터사이언티스트",
                "데이터 분석이 재미있어서",
                "AI",
                JobValue.ACHIEVEMENT,
                MBTI.ENFP,
                "연구",
                "과학",
                Holland.REALISTIC
        );

        given()
                .log().all()
                .header("Authorization", token)
                .contentType(ContentType.JSON)
                .body(updateDto)
                .when()
                .put("/api/recommendation-info")
                .then()
                .log().all()
                .statusCode(200)
                .body("dream", equalTo("데이터사이언티스트"))
                .body("interest", equalTo("AI"))
                .body("favoriteSubject", equalTo("과학"));
    }

}