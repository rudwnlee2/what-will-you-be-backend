package com.example.whatwillyoube.whatwillyoube_backend.exception.custom;

import com.example.whatwillyoube.whatwillyoube_backend.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class InvalidApiResponseException extends BusinessException {
    public InvalidApiResponseException() {
        super("INVALID_API_RESPONSE",
                "Python API로부터 유효한 추천 응답을 받지 못했습니다.",
                HttpStatus.BAD_GATEWAY);
    }
}