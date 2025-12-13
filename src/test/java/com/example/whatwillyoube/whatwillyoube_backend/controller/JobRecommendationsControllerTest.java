package com.example.whatwillyoube.whatwillyoube_backend.controller;

import com.example.whatwillyoube.whatwillyoube_backend.domain.*;
import com.example.whatwillyoube.whatwillyoube_backend.dto.MemberRequestDto;
import com.example.whatwillyoube.whatwillyoube_backend.repository.JobRecommendationsRepository;
import com.example.whatwillyoube.whatwillyoube_backend.repository.MemberRepository;
import com.example.whatwillyoube.whatwillyoube_backend.repository.RecommendationInfoRepository;
import com.example.whatwillyoube.whatwillyoube_backend.util.JwtUtil;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.Matchers.equalTo;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class JobRecommendationsControllerTest {

    @LocalServerPort
    int port;

    @Autowired
    private JobRecommendationsRepository jobRecommendationsRepository;

    @Autowired
    private RecommendationInfoRepository recommendationInfoRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    private Member member;
    private String token;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;

        jobRecommendationsRepository.deleteAll();
        recommendationInfoRepository.deleteAll();
        memberRepository.deleteAll();

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
        token = jwtUtil.createToken(member.getEmail(), member.getName());

        //추천 정보 등록 + 연관관계 설정
        RecommendationInfo recommendationInfo = RecommendationInfo.builder()
                .member(member)
                .dream("개발자")
                .dreamReason("코딩을 좋아해서")
                .interest("IT")
                .jobValue(JobValue.STABILITY)
                .mbti(MBTI.ISTJ)
                .hobby("프로그래밍")
                .favoriteSubject("수학")
                .holland(Holland.INVESTIGATIVE)
                .build();

        recommendationInfoRepository.saveAndFlush(recommendationInfo);

        member.setRecommendationInfo(recommendationInfo); //관계 연결
        memberRepository.saveAndFlush(member);
    }

    @Test
    @DisplayName("추천 리스트 조회 성공")
    void getMyRecommendationsList_success() {
        // given: 추천 기록 미리 저장
        JobRecommendations job = JobRecommendations.builder()
                .jobName("백엔드 개발자")
                .jobSum("서버 개발 및 유지보수")
                .way("Spring Boot 학습")
                .major("컴퓨터공학")
                .certificate("정보처리기사")
                .pay("중상")
                .jobProspect("좋음")
                .knowledge("Java, DB, 네트워크")
                .jobEnvironment("사무실 근무")
                .jobValues("안정성")
                .reason("꾸준히 성장 가능")
                .member(member)
                .build();

        jobRecommendationsRepository.saveAndFlush(job);

        given()
                .log().all()
                .header("Authorization", token)
        .when()
                .get("/api/job-recommendations?page=0&size=12")
        .then()
                .log().all()
                .statusCode(200)
                .body("content[0].jobName", equalTo("백엔드 개발자"));
    }

    @Test
    @DisplayName("직업 추천 상세 조회 성공")
    void getDetailRecommendation_success() {
        // given: 추천 기록 미리 저장
        JobRecommendations job = JobRecommendations.builder()
                .jobName("데이터 분석가")
                .jobSum("데이터 기반 의사결정 지원")
                .way("통계 및 Python 학습")
                .major("통계학")
                .certificate("ADsP")
                .pay("중")
                .jobProspect("매우 좋음")
                .knowledge("통계, Python, SQL")
                .jobEnvironment("하이브리드 근무")
                .jobValues("성취감")
                .reason("분석을 통한 문제 해결이 흥미로움")
                .member(member)
                .build();

        job = jobRecommendationsRepository.saveAndFlush(job);

        given()
                .log().all()
                .header("Authorization", token)
        .when()
                .get("/api/job-recommendations/" + job.getId())
        .then()
                .log().all()
                .statusCode(200)
                .body("jobName", equalTo("데이터 분석가"))
                .body("relatedMajors", equalTo("통계학"));
    }

    @Test
    @DisplayName("추천 기록 삭제 성공")
    void deleteJobRecommendation_success() {
        // given: 추천 기록 미리 저장
        JobRecommendations job = JobRecommendations.builder()
                .jobName("프론트엔드 개발자")
                .jobSum("웹 화면 구현 및 UI 개발")
                .way("React 학습")
                .major("컴퓨터공학")
                .certificate("정보처리기사")
                .pay("중상")
                .jobProspect("보통")
                .knowledge("HTML, CSS, JS")
                .jobEnvironment("사무실 근무")
                .jobValues("창의성")
                .reason("시각적 결과물이 흥미로움")
                .member(member)
                .build();

        job = jobRecommendationsRepository.saveAndFlush(job);

        given()
                .log().all()
                .header("Authorization", token)
        .when()
                .delete("/api/job-recommendations/" + job.getId())
        .then()
                .log().all()
                .statusCode(200);

        // then: 실제 DB에서 삭제되었는지 확인
        assertThat(jobRecommendationsRepository.findById(job.getId())).isEmpty();
    }

}