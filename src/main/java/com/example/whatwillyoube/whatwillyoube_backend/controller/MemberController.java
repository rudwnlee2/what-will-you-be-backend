package com.example.whatwillyoube.whatwillyoube_backend.controller;

import com.example.whatwillyoube.whatwillyoube_backend.dto.LoginRequestDto;
import com.example.whatwillyoube.whatwillyoube_backend.dto.MemberRequestDto;
import com.example.whatwillyoube.whatwillyoube_backend.dto.MemberResponseDto;
import com.example.whatwillyoube.whatwillyoube_backend.security.UserDetailsImpl;
import com.example.whatwillyoube.whatwillyoube_backend.service.MemberService;
import com.example.whatwillyoube.whatwillyoube_backend.util.JwtUtil;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members")
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/signup")
    public ResponseEntity<MemberResponseDto> signUp(@Valid @RequestBody MemberRequestDto memberRequestDto) {
        // @Valid: MemberRequestDto의 유효성 검증을 실행
        // @RequestBody: 요청 본문(JSON)을 MemberRequestDto 객체로 변환

        MemberResponseDto responseDto = memberService.signUp(memberRequestDto);

        // 성공 시, HTTP 상태 코드 201(Created)과 함께 응답 DTO를 반환
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@Valid @RequestBody LoginRequestDto loginRequest, HttpServletResponse response) {

        String token = memberService.login(loginRequest.getLoginId(), loginRequest.getPassword());
        response.addHeader(JwtUtil.AUTHORIZATION_HEADER, token);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/me")
    public ResponseEntity<MemberResponseDto> myPage(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        // @AuthenticationPrincipal 어노테이션이 인증된 사용자 정보를 UserDetailsImpl 형태로 주입해줍니다.
        // 이제 request에서 직접 꺼낼 필요가 없습니다.

        Long memberId = userDetails.getMember().getId(); // UserDetails에서 직접 ID를 가져옵니다.
        MemberResponseDto responseDto = memberService.getMember(memberId);

        return ResponseEntity.ok(responseDto);
    }

    @PatchMapping("/me")
    public ResponseEntity<MemberResponseDto> updateMyPage(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @Valid @RequestBody MemberRequestDto memberRequestDto) {

        Long memberId = userDetails.getMember().getId();
        MemberResponseDto responseDto = memberService.updateMember(memberId, memberRequestDto);

        return ResponseEntity.ok(responseDto);
    }

    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteMember(@AuthenticationPrincipal UserDetailsImpl userDetails) {

        Long memberId = userDetails.getMember().getId();
        memberService.deleteMember(memberId);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/check-loginId/{loginId}")
    public ResponseEntity<Map<String, Boolean>> checkLoginIdDuplicate(@PathVariable String loginId) {
        boolean exists = memberService.isLoginIdExists(loginId);
        return ResponseEntity.ok(Map.of("exists", exists));
    }

}

