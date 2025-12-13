package com.example.whatwillyoube.whatwillyoube_backend.controller;

import com.example.whatwillyoube.whatwillyoube_backend.config.SecurityConfig;
import com.example.whatwillyoube.whatwillyoube_backend.domain.Gender;
import com.example.whatwillyoube.whatwillyoube_backend.domain.Member;
import com.example.whatwillyoube.whatwillyoube_backend.dto.LoginRequestDto;
import com.example.whatwillyoube.whatwillyoube_backend.dto.MemberRequestDto;
import com.example.whatwillyoube.whatwillyoube_backend.dto.MemberResponseDto;
import com.example.whatwillyoube.whatwillyoube_backend.exception.custom.DuplicateLoginIdException;
import com.example.whatwillyoube.whatwillyoube_backend.security.UserDetailsImpl;
import com.example.whatwillyoube.whatwillyoube_backend.service.MemberService;
import com.example.whatwillyoube.whatwillyoube_backend.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

@WebMvcTest(controllers = MemberController.class)
@Import({SecurityConfig.class, com.example.whatwillyoube.whatwillyoube_backend.exception.GlobalExceptionHandler.class})
@TestPropertySource(properties = {
        "jwt.secret.key=dGVzdC1zZWNyZXQta2V5LXRlc3Qtc2VjcmV0LWtleS10ZXN0LXNlY3JldC1rZXkK",
        "jwt.token.expiration=60000"
})
class MemberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MemberService memberService;

    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @Autowired
    private ObjectMapper objectMapper;

    private Member testMember;

    @BeforeEach
    void setup() {
        testMember = Member.builder()
                .loginId("testId")
                .password("encodedPw")
                .name("테스터")
                .email("test@test.com")
                .birth(LocalDate.of(2000, 1, 1))
                .gender(Gender.MALE)
                .phone("010-1234-5678")
                .school("테스트학교")
                .build();

        ReflectionTestUtils.setField(testMember, "id", 1L);
        ReflectionTestUtils.setField(testMember, "createdDate", LocalDateTime.now());
    }

    @Test
    @DisplayName("회원가입 성공 시 201 반환")
    void signUp_success() throws Exception {
        MemberRequestDto requestDto = new MemberRequestDto(
                "testId", "Password!1", "테스터", "test@test.com",
                LocalDate.of(2000, 1, 1), Gender.MALE,
                "010-1234-5678", "테스트학교"
        );
        MemberResponseDto responseDto = MemberResponseDto.from(testMember);
        given(memberService.signUp(any(MemberRequestDto.class))).willReturn(responseDto);

        mockMvc.perform(post("/api/members/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto))
                        .with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.loginId").value("testId"))
                .andExpect(jsonPath("$.email").value("test@test.com"));
    }

    @Test
    @DisplayName("로그인 성공 시 200 반환 + Authorization 헤더 추가")
    void login_success() throws Exception {
        LoginRequestDto requestDto = new LoginRequestDto("testId", "Password!1");
        String token = "Bearer test.jwt.token";
        given(memberService.login(anyString(), anyString())).willReturn(token);

        mockMvc.perform(post("/api/members/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(header().string(JwtUtil.AUTHORIZATION_HEADER, token));
    }

    @Test
    @DisplayName("내 정보 조회 성공 시 200 반환")
    void myPage_success() throws Exception {
        MemberResponseDto responseDto = MemberResponseDto.from(testMember);
        given(memberService.getMember(1L)).willReturn(responseDto);
        UserDetailsImpl principal = new UserDetailsImpl(testMember);

        mockMvc.perform(get("/api/members/me")
                        .with(SecurityMockMvcRequestPostProcessors.user(principal)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.loginId").value("testId"));
    }

    @Test
    @DisplayName("내 정보 수정 성공 시 200 반환")
    void updateMyPage_success() throws Exception {
        MemberRequestDto updateRequest = new MemberRequestDto(
                "newId", "Password!1", "새이름", "new@test.com",
                LocalDate.of(2001, 2, 2), Gender.FEMALE,
                "010-1111-2222", "새학교"
        );

        Member updatedMember = Member.builder()
                .loginId("newId").password("encodedPw").name("새이름")
                .email("new@test.com").birth(LocalDate.of(2001, 2, 2))
                .gender(Gender.FEMALE).phone("010-1111-2222").school("새학교")
                .build();
        ReflectionTestUtils.setField(updatedMember, "id", 1L);

        MemberResponseDto responseDto = MemberResponseDto.from(updatedMember);
        given(memberService.updateMember(eq(1L), any(MemberRequestDto.class))).willReturn(responseDto);

        UserDetailsImpl principal = new UserDetailsImpl(testMember);

        mockMvc.perform(patch("/api/members/me")
                        .with(SecurityMockMvcRequestPostProcessors.user(principal))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.loginId").value("newId"))
                .andExpect(jsonPath("$.email").value("new@test.com"));
    }

    @Test
    @DisplayName("회원 탈퇴 성공 시 204 반환")
    void deleteMember_success() throws Exception {
        UserDetailsImpl principal = new UserDetailsImpl(testMember);

        mockMvc.perform(delete("/api/members/me")
                        .with(SecurityMockMvcRequestPostProcessors.user(principal)))
                .andExpect(status().isNoContent());

        verify(memberService).deleteMember(1L);
    }

    // ======================== 실패 케이스 ========================

    @Test
    @DisplayName("회원가입 실패 - 유효성 검사 실패 시 400 반환")
    void signUp_fail_validation() throws Exception {
        MemberRequestDto requestDto = new MemberRequestDto(
                "testId", "", "테스터", "test@test.com", // 비밀번호 공백
                LocalDate.of(2000, 1, 1), Gender.MALE,
                "010-1234-5678", "테스트학교"
        );

        mockMvc.perform(post("/api/members/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto))
                        .with(csrf()))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("회원가입 실패 - 아이디 중복 시 409 반환")
    void signUp_fail_duplicateLoginId() throws Exception {
        MemberRequestDto requestDto = new MemberRequestDto(
                "testId", "Password!1", "테스터", "test@test.com",
                LocalDate.of(2000, 1, 1), Gender.MALE,
                "010-1234-5678", "테스트학교"
        );

        given(memberService.signUp(any(MemberRequestDto.class)))
                .willThrow(new DuplicateLoginIdException());

        mockMvc.perform(post("/api/members/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto))
                        .with(csrf()))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("로그인 실패 - 존재하지 않는 회원 시 404 반환")
    void login_fail_memberNotFound() throws Exception {
        LoginRequestDto requestDto = new LoginRequestDto("nonExistentId", "Password!1");
        given(memberService.login(anyString(), anyString()))
                .willThrow(new com.example.whatwillyoube.whatwillyoube_backend.exception.custom.MemberNotFoundException("nonExistentId"));

        mockMvc.perform(post("/api/members/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("로그인 실패 - 비밀번호 불일치 시 401 반환")
    void login_fail_wrongPassword() throws Exception {
        LoginRequestDto requestDto = new LoginRequestDto("testId", "wrongPassword");
        given(memberService.login(anyString(), anyString()))
                .willThrow(new com.example.whatwillyoube.whatwillyoube_backend.exception.custom.InvalidPasswordException());

        mockMvc.perform(post("/api/members/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("내 정보 조회 실패 - 존재하지 않는 회원 시 404 반환")
    void myPage_fail_memberNotFound() throws Exception {
        given(memberService.getMember(1L))
                .willThrow(new com.example.whatwillyoube.whatwillyoube_backend.exception.custom.MemberNotFoundException("1"));
        UserDetailsImpl principal = new UserDetailsImpl(testMember);

        mockMvc.perform(get("/api/members/me")
                        .with(SecurityMockMvcRequestPostProcessors.user(principal)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("내 정보 수정 실패 - 유효성 검사 실패 시 400 반환")
    void updateMyPage_fail_validation() throws Exception {
        MemberRequestDto updateRequest = new MemberRequestDto(
                "newId", "Password!1", "", "new@test.com", // 이름이 빈 문자열
                LocalDate.of(2001, 2, 2), Gender.FEMALE,
                "010-1111-2222", "새학교"
        );
        UserDetailsImpl principal = new UserDetailsImpl(testMember);

        mockMvc.perform(patch("/api/members/me")
                        .with(SecurityMockMvcRequestPostProcessors.user(principal))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("회원 탈퇴 실패 - 존재하지 않는 회원 시 404 반환")
    void deleteMember_fail_memberNotFound() throws Exception {
        doThrow(new com.example.whatwillyoube.whatwillyoube_backend.exception.custom.MemberNotFoundException("1"))
                .when(memberService).deleteMember(1L);
        UserDetailsImpl principal = new UserDetailsImpl(testMember);

        mockMvc.perform(delete("/api/members/me")
                        .with(SecurityMockMvcRequestPostProcessors.user(principal)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("인증되지 않은 사용자의 보호된 리소스 접근 시 403 반환")
    void accessProtectedResource_fail_unauthorized() throws Exception {
        mockMvc.perform(get("/api/members/me"))
                .andExpect(status().isForbidden());
    }
}