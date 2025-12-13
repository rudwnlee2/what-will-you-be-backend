package com.example.whatwillyoube.whatwillyoube_backend.dto;

import com.example.whatwillyoube.whatwillyoube_backend.domain.Gender;
import com.example.whatwillyoube.whatwillyoube_backend.domain.Member;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor // @RequestBody로 들어온 JSON을 객체로 변환하려면 기본 생성자가 필요!
@Builder
public class MemberRequestDto {

    @NotBlank(message = "아이디를 입력해주세요.")
    private String loginId;

    @NotBlank(message = "비밀번호를 입력해주세요.")
    // 비밀번호 정규식 예시: 영문, 숫자, 특수문자  최소 1개씩 포함 8~16자
    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[^a-zA-Z0-9]).{8,16}$", message = "비밀번호는 영문, 숫자, 특수문자를 포함한 8~16자여야 합니다.")
    private String password;

    @NotBlank(message = "이름을 입력해주세요.")
    private String name;

    @NotBlank(message = "이메일을 입력해주세요.")
    @Email(message = "올바른 이메일 형식이 아닙니다.")
    private String email;

    @NotNull(message = "생년월일을 입력해주세요.")
    private LocalDate birth;

    @NotNull(message = "성별을 선택해주세요.")
    private Gender gender;

    @NotBlank(message = "전화번호를 입력해주세요.")
    private String phone;

    @NotBlank(message = "학교 이름을 입력해주세요.")
    private String school;

    // Builder를 위한 생성자
    public MemberRequestDto(String loginId, String password, String name, String email, 
                           LocalDate birth, Gender gender, String phone, String school) {
        this.loginId = loginId;
        this.password = password;
        this.name = name;
        this.email = email;
        this.birth = birth;
        this.gender = gender;
        this.phone = phone;
        this.school = school;
    }

    //DTO를 Member 엔티티로 변환하는 메서드
    public Member toEntity(PasswordEncoder passwordEncoder) {
        return Member.builder()
                .loginId(this.loginId)
                .password(passwordEncoder.encode(this.password)) // 비밀번호는 암호화
                .name(this.name)
                .email(this.email)
                .birth(this.birth)
                .gender(this.gender)
                .phone(this.phone)
                .school(this.school)
                .build();
    }

}
