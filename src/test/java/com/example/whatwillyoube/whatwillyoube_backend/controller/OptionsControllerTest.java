package com.example.whatwillyoube.whatwillyoube_backend.controller;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.anyOf;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class OptionsControllerTest {

    @LocalServerPort
    int port;

    @BeforeEach
    void setup() {
        RestAssured.port = port; // 테스트마다 랜덤 포트를 RestAssured에 설정
    }

    @Test
    @DisplayName("성별 목록 조회 성공")
    void getGenders_success() {
        given()
                .log().all()
        .when()
                .get("/api/options/genders")
        .then()
                .statusCode(200)
                .log().all()
                .body("size()", equalTo(2))
                .body("[0].value", anyOf(equalTo("MALE"), equalTo("FEMALE")))
                .body("[0].label", anyOf(equalTo("남성"), equalTo("여성")));

    }

    @Test
    @DisplayName("직업 추천 선택지 목록 조회 성공")
    void getRecommendationOptions_success() {
        given()
                .log().all()
        .when()
                .get("/api/options/recommendations")
        .then()
                .statusCode(200)
                .log().all()
                .body("hollandTypes.size()", equalTo(6))
                .body("jobValues.size()", equalTo(12))
                .body("mbtiTypes.size()", equalTo(16));
    }

}