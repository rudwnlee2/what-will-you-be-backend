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

    // --- 기본 정보 ---
    private Long recommendationId;    // entity.id
    private String jobName;           // entity.jobName
    private LocalDateTime recommendedAt; // entity.createdDate (BaseTimeEntity)

    // --- 상세 정보 ---
    private String jobSummary;        // entity.jobSum
    private String reason;            // entity.reason
    private String relatedMajors;     // entity.major
    private String relatedCertificates; // entity.certificate
    private String salary;            // entity.pay
    private String prospect;          // entity.jobProspect
    private String requiredKnowledge; // entity.knowledge
    private String careerPath;        // entity.way
    private String environment;       // entity.jobEnvironment
    private String jobValues;         // entity.jobValues

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