package com.example.whatwillyoube.whatwillyoube_backend.domain;

import com.example.whatwillyoube.whatwillyoube_backend.dto.MemberRequestDto;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "members")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Member extends BaseTimeEntity{ // BaseTimeEntity 상속

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String loginId;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private LocalDate birth;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(nullable = false)
    private String phone;
    
    @Column(nullable = false)
    private String school;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "recommendation_info_id", unique = true)
    private RecommendationInfo recommendationInfo;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<JobRecommendations> jobRecommendations = new ArrayList<>();

    @Builder
    public Member(String loginId, String password, String name, String email, LocalDate birth, Gender gender, String phone, String school) {
        this.loginId = loginId;
        this.password = password;
        this.name = name;
        this.email = email;
        this.birth = birth;
        this.gender = gender;
        this.phone = phone;
        this.school = school;

    }

    // ===== 연관관계 편의 메서드 =====

    public void setRecommendationInfo(RecommendationInfo recommendationInfo) {
        this.recommendationInfo = recommendationInfo;
        if (recommendationInfo != null) {
            recommendationInfo.setMember(this);
        }
    }

    public void addJobRecommendation(JobRecommendations job) {
        jobRecommendations.add(job);
        job.setMember(this);
    }

    public void removeJobRecommendation(JobRecommendations job) {
        jobRecommendations.remove(job);
        job.setMember(null); // orphanRemoval=true -> 컬렉션에서 제거되면 DB에서 삭제
    }

    public void update(MemberRequestDto requestDto, PasswordEncoder passwordEncoder) {
        // 비밀번호는 값이 존재할 때만 변경
        if (requestDto.getPassword() != null && !requestDto.getPassword().isBlank()) {
            this.password = passwordEncoder.encode(requestDto.getPassword());
        }
        // DTO의 각 필드가 null이 아닐 경우에만 기존 엔티티의 값을 변경
        if (requestDto.getName() != null) {
            this.name = requestDto.getName();
        }
        if (requestDto.getEmail() != null) {
            this.email = requestDto.getEmail();
        }
        if (requestDto.getBirth() != null) {
            this.birth = requestDto.getBirth();
        }
        if (requestDto.getGender() != null) {
            this.gender = requestDto.getGender();
        }
        if (requestDto.getPhone() != null) {
            this.phone = requestDto.getPhone();
        }
        if (requestDto.getSchool() != null) {
            this.school = requestDto.getSchool();
        }
    }

}
