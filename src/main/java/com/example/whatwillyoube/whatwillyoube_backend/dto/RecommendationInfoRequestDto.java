package com.example.whatwillyoube.whatwillyoube_backend.dto;

import com.example.whatwillyoube.whatwillyoube_backend.domain.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Builder
public class RecommendationInfoRequestDto {

    private Long memberId;

    @NotBlank(message = "꿈을 입력해주세요.")
    private String dream;
    
    private String dreamReason;
    
    @NotBlank(message = "관심사를 입력해주세요.")
    private String interest;

    @NotNull(message = "직업 가치관을 선택해주세요.")
    private JobValue jobValue;

    @NotNull(message = "MBTI를 선택해주세요.")
    private MBTI mbti;

    @NotBlank(message = "취미를 입력해주세요.")
    private String hobby;
    
    @NotBlank(message = "좋아하는 과목을 입력해주세요.")
    private String favoriteSubject;

    @NotNull(message = "홀랜드 유형을 선택해주세요.")
    private Holland holland;

    // Builder를 위한 생성자
    public RecommendationInfoRequestDto(Long memberId, String dream, String dreamReason, String interest,
                                       JobValue jobValue, MBTI mbti, String hobby, 
                                       String favoriteSubject, Holland holland) {
        this.memberId = memberId;
        this.dream = dream;
        this.dreamReason = dreamReason;
        this.interest = interest;
        this.jobValue = jobValue;
        this.mbti = mbti;
        this.hobby = hobby;
        this.favoriteSubject = favoriteSubject;
        this.holland = holland;
    }

}
