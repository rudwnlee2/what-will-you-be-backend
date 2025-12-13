package com.example.whatwillyoube.whatwillyoube_backend.service;

import com.example.whatwillyoube.whatwillyoube_backend.domain.Member;
import com.example.whatwillyoube.whatwillyoube_backend.domain.RecommendationInfo;
import com.example.whatwillyoube.whatwillyoube_backend.dto.RecommendationInfoRequestDto;
import com.example.whatwillyoube.whatwillyoube_backend.dto.RecommendationInfoResponseDto;
import com.example.whatwillyoube.whatwillyoube_backend.exception.custom.MemberNotFoundException;
import com.example.whatwillyoube.whatwillyoube_backend.exception.custom.RecommendationInfoNotFoundException;
import com.example.whatwillyoube.whatwillyoube_backend.repository.MemberRepository;
import com.example.whatwillyoube.whatwillyoube_backend.repository.RecommendationInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RecommendationInfoService {

    private final RecommendationInfoRepository recommendationInfoRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public RecommendationInfoResponseDto createOrUpdateRecommendationInfo(Long memberId, RecommendationInfoRequestDto requestDto) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException(String.valueOf(memberId)));

//        RecommendationInfo info = recommendationInfoRepository.findByMemberIdWithMember(memberId)
//                .orElseThrow(() -> new RecommendationInfoNotFoundException(memberId));
        RecommendationInfo info = member.getRecommendationInfo();

        if (info != null) {
            // 이미 존재하면 업데이트
            info.update(requestDto);
        } else {
            // 존재하지 않으면 새로 생성
            info = RecommendationInfo.builder()
                    .member(member)
                    .dream(requestDto.getDream())
                    .dreamReason(requestDto.getDreamReason())
                    .interest(requestDto.getInterest())
                    .jobValue(requestDto.getJobValue())
                    .mbti(requestDto.getMbti())
                    .hobby(requestDto.getHobby())
                    .favoriteSubject(requestDto.getFavoriteSubject())
                    .holland(requestDto.getHolland())
                    .build();

            member.setRecommendationInfo(info);

            recommendationInfoRepository.save(info);
        }

        return new RecommendationInfoResponseDto(info);
    }

    @Transactional(readOnly = true)
    public RecommendationInfoResponseDto getRecommendationInfo(Long memberId) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException(String.valueOf(memberId)));

        RecommendationInfo info = member.getRecommendationInfo();

        if (info != null) {
            return new RecommendationInfoResponseDto(info);
        } else {
            return new RecommendationInfoResponseDto();
        }
    }
}