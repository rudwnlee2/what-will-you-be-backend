package com.example.whatwillyoube.whatwillyoube_backend.service;

import com.example.whatwillyoube.whatwillyoube_backend.domain.Member;
import com.example.whatwillyoube.whatwillyoube_backend.dto.MemberRequestDto;
import com.example.whatwillyoube.whatwillyoube_backend.dto.MemberResponseDto;
import com.example.whatwillyoube.whatwillyoube_backend.exception.custom.DuplicateEmailException;
import com.example.whatwillyoube.whatwillyoube_backend.exception.custom.DuplicateLoginIdException;
import com.example.whatwillyoube.whatwillyoube_backend.exception.custom.InvalidPasswordException;
import com.example.whatwillyoube.whatwillyoube_backend.exception.custom.MemberNotFoundException;
import com.example.whatwillyoube.whatwillyoube_backend.repository.MemberRepository;
import com.example.whatwillyoube.whatwillyoube_backend.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Transactional
    public MemberResponseDto signUp(MemberRequestDto memberRequestDto) {

        // ID 및 이메일 중복 검사 (필요 시 추가)
        if (memberRepository.findByLoginId(memberRequestDto.getLoginId()).isPresent()) {
            throw new DuplicateLoginIdException();
        }
        if (memberRepository.findByEmail(memberRequestDto.getEmail()).isPresent()) {
            throw new DuplicateEmailException();
        }

        // Request DTO에서 toEntity 메서드를 호출하여 Member 객체 생성
        Member member = memberRequestDto.toEntity(passwordEncoder);

        // Member 저장 후, 저장된 Member를 Response DTO로 변환하여 반환
        Member savedMember = memberRepository.save(member);
        return MemberResponseDto.from(savedMember);
    }

    public String login(String loginId, String password) {
        Member member = memberRepository.findByLoginId(loginId)
                .orElseThrow(() -> new MemberNotFoundException(loginId));

        if (!passwordEncoder.matches(password, member.getPassword())) {
            throw new InvalidPasswordException();
        }

        return jwtUtil.createToken(member.getEmail(), member.getName());
    }

    public MemberResponseDto getMember(Long id) {
        Member  member = memberRepository.findById(id)
                .orElseThrow(() -> new MemberNotFoundException(String.valueOf(id)));

        return MemberResponseDto.from(member);
    }

    @Transactional
    public MemberResponseDto updateMember(Long id, MemberRequestDto memberRequestDto) {

        // 'id'(PK)로 회원을 정확하고 안전하게 찾습니다.
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new MemberNotFoundException(String.valueOf(id)));

        // 엔티티의 update 메서드를 호출하여 정보 변경
        member.update(memberRequestDto, passwordEncoder);

        return MemberResponseDto.from(member);
    }

    @Transactional
    public void deleteMember(Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new MemberNotFoundException(String.valueOf(id)));

        memberRepository.delete(member);
    }

    public boolean isLoginIdExists(String loginId) {
        return memberRepository.findByLoginId(loginId).isPresent();
    }


}
