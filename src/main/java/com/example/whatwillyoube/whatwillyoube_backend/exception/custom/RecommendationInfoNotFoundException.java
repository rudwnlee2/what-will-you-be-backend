package com.example.whatwillyoube.whatwillyoube_backend.exception.custom;

import com.example.whatwillyoube.whatwillyoube_backend.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class RecommendationInfoNotFoundException extends BusinessException {
    public RecommendationInfoNotFoundException(Long memberId) {
        super("RECOMMENDATION_INFO_NOT_FOUND",
                "추천 정보를 찾을 수 없습니다. memberId=" + memberId,
                HttpStatus.NOT_FOUND);
    }
}