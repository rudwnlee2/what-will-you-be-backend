package com.example.whatwillyoube.whatwillyoube_backend.dto;

import com.example.whatwillyoube.whatwillyoube_backend.domain.Gender;
import com.example.whatwillyoube.whatwillyoube_backend.domain.Member;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
public class MemberResponseDto {

    private final Long id;
    private final String loginId;
    private final String name;
    private final String email;
    private final LocalDate birth;
    private final Gender gender;
    private final String phone;
    private final String school;
    private final LocalDateTime createdDate;

    public static MemberResponseDto from(Member member) {
        return new MemberResponseDto(
                member.getId(),
                member.getLoginId(),
                member.getName(),
                member.getEmail(),
                member.getBirth(),
                member.getGender(),
                member.getPhone(),
                member.getSchool(),
                member.getCreatedDate()
        );
    }

    // Builder는 굳이 필요 없고, 정적 팩토리 메서드를 위한 private 생성자면 충분합니다.
    private MemberResponseDto(Long id, String loginId,  String name, String email, LocalDate birth, Gender gender, String phone, String school, LocalDateTime createdDate) {
        this.id = id;
        this.loginId = loginId;
        this.name = name;
        this.email = email;
        this.birth = birth;
        this.gender = gender;
        this.phone = phone;
        this.school = school;
        this.createdDate = createdDate;
    }

}
