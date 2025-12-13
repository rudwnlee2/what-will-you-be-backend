package com.example.whatwillyoube.whatwillyoube_backend.dto;

import com.example.whatwillyoube.whatwillyoube_backend.domain.Holland;
import com.example.whatwillyoube.whatwillyoube_backend.domain.JobValue;
import com.example.whatwillyoube.whatwillyoube_backend.domain.MBTI;
import com.example.whatwillyoube.whatwillyoube_backend.domain.RecommendationInfo;
import lombok.Getter;

@Getter
public class RecommendationInfoResponseDto {

    private final String dream;
    private final String dreamReason;
    private final String interest;
    private final JobValue jobValue;
    private final MBTI mbti;
    private final String hobby;
    private final String favoriteSubject;
    private final Holland holland;

    // Entity -> DTO 변환 생성자
    public RecommendationInfoResponseDto(RecommendationInfo entity) {
        this.dream = entity.getDream();
        this.dreamReason = entity.getDreamReason();
        this.interest = entity.getInterest();
        this.jobValue = entity.getJobValue();
        this.mbti = entity.getMbti();
        this.hobby = entity.getHobby();
        this.favoriteSubject = entity.getFavoriteSubject();
        this.holland = entity.getHolland();
    }

    // 2. 수정된 기본 생성자: 정보가 없을 때 사용
    public RecommendationInfoResponseDto() {
        // String 필드들은 빈 문자열로 초기화
        this.dream = "";
        this.dreamReason = "";
        this.interest = "";
        this.hobby = "";
        this.favoriteSubject = "";

        // ENUM 필드들은 null로 초기화
        this.holland = null;
        this.mbti = null;
        this.jobValue = null;
    }

}
