package com.example.whatwillyoube.whatwillyoube_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PythonApiResponseDto {

    private List<RecommendedJobDetail> recommendedJobs;
    private Long memberId;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RecommendedJobDetail {
        private String jobName;
        private String jobSum;
        private String way;
        private String major;
        private String certificate;
        private String pay;
        private String jobProspect;
        private String knowledge;
        private String jobEnvironment;
        private String jobValues;
        private String reason; // 추천 이유
    }

}
