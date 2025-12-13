package com.example.whatwillyoube.whatwillyoube_backend.service;

import com.example.whatwillyoube.whatwillyoube_backend.domain.JobRecommendations;
import com.example.whatwillyoube.whatwillyoube_backend.domain.Member;
import com.example.whatwillyoube.whatwillyoube_backend.domain.RecommendationInfo;
import com.example.whatwillyoube.whatwillyoube_backend.dto.JobRecommendationsResponseDto;
import com.example.whatwillyoube.whatwillyoube_backend.dto.PythonApiRequestDto;
import com.example.whatwillyoube.whatwillyoube_backend.dto.PythonApiResponseDto;
import com.example.whatwillyoube.whatwillyoube_backend.exception.custom.ExternalApiException;
import com.example.whatwillyoube.whatwillyoube_backend.exception.custom.InvalidApiResponseException;
import com.example.whatwillyoube.whatwillyoube_backend.exception.custom.MemberNotFoundException;
import com.example.whatwillyoube.whatwillyoube_backend.exception.custom.RecommendationInfoNotFoundException;
import com.example.whatwillyoube.whatwillyoube_backend.repository.JobRecommendationsRepository;
import com.example.whatwillyoube.whatwillyoube_backend.repository.MemberRepository;
import com.example.whatwillyoube.whatwillyoube_backend.repository.RecommendationInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class RecommendationService {

    private final JobRecommendationsRepository jobRecommendationsRepository;
    private final RecommendationInfoRepository recommendationInfoRepository;
    private final MemberRepository memberRepository;
    private final RestClient restClient;

    @Value("${api.python.url}")
    private String pythonApiBaseUrl;

    /**
     * 프론트 요청 시, DB의 RecommendationInfo를 조회해 Python API로 전송
     * Python에서 받은 추천 결과를 JobRecommendations로 저장
     */
    public List<JobRecommendationsResponseDto> generateJobRecommendations(Long memberId) {

        //회원 존재 여부 확인
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException(String.valueOf(memberId)));

        //member 엔티티에서 직접 접근
        RecommendationInfo recommendationInfo = member.getRecommendationInfo();
        if (recommendationInfo == null) {
            throw new RecommendationInfoNotFoundException(memberId);
        }

        //Python API 요청 DTO 생성
        PythonApiRequestDto pythonRequest = new PythonApiRequestDto(
                memberId,
                recommendationInfo.getDream(),
                recommendationInfo.getInterest(),
                recommendationInfo.getJobValue().name(),
                recommendationInfo.getMbti().name(),
                recommendationInfo.getHobby(),
                recommendationInfo.getFavoriteSubject(),
                recommendationInfo.getHolland().name()
        );

        String pythonApiUrl = pythonApiBaseUrl + "/api/recommend/";

        ResponseEntity<PythonApiResponseDto> responseEntity;
        try {
            responseEntity = restClient.post()
                    .uri(pythonApiUrl)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .body(pythonRequest)
                    .retrieve()
                    .toEntity(PythonApiResponseDto.class);
        } catch (Exception e) {
            throw new ExternalApiException("Python API 호출 실패: " + e.getMessage());
        }

        PythonApiResponseDto apiResponse = responseEntity.getBody();

        //Python 응답 유효성 검증
        if (apiResponse == null || apiResponse.getRecommendedJobs() == null || apiResponse.getRecommendedJobs().isEmpty()) {
            throw new InvalidApiResponseException();
        }

        //Python 응답을 JobRecommendations 엔티티로 변환 + Member와 연결
        apiResponse.getRecommendedJobs().forEach(jobDetail -> {
            JobRecommendations job = JobRecommendations.builder()
                    .jobName(jobDetail.getJobName())
                    .jobSum(jobDetail.getJobSum())
                    .way(jobDetail.getWay())
                    .major(jobDetail.getMajor())
                    .certificate(jobDetail.getCertificate())
                    .pay(jobDetail.getPay())
                    .jobProspect(jobDetail.getJobProspect())
                    .knowledge(jobDetail.getKnowledge())
                    .jobEnvironment(jobDetail.getJobEnvironment())
                    .jobValues(jobDetail.getJobValues())
                    .reason(jobDetail.getReason())
                    .build();

            member.addJobRecommendation(job);
        });

        memberRepository.save(member);

        // 저장된 추천 결과를 DTO로 변환 후 반환
        return member.getJobRecommendations().stream()
                .map(JobRecommendationsResponseDto::fromEntity)
                .collect(Collectors.toList());
    }
}
