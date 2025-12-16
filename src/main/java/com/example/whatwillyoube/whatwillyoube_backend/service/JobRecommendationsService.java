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

        if (!recommendation.getMember().getId().equals(memberId)) {
            throw new RecommendationAccessDeniedException(memberId);
        }

        return JobRecommendationsResponseDto.fromEntity(recommendation);
    }

    @Transactional
    public void deleteJobRecommendation(Long memberId, Long recommendationId) {

        JobRecommendations recommendation = jobRecommendationsRepository.findById(recommendationId)
                .orElseThrow(() -> new RecommendationNotFoundException(recommendationId));

        if (!recommendation.getMember().getId().equals(memberId)) {
            throw new RecommendationAccessDeniedException(memberId);
        }

        jobRecommendationsRepository.delete(recommendation);

    }


}
