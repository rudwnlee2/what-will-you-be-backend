package com.example.whatwillyoube.whatwillyoube_backend.exception.custom;

import com.example.whatwillyoube.whatwillyoube_backend.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class InvalidPasswordException extends BusinessException {
    public InvalidPasswordException() {
        super("INVALID_PASSWORD",
                "비밀번호가 틀렸습니다.",
                HttpStatus.UNAUTHORIZED);
    }
}