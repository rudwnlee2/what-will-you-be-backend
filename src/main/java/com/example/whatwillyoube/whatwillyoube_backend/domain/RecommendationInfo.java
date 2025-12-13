package com.example.whatwillyoube.whatwillyoube_backend.domain;

import com.example.whatwillyoube.whatwillyoube_backend.dto.RecommendationInfoRequestDto;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "recommendation_info")
public class RecommendationInfo extends BaseTimeEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String dream;
    private String dreamReason;
    private String interest;

    @Enumerated(EnumType.STRING)
    private JobValue jobValue;

    @Enumerated(EnumType.STRING)
    private MBTI mbti;

    private String hobby;
    private String favoriteSubject;

    @Enumerated(EnumType.STRING)
    private Holland holland;

    @OneToOne(mappedBy = "recommendationInfo", fetch = FetchType.LAZY)
    private Member member;

    @Builder
    public RecommendationInfo(Member member, String dream, String dreamReason, String interest,
                              JobValue jobValue, MBTI mbti, String hobby,
                              String favoriteSubject, Holland holland) {
        this.member = member;
        this.dream = dream;
        this.dreamReason = dreamReason;
        this.interest = interest;
        this.jobValue = jobValue;
        this.mbti = mbti;
        this.hobby = hobby;
        this.favoriteSubject = favoriteSubject;
        this.holland = holland;
    }

    public void update(RecommendationInfoRequestDto requestDto) {
        this.dream = requestDto.getDream();
        this.dreamReason = requestDto.getDreamReason();
        this.interest = requestDto.getInterest();
        this.jobValue = requestDto.getJobValue();
        this.mbti = requestDto.getMbti();
        this.hobby = requestDto.getHobby();
        this.favoriteSubject = requestDto.getFavoriteSubject();
        this.holland = requestDto.getHolland();
    }

    void setMember(Member member) {
        this.member = member;
    }

}
