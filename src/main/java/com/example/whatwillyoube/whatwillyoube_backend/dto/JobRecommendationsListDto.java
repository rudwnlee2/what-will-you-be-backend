package com.example.whatwillyoube.whatwillyoube_backend.dto;

import com.example.whatwillyoube.whatwillyoube_backend.domain.JobRecommendations;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
public class JobRecommendationsListDto {

    private Long recommendationId;
    private String jobName;
    private String reason;
    private LocalDateTime createdDate;

    // JobRecommendations 엔티티를 받아서 JobRecommendationsListDto로 변환하는 정적(static) 메서드
    public static JobRecommendationsListDto fromEntity(JobRecommendations entity) {
        return new JobRecommendationsListDto(
                entity.getId(),
                entity.getJobName(),
                entity.getReason(),
                entity.getCreatedDate()
        );
    }
}
