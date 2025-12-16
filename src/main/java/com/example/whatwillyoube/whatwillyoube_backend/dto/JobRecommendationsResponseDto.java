package com.example.whatwillyoube.whatwillyoube_backend.dto;

import com.example.whatwillyoube.whatwillyoube_backend.domain.JobRecommendations;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class    JobRecommendationsResponseDto {

    private Long recommendationId;
    private String jobName;
    private LocalDateTime recommendedAt;

    private String jobSummary;
    private String reason;
    private String relatedMajors;
    private String relatedCertificates;
    private String salary;
    private String prospect;
    private String requiredKnowledge;
    private String careerPath;
    private String environment;
    private String jobValues;

    /**
     * JobRecommendations 엔티티를 받아서 상세 정보 DTO로 변환합니다.
     */
    public static JobRecommendationsResponseDto fromEntity(JobRecommendations entity) {
        return new JobRecommendationsResponseDto(
                entity.getId(),
                entity.getJobName(),
                entity.getCreatedDate(),
                entity.getJobSum(),
                entity.getReason(),
                entity.getMajor(),
                entity.getCertificate(),
                entity.getPay(),
                entity.getJobProspect(),
                entity.getKnowledge(),
                entity.getWay(),
                entity.getJobEnvironment(),
                entity.getJobValues()
        );
    }
}