package com.example.whatwillyoube.whatwillyoube_backend.service;

import com.example.whatwillyoube.whatwillyoube_backend.domain.Gender;
import com.example.whatwillyoube.whatwillyoube_backend.domain.Member;
import com.example.whatwillyoube.whatwillyoube_backend.dto.MemberRequestDto;
import com.example.whatwillyoube.whatwillyoube_backend.dto.MemberResponseDto;
import com.example.whatwillyoube.whatwillyoube_backend.exception.custom.DuplicateEmailException;
import com.example.whatwillyoube.whatwillyoube_backend.exception.custom.DuplicateLoginIdException;
import com.example.whatwillyoube.whatwillyoube_backend.exception.custom.MemberNotFoundException;
import com.example.whatwillyoube.whatwillyoube_backend.repository.MemberRepository;
import com.example.whatwillyoube.whatwillyoube_backend.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("MemberService 단위 테스트")
public class MemberServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private MemberService memberService;

    private Member testMember;
    private MemberRequestDto memberRequestDto;

    @BeforeEach
    void setUp() {
        memberRequestDto = new MemberRequestDto(
                "testuser",
                "password123",
                "테스트유저",
                "test@example.com",
                LocalDate.of(2000, 1, 1),
                Gender.MALE,
                "010-1234-5678",
                "테스트고등학교"
        );

        testMember = Member.builder()
                .loginId("testuser")
                .password("encodedPassword") // 비밀번호는 암호화된 상태라고 가정
                .name("테스트유저")
                .email("test@example.com")
                .birth(LocalDate.of(2000, 1, 1))
                .gender(Gender.MALE)
                .phone("010-1234-5678")
                .school("테스트고등학교")
                .build();

        // ReflectionTestUtils 등을 사용해 private 필드인 id와 createdDate를 설정
        // 실제 엔티티는 DB가 ID를 자동 생성해주지만, 테스트에서는 수동으로 지정
        // (실무에서는 테스트용 setter를 엔티티에 추가하는 방법도 고려)
        org.springframework.test.util.ReflectionTestUtils.setField(testMember, "id", 1L);
        org.springframework.test.util.ReflectionTestUtils.setField(testMember, "createdDate", LocalDateTime.now());
    }

    // --- 회원가입(signUp) 테스트 ---
    @Test
    @DisplayName("회원가입 성공")
    void signUp_Success() {
        // given
        // 1. ID와 이메일 중복 검사 시, 결과가 비어있다고(중복이 아니라고) 설정
        when(memberRepository.findByLoginId(anyString())).thenReturn(Optional.empty());
        when(memberRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        // 2. DTO를 엔티티로 변환할 때, passwordEncoder가 특정 값을 반환하도록 설정
        when(passwordEncoder.encode(memberRequestDto.getPassword())).thenReturn("encodedPassword");
        // 3. repository.save가 호출되면, 미리 만들어둔 testMember를 반환하도록 설정
        when(memberRepository.save(any(Member.class))).thenReturn(testMember);

        // when
        MemberResponseDto responseDto = memberService.signUp(memberRequestDto);

        // then
        assertNotNull(responseDto);
        assertEquals(testMember.getId(), responseDto.getId());
        assertEquals(testMember.getLoginId(), responseDto.getLoginId());
        assertEquals(testMember.getName(), responseDto.getName());

        verify(memberRepository, times(1)).save(any(Member.class));
    }

    @Test
    @DisplayName("회원가입 실패 - 로그인 ID 중복")
    void signUp_Fail_DuplicateLoginId() {
        // given
        // loginId로 조회 시, 이미 member가 존재한다고 설정
        when(memberRepository.findByLoginId(memberRequestDto.getLoginId())).thenReturn(Optional.of(testMember));

        // when & then
        DuplicateLoginIdException exception = assertThrows(DuplicateLoginIdException.class, () -> {
            memberService.signUp(memberRequestDto);
        });

        assertEquals("이미 사용 중인 아이디입니다.", exception.getMessage());

        verify(memberRepository, never()).save(any(Member.class));
    }

    @Test
    @DisplayName("회원가입 실패 - 이메일 중복")
    void signUp_Fail_DuplicateEmail() {
        // given
        // loginId는 중복이 아니지만, email이 중복되는 상황을 설정
        when(memberRepository.findByLoginId(anyString())).thenReturn(Optional.empty());
        when(memberRepository.findByEmail(memberRequestDto.getEmail())).thenReturn(Optional.of(testMember));

        // when & then
        DuplicateEmailException exception = assertThrows(DuplicateEmailException.class, () -> memberService.signUp(memberRequestDto));
        assertEquals("이미 사용 중인 이메일입니다.", exception.getMessage());
        verify(memberRepository, never()).save(any(Member.class));
    }

    @Test
    @DisplayName("로그인 성공")
    void login_Success() {
        // given
        when(memberRepository.findByLoginId("testuser")).thenReturn(Optional.of(testMember));
        when(passwordEncoder.matches("password123", "encodedPassword")).thenReturn(true);
        when(jwtUtil.createToken(testMember.getEmail(), testMember.getName()))
                .thenReturn("test-jwt-token");

        // when
        String token = memberService.login("testuser", "password123");

        // then
        assertEquals("test-jwt-token", token);
    }

    @Test
    @DisplayName("회원 정보 수정 성공")
    void updateMember_Success() {
        // given
        MemberRequestDto updateRequest = new MemberRequestDto(
                null, // loginId는 변경 불가
                "newPassword456",
                "이름변경",
                "new.email@example.com",
                LocalDate.of(2001, 2, 2),
                Gender.FEMALE,
                "010-8765-4321",
                "새로운고등학교"
        );
        // repository가 ID로 기존 member를 잘 찾아온다고 설정
        when(memberRepository.findById(1L)).thenReturn(Optional.of(testMember));

        // when
        MemberResponseDto responseDto = memberService.updateMember(1L, updateRequest);

        // then
        assertNotNull(responseDto);
        assertEquals("이름변경", responseDto.getName());
        assertEquals("new.email@example.com", responseDto.getEmail());
        assertEquals(Gender.FEMALE, responseDto.getGender());

        verify(passwordEncoder, times(1)).encode("newPassword456");
    }

    @Test
    @DisplayName("회원 정보 삭제 성공")
    void deleteMember_Success() {
        // given
        when(memberRepository.findById(1L)).thenReturn(Optional.of(testMember));
        // delete 메소드는 반환값이 없으므로, 어떤 Member 객체가 주어져도 아무 일도 하지 않도록 설정
        doNothing().when(memberRepository).delete(any(Member.class));

        // when
        assertDoesNotThrow(() -> memberService.deleteMember(1L));

        // then
        ArgumentCaptor<Member> memberCaptor = ArgumentCaptor.forClass(Member.class);
        verify(memberRepository, times(1)).delete(memberCaptor.capture());

        assertEquals(1L, memberCaptor.getValue().getId());
    }

    @Test
    @DisplayName("회원 정보 삭제 실패 - 존재하지 않는 회원")
    void deleteMember_Fail_MemberNotFound() {
        // given
        when(memberRepository.findById(999L)).thenReturn(Optional.empty());

        // when & then
        MemberNotFoundException exception = assertThrows(MemberNotFoundException.class, () -> {
            memberService.deleteMember(999L);
        });
        assertTrue(exception.getMessage().contains("존재하지 않는 회원입니다"));

        verify(memberRepository, never()).delete(any(Member.class));
    }
}
