package com.example.whatwillyoube.whatwillyoube_backend.service;

import com.example.whatwillyoube.whatwillyoube_backend.domain.*;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestClient;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("RecommendationService 단위 테스트")
class RecommendationServiceTest {

    @Mock
    private JobRecommendationsRepository jobRecommendationsRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private RestClient restClient;

    @Mock
    private RestClient.RequestBodyUriSpec requestBodyUriSpec;

    @Mock
    private RestClient.RequestBodySpec requestBodySpec;

    @Mock
    private RestClient.ResponseSpec responseSpec;

    @InjectMocks
    private RecommendationService recommendationService;

    private Member testMember;
    private RecommendationInfo testRecommendationInfo;
    private PythonApiResponseDto mockApiResponse;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(recommendationService, "pythonApiBaseUrl", "http://127.0.0.1:8000");

        testMember = createTestMember();
        testRecommendationInfo = createTestRecommendationInfo(testMember);
        mockApiResponse = createMockApiResponse();
    }

    private Member createTestMember() {
        Member member = Member.builder()
                .loginId("testuser")
                .password("password")
                .name("테스트유저")
                .email("test@example.com")
                .birth(LocalDate.of(2000, 1, 1))
                .gender(Gender.MALE)
                .phone("010-1234-5678")
                .school("테스트고등학교")
                .build();
        ReflectionTestUtils.setField(member, "id", 1L);
        return member;
    }

    private RecommendationInfo createTestRecommendationInfo(Member member) {
        RecommendationInfo info = RecommendationInfo.builder()
                .member(member)
                .dream("소프트웨어 개발자")
                .interest("프로그래밍, 기술")
                .jobValue(JobValue.STABILITY)
                .mbti(MBTI.INTJ)
                .hobby("코딩, 독서")
                .favoriteSubject("수학, 과학")
                .holland(Holland.INVESTIGATIVE)
                .build();

        // Member에 직접 연결 (방법 1 핵심)
        member.setRecommendationInfo(info);
        return info;
    }

    private PythonApiResponseDto createMockApiResponse() {
        PythonApiResponseDto.RecommendedJobDetail jobDetail = new PythonApiResponseDto.RecommendedJobDetail(
                "백엔드 개발자",
                "서버 시스템 개발",
                "컴퓨터공학 전공 후 실무 경험",
                "컴퓨터공학, 소프트웨어공학",
                "정보처리기사",
                "4000-8000만원",
                "매우 좋음",
                "프로그래밍, 데이터베이스",
                "사무실, 재택근무 가능",
                "창조성, 안정성",
                "INTJ 성향과 프로그래밍 관심사가 일치"
        );
        return new PythonApiResponseDto(List.of(jobDetail), 1L);
    }

    private void mockRestClientSuccess() {
        when(restClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(anyString())).thenReturn(requestBodySpec);
        when(requestBodySpec.contentType(any())).thenReturn(requestBodySpec);
        when(requestBodySpec.accept(any())).thenReturn(requestBodySpec);
        when(requestBodySpec.body(any(PythonApiRequestDto.class))).thenReturn(requestBodySpec);
        when(requestBodySpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.toEntity(PythonApiResponseDto.class)).thenReturn(ResponseEntity.ok(mockApiResponse));
    }

    private void mockRestClientFailure(Exception exception) {
        when(restClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(anyString())).thenReturn(requestBodySpec);
        when(requestBodySpec.contentType(any())).thenReturn(requestBodySpec);
        when(requestBodySpec.accept(any())).thenReturn(requestBodySpec);
        when(requestBodySpec.body(any(PythonApiRequestDto.class))).thenReturn(requestBodySpec);
        when(requestBodySpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.toEntity(PythonApiResponseDto.class)).thenThrow(exception);
    }

    // 정상 케이스
    @Test
    @DisplayName("직업 추천 생성 성공 - cascade로 Member 중심 저장")
    void generateJobRecommendations_Success() {
        // given
        Long memberId = 1L;
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(testMember));

        mockRestClientSuccess();

        // when
        List<JobRecommendationsResponseDto> result = recommendationService.generateJobRecommendations(memberId);

        // then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("백엔드 개발자", result.get(0).getJobName());

        verify(memberRepository).findById(memberId);
        verify(memberRepository).save(testMember);
        verifyNoInteractions(jobRecommendationsRepository);
    }

    //회원 없음
    @Test
    @DisplayName("존재하지 않는 회원 예외 발생")
    void generateJobRecommendations_MemberNotFound() {
        when(memberRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(MemberNotFoundException.class,
                () -> recommendationService.generateJobRecommendations(999L));

        verify(memberRepository).findById(999L);
    }

    //추천 정보 없음
    @Test
    @DisplayName("추천 정보가 없으면 RecommendationInfoNotFoundException 발생")
    void generateJobRecommendations_RecommendationInfoNotFound() {
        // given
        Long memberId = 1L;
        testMember.setRecommendationInfo(null); // intentionally null
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(testMember));

        // when & then
        assertThrows(RecommendationInfoNotFoundException.class,
                () -> recommendationService.generateJobRecommendations(memberId));

        verify(memberRepository).findById(memberId);
    }

    // API 호출 실패
    @Test
    @DisplayName("Python API 호출 실패 시 ExternalApiException 발생")
    void generateJobRecommendations_PythonApiFailure() {
        Long memberId = 1L;
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(testMember));
        mockRestClientFailure(new RuntimeException("API 실패"));

        assertThrows(ExternalApiException.class,
                () -> recommendationService.generateJobRecommendations(memberId));
    }

    // API 응답이 null일 때
    @Test
    @DisplayName("Python API 응답이 null이면 InvalidApiResponseException 발생")
    void generateJobRecommendations_NullResponse() {
        Long memberId = 1L;
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(testMember));

        when(restClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(anyString())).thenReturn(requestBodySpec);
        when(requestBodySpec.contentType(any())).thenReturn(requestBodySpec);
        when(requestBodySpec.accept(any())).thenReturn(requestBodySpec);
        when(requestBodySpec.body(any(PythonApiRequestDto.class))).thenReturn(requestBodySpec);
        when(requestBodySpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.toEntity(PythonApiResponseDto.class)).thenReturn(ResponseEntity.ok(null));

        assertThrows(InvalidApiResponseException.class,
                () -> recommendationService.generateJobRecommendations(memberId));
    }

    // 추천 결과가 비어있을 때
    @Test
    @DisplayName("Python API 추천 결과가 비어있으면 InvalidApiResponseException 발생")
    void generateJobRecommendations_EmptyList() {
        Long memberId = 1L;
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(testMember));

        PythonApiResponseDto emptyResponse = new PythonApiResponseDto(List.of(), 1L);
        when(restClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(anyString())).thenReturn(requestBodySpec);
        when(requestBodySpec.contentType(any())).thenReturn(requestBodySpec);
        when(requestBodySpec.accept(any())).thenReturn(requestBodySpec);
        when(requestBodySpec.body(any(PythonApiRequestDto.class))).thenReturn(requestBodySpec);
        when(requestBodySpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.toEntity(PythonApiResponseDto.class)).thenReturn(ResponseEntity.ok(emptyResponse));

        assertThrows(InvalidApiResponseException.class,
                () -> recommendationService.generateJobRecommendations(memberId));
    }
}