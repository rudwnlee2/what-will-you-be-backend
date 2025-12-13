package com.example.whatwillyoube.whatwillyoube_backend.exception.custom;

import com.example.whatwillyoube.whatwillyoube_backend.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class RecommendationAccessDeniedException extends BusinessException {
    public RecommendationAccessDeniedException(Long memberId) {
        super("ACCESS_DENIED",
                "해당 추천 정보를 조회/삭제할 권한이 없습니다. memberId=" + memberId,
                HttpStatus.FORBIDDEN);
    }
}