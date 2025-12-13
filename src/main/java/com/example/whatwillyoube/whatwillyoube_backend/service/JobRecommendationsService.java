package com.example.whatwillyoube.whatwillyoube_backend.service;

import com.example.whatwillyoube.whatwillyoube_backend.domain.JobRecommendations;
import com.example.whatwillyoube.whatwillyoube_backend.domain.Member;
import com.example.whatwillyoube.whatwillyoube_backend.dto.JobRecommendationsListDto;
import com.example.whatwillyoube.whatwillyoube_backend.dto.JobRecommendationsResponseDto;
import com.example.whatwillyoube.whatwillyoube_backend.exception.custom.MemberNotFoundException;
import com.example.whatwillyoube.whatwillyoube_backend.exception.custom.RecommendationAccessDeniedException;
import com.example.whatwillyoube.whatwillyoube_backend.exception.custom.RecommendationNotFoundException;
import com.example.whatwillyoube.whatwillyoube_backend.repository.JobRecommendationsRepository;
import com.example.whatwillyoube.whatwillyoube_backend.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class JobRecommendationsService {

    private final JobRecommendationsRepository jobRecommendationsRepository;
    private final MemberRepository memberRepository;

    public Page<JobRecommendationsListDto> getJobRecommendationsList(Long memberId, int page, int size) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException(String.valueOf(memberId)));

        Pageable pageable = PageRequest.of(page, size); // 페이지 번호와 크기 설정
        Page<JobRecommendations> recommendations = jobRecommendationsRepository.findByMemberOrderByCreatedDateDesc(member, pageable);

        return recommendations.map(JobRecommendationsListDto::fromEntity);
    }

    public JobRecommendationsResponseDto getJobRecommendationDetail(Long memberId, Long recommendationId) {
        JobRecommendations recommendation = jobRecommendationsRepository.findById(recommendationId)
                .orElseThrow(() -> new RecommendationNotFoundException(recommendationId));

        // 2. (중요) 해당 추천 기록이 요청한 회원의 것인지 권한 확인
        if (!recommendation.getMember().getId().equals(memberId)) {
            throw new RecommendationAccessDeniedException(memberId);
        }

        // 3. Entity -> DTO로 변환하여 반환
        return JobRecommendationsResponseDto.fromEntity(recommendation); // DTO 변환 메서드 호출 (예시)
    }

    @Transactional
    public void deleteJobRecommendation(Long memberId, Long recommendationId) {

        // 1. 추천 기록 조회
        JobRecommendations recommendation = jobRecommendationsRepository.findById(recommendationId)
                .orElseThrow(() -> new RecommendationNotFoundException(recommendationId));

        // 2. (중요) 해당 추천 기록이 요청한 회원의 것인지 권한 확인
        if (!recommendation.getMember().getId().equals(memberId)) {
            throw new RecommendationAccessDeniedException(memberId);
        }

        // 3. 레코드 삭제
        jobRecommendationsRepository.delete(recommendation);

    }


}
