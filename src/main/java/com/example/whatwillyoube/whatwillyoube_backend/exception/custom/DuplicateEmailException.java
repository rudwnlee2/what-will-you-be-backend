package com.example.whatwillyoube.whatwillyoube_backend.exception.custom;

import com.example.whatwillyoube.whatwillyoube_backend.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class DuplicateEmailException extends BusinessException {
    public DuplicateEmailException() {
        super("DUPLICATE_EMAIL",
                "이미 사용 중인 이메일입니다.",
                HttpStatus.CONFLICT);
    }
}