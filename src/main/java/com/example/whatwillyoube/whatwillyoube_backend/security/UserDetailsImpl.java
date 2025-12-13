package com.example.whatwillyoube.whatwillyoube_backend.security;

import com.example.whatwillyoube.whatwillyoube_backend.domain.Member;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@Getter
public class UserDetailsImpl implements UserDetails {
    private final Member member;

    public UserDetailsImpl(Member member) {
        this.member = member;
    }

    // 사용자에게 부여된 권한을 반환. 지금은 권한이 없으므로 기본 역할을 부여
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // 예: "ROLE_USER". 나중에 권한 체계가 생기면 member.getRole() 등을 사용
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
    }

    // 사용자의 비밀번호를 반환
    @Override
    public String getPassword() {
        return member.getPassword();
    }

    // 사용자의 이름(여기서는 이메일)을 반환
    @Override
    public String getUsername() {
        return member.getEmail();
    }

    // 계정이 만료되지 않았는지 여부를 반환
    @Override
    public boolean isAccountNonExpired() {
        return true; // true: 만료되지 않음
    }

    // 계정이 잠겨있지 않은지 여부를 반환
    @Override
    public boolean isAccountNonLocked() {
        return true; // true: 잠기지 않음
    }

    // 자격 증명(비밀번호)이 만료되지 않았는지 여부를 반환
    @Override
    public boolean isCredentialsNonExpired() {
        return true; // true: 만료되지 않음
    }

    // 계정이 활성화되어 있는지 여부를 반환
    @Override
    public boolean isEnabled() {
        return true; // true: 활성화됨
    }
}
