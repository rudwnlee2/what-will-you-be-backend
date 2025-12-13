package com.example.whatwillyoube.whatwillyoube_backend.exception.custom;

import com.example.whatwillyoube.whatwillyoube_backend.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class MemberNotFoundException extends BusinessException {
    public MemberNotFoundException(String loginId) {
        super("MEMBER_NOT_FOUND",
                "존재하지 않는 회원입니다. loginId=" + loginId,
                HttpStatus.NOT_FOUND);
    }
}
