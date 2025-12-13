package com.example.whatwillyoube.whatwillyoube_backend.dto;

import java.util.List;

public class RecommendationOptionsDto {

    private final List<HollandDto> hollandTypes;
    private final List<JobValueDto> jobValues;
    private final List<MbtiDto> mbtiTypes;

    public RecommendationOptionsDto(List<HollandDto> hollandTypes, List<JobValueDto> jobValues, List<MbtiDto> mbtiTypes) {
        this.hollandTypes = hollandTypes;
        this.jobValues = jobValues;
        this.mbtiTypes = mbtiTypes;
    }

    public List<HollandDto> getHollandTypes() {
        return hollandTypes;
    }

    public List<JobValueDto> getJobValues() {
        return jobValues;
    }

    public List<MbtiDto> getMbtiTypes() {
        return mbtiTypes;
    }
}
