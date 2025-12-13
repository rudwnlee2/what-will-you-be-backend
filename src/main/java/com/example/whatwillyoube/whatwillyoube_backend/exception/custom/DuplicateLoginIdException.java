package com.example.whatwillyoube.whatwillyoube_backend.exception.custom;

import com.example.whatwillyoube.whatwillyoube_backend.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class DuplicateLoginIdException extends BusinessException {
    public DuplicateLoginIdException() {
        super("DUPLICATE_LOGIN_ID",
                "이미 사용 중인 아이디입니다.",
                HttpStatus.CONFLICT);
    }
}