package com.example.whatwillyoube.whatwillyoube_backend.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
// 이 어노테이션이 Java의 camelCase 필드를 JSON의 snake_case 키로 변환
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class PythonApiRequestDto {
    private Long memberId;
    private String dream;
    private String interest;
    private String jobValue;
    private String mbti;
    private String hobby;
    private String favoriteSubject;
    private String holland;
}
