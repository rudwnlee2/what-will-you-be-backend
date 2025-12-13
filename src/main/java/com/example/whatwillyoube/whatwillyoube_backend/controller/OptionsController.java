package com.example.whatwillyoube.whatwillyoube_backend.controller;

import com.example.whatwillyoube.whatwillyoube_backend.domain.Gender;
import com.example.whatwillyoube.whatwillyoube_backend.domain.Holland;
import com.example.whatwillyoube.whatwillyoube_backend.domain.JobValue;
import com.example.whatwillyoube.whatwillyoube_backend.domain.MBTI;
import com.example.whatwillyoube.whatwillyoube_backend.dto.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

//나중에 캐싱 고려
@RestController
@RequestMapping("/api/options")
public class OptionsController {

    /**
     * 회원가입에 필요한 성별 목록 조회
     */
    @GetMapping("/genders")
    public ResponseEntity<List<GenderDto>> getGenders() {
        List<GenderDto> genders = Arrays.stream(Gender.values())
                .map(g -> new GenderDto(g.name(), g == Gender.MALE ? "남성" : "여성"))
                .collect(Collectors.toList());
        return ResponseEntity.ok(genders);
    }

    /**
     * 직업 추천 정보 입력 화면에 필요한 모든 선택지 목록 조회
     */
    @GetMapping("/recommendations")
    public ResponseEntity<RecommendationOptionsDto> getRecommendationOptions() {
        // Holland 유형 목록 생성
        List<HollandDto> hollandTypes = Arrays.stream(Holland.values())
                .map(h -> new HollandDto(h.name(), h.getDisplayName(), h.getDescription()))
                .collect(Collectors.toList());

        // 직업 가치관 목록 생성
        List<JobValueDto> jobValues = Arrays.stream(JobValue.values())
                .map(j -> new JobValueDto(j.name(), j.getDisplayName(), j.getDescription()))
                .collect(Collectors.toList());

        // MBTI 유형 목록 생성
        List<MbtiDto> mbtiTypes = Arrays.stream(MBTI.values())
                .map(m -> new MbtiDto(m.name(), m.getDescription()))
                .collect(Collectors.toList());

        RecommendationOptionsDto recommendationOptions = new RecommendationOptionsDto(hollandTypes, jobValues, mbtiTypes);

        return ResponseEntity.ok(recommendationOptions);
    }


}
