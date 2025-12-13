package com.example.whatwillyoube.whatwillyoube_backend.controller;

import com.example.whatwillyoube.whatwillyoube_backend.domain.Gender;
import com.example.whatwillyoube.whatwillyoube_backend.domain.Member;
import com.example.whatwillyoube.whatwillyoube_backend.dto.LoginRequestDto;
import com.example.whatwillyoube.whatwillyoube_backend.dto.MemberRequestDto;
import com.example.whatwillyoube.whatwillyoube_backend.repository.MemberRepository;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
//@Transactional
class MemberControllerTest2 {

    @LocalServerPort
    int port;
    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setup() {
        RestAssured.port = port; // 테스트마다 랜덤 포트를 RestAssured에 설정
        memberRepository.deleteAll();

        // "existingUser" 라는 아이디를 가진 회원을 생성 후 저장 -> 아이디 중복 확인
        Member member = new Member(
                "existingUser",
                passwordEncoder.encode("Password!1"),
                "기존유저",
                "exist@test.com",
                LocalDate.of(2000, 1, 1),
                Gender.MALE,
                "010-0000-0000",
                "테스트학교"
        );
        memberRepository.save(member);
    }

//    @AfterEach
//    void tearDown() {
//        memberRepository.deleteAll();
//    }

    private MemberRequestDto createTestMemberDto() {
        return new MemberRequestDto(
                "testId",
                "Password!1",
                "테스터",
                "test@test.com",
                LocalDate.of(2000, 1, 1),
                Gender.MALE,
                "010-1234-5678",
                "테스트학교"
        );
    }

    private String login(LoginRequestDto loginRequestDto) {
        return given()
                    .contentType(ContentType.JSON)
                    .body(loginRequestDto)
                .when()
                    .post("/api/members/login")
                .then()
                    .statusCode(200)
                    .header("Authorization", notNullValue()) // 헤더에 토큰이 존재하는지 확인
                    .extract()
                    .header("Authorization"); // 실제 토큰 값 추출
    }

    @Test
    @Order(1)
    @DisplayName("아이디 중복체크 성공 (존재하는 아이디)")
    void checkLoginIdDuplicate_success() {
        String existingLoginId = "existingUser";

        given()
                .log().all()
        .when()
                .get("/api/members/check-loginId/{loginId}", existingLoginId)
        .then()
                .log().all()
                .statusCode(200)
                .body("exists", equalTo(true)); // "existingUser"는 이미 존재하므로 true
    }

    @Test
    @Order(2)
    @DisplayName("회원가입 성공")
    void signUp_success() {
        MemberRequestDto memberRequestDto = createTestMemberDto();

        given()
                .log().all()
                .contentType(ContentType.JSON)
                .body(memberRequestDto).
        when()
                .post("/api/members/signup").
        then()
                .log().all()
                .statusCode(201)
                .body("loginId", equalTo("testId"))
                .body("email", equalTo("test@test.com"));

    }

    @Test
    @Order(3)
    @DisplayName("로그인 성공 후 토큰 발급")
    void login_success() {

        MemberRequestDto memberDto = createTestMemberDto();
//        Member member = memberDto.toEntity(passwordEncoder);
        memberRepository.save(memberDto.toEntity(passwordEncoder));

        LoginRequestDto loginRequestDto = new LoginRequestDto("testId", "Password!1");

        String token = login(loginRequestDto);

        assertThat(token).startsWith("Bearer "); // JWT 포맷 검증
    }

    @Test
    @Order(4)
    @DisplayName("내 정보 조회 성공")
    void myPage_success() {

        MemberRequestDto memberDto = createTestMemberDto();
        memberRepository.save(memberDto.toEntity(passwordEncoder));

        // 1) 로그인 요청 → 토큰 발급
        LoginRequestDto loginRequest = new LoginRequestDto("testId", "Password!1");

        String token = login(loginRequest);

        // 2) 발급받은 토큰을 Authorization 헤더에 담아 내 정보 조회
        given()
                .log().all()
                .header("Authorization", token)
        .when()
                .get("/api/members/me")
        .then()
                .log().all()
                .statusCode(200)
                .body("loginId", equalTo("testId"))
                .body("email", equalTo("test@test.com"))
                .body("name", equalTo("테스터"))
                .body("gender", equalTo("MALE"))
                .body("birth", equalTo("2000-01-01"))
                .body("phone", equalTo("010-1234-5678"))
                .body("school", equalTo("테스트학교"));

    }

    @Test
    @Order(5)
    @DisplayName("내 정보 수정 성공")
    void updateMyPage_success() {
        MemberRequestDto memberRequestDto = createTestMemberDto();
        memberRepository.save(memberRequestDto.toEntity(passwordEncoder));

        LoginRequestDto loginRequest = new LoginRequestDto("testId", "Password!1");
        String token = login(loginRequest);


        MemberRequestDto updateMemberDto = new MemberRequestDto(
                "testId",
                "Password!1",
                "이름 변경",
                "test2@naver.com",
                LocalDate.of(2001, 11, 1),
                Gender.MALE,
                "010-1234-5678",
                "테스트학교"
        );

        given()
                .log().all()
                .contentType(ContentType.JSON)
                .header("Authorization", token)
                .body(updateMemberDto)
        .when()
                .patch("/api/members/me")
        .then()
                .log().all()
                .statusCode(200)
                .body("loginId", equalTo("testId"))
                .body("email", equalTo("test2@naver.com"))
                .body("name", equalTo("이름 변경"))
                .body("gender", equalTo("MALE"))
                .body("birth", equalTo("2001-11-01"))
                .body("phone", equalTo("010-1234-5678"))
                .body("school", equalTo("테스트학교"));

    }

    @Test
    @Order(6)
    @DisplayName("회원 탈퇴 성공")
    void deleteMember_success() {
        MemberRequestDto memberRequestDto = createTestMemberDto();
        memberRepository.save(memberRequestDto.toEntity(passwordEncoder));

        LoginRequestDto loginRequest = new LoginRequestDto("testId", "Password!1");
        String token = login(loginRequest);

        given()
                .log().all()
                .contentType(ContentType.JSON)
                .header("Authorization", token)
        .when()
                .delete("/api/members/me")
        .then()
                .log().all()
                .statusCode(204);

    }

}