package com.example.whatwillyoube.whatwillyoube_backend.exception.custom;

import com.example.whatwillyoube.whatwillyoube_backend.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class ExternalApiException extends BusinessException {
    public ExternalApiException(String message) {
        super("EXTERNAL_API_ERROR",
                "Python API 호출 실패: " + message,
                HttpStatus.BAD_GATEWAY);
    }
}