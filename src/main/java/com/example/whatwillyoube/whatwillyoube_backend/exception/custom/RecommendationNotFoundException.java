package com.example.whatwillyoube.whatwillyoube_backend.exception.custom;

import com.example.whatwillyoube.whatwillyoube_backend.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class RecommendationNotFoundException extends BusinessException {
    public RecommendationNotFoundException(Long recommendationId) {
        super("RECOMMENDATION_NOT_FOUND",
                "존재하지 않는 추천 정보입니다. id=" + recommendationId,
                HttpStatus.NOT_FOUND);
    }
}