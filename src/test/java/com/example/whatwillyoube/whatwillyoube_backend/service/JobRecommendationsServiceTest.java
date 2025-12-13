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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("JobRecommendationsService 단위 테스트")
public class JobRecommendationsServiceTest {

    @Mock
    private JobRecommendationsRepository jobRecommendationsRepository;

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private JobRecommendationsService jobRecommendationsService;

    private Member testMember;
    private Member otherMember;
    private JobRecommendations testRecommendation;

    @BeforeEach
    void setUp() {
        testMember = Member.builder().build();
        org.springframework.test.util.ReflectionTestUtils.setField(testMember, "id", 1L);

        otherMember = Member.builder().build();
        org.springframework.test.util.ReflectionTestUtils.setField(otherMember, "id", 2L);

        testRecommendation = JobRecommendations.builder()
                .member(testMember)
                .jobName("백엔드 개발자")
                .reason("안정성을 중시하는 성향에 적합")
                .build();
        org.springframework.test.util.ReflectionTestUtils.setField(testRecommendation, "id", 100L);
        org.springframework.test.util.ReflectionTestUtils.setField(testRecommendation, "createdDate", LocalDateTime.now());
    }

    @Test
    @DisplayName("직업 추천 목록 조회 성공")
    void getJobRecommendationsList_Success() {
        // given
        // 1. memberId로 회원을 찾으면 testMember가 반환된다고 설정
        when(memberRepository.findById(1L)).thenReturn(Optional.of(testMember));
        // 2. Repository의 페이징 조회 메소드가 호출되면, testRecommendation을 포함한 Page 객체를 반환하도록 설정
        Pageable pageable = PageRequest.of(0, 5);
        Page<JobRecommendations> recommendationsPage = new PageImpl<>(List.of(testRecommendation), pageable, 1);
        when(jobRecommendationsRepository.findByMemberOrderByCreatedDateDesc(testMember, pageable))
                .thenReturn(recommendationsPage);

        // when
        Page<JobRecommendationsListDto> resultPage = jobRecommendationsService.getJobRecommendationsList(1L, 0, 5);

        // then
        assertNotNull(resultPage);
        assertEquals(1, resultPage.getTotalElements());
        assertEquals(0, resultPage.getNumber());
        JobRecommendationsListDto firstItem = resultPage.getContent().get(0);
        assertEquals(testRecommendation.getId(), firstItem.getRecommendationId());
        assertEquals(testRecommendation.getJobName(), firstItem.getJobName());
    }

    @Test
    @DisplayName("직업 추천 목록 조회 실패 - 존재하지 않는 회원")
    void getJobRecommendationsList_Fail_MemberNotFound() {
        // given
        // memberId로 회원을 찾았지만 결과가 없다고 설정
        when(memberRepository.findById(999L)).thenReturn(Optional.empty());

        // when & then
        MemberNotFoundException exception = assertThrows(MemberNotFoundException.class, () -> {
            jobRecommendationsService.getJobRecommendationsList(999L, 0, 5);
        });
        assertTrue(exception.getMessage().contains("존재하지 않는 회원입니다"));
    }

    @Test
    @DisplayName("직업 추천 상세 조회 성공")
    void getJobRecommendationDetail_Success() {
        // given
        // recommendationId로 추천 정보를 찾으면 testRecommendation이 반환된다고 설정
        when(jobRecommendationsRepository.findById(100L)).thenReturn(Optional.of(testRecommendation));

        // when
        JobRecommendationsResponseDto responseDto = jobRecommendationsService.getJobRecommendationDetail(1L, 100L);

        // then
        assertNotNull(responseDto);
        assertEquals(testRecommendation.getId(), responseDto.getRecommendationId());
        assertEquals(testRecommendation.getJobName(), responseDto.getJobName());
    }

    @Test
    @DisplayName("직업 추천 상세 조회 실패 - 존재하지 않는 추천 정보")
    void getJobRecommendationDetail_Fail_RecommendationNotFound() {
        // given
        // recommendationId로 조회 시 결과가 없다고 설정
        when(jobRecommendationsRepository.findById(999L)).thenReturn(Optional.empty());

        // when & then
        RecommendationNotFoundException exception = assertThrows(RecommendationNotFoundException.class, () -> {
            jobRecommendationsService.getJobRecommendationDetail(1L, 999L);
        });
        assertTrue(exception.getMessage().contains("존재하지 않는 추천 정보입니다"));
    }

    @Test
    @DisplayName("직업 추천 상세 조회 실패 - 조회 권한 없음")
    void getJobRecommendationDetail_Fail_NoAuthority() {
        // given
        // 추천 정보는 존재하지만, 소유자는 memberId=1L (testMember)
        when(jobRecommendationsRepository.findById(100L)).thenReturn(Optional.of(testRecommendation));

        // when & then
        RecommendationAccessDeniedException exception = assertThrows(RecommendationAccessDeniedException.class, () -> {
            jobRecommendationsService.getJobRecommendationDetail(2L, 100L);
        });
        assertTrue(exception.getMessage().contains("추천 정보를 조회/삭제할 권한이 없습니다"));
    }

    @Test
    @DisplayName("직업 추천 삭제 성공")
    void deleteJobRecommendation_Success() {
        // given
        // 1. 삭제할 추천 정보를 성공적으로 찾아온다고 설정
        when(jobRecommendationsRepository.findById(100L)).thenReturn(Optional.of(testRecommendation));
        // 2. delete 메소드는 반환값이 없으므로, 어떤 JobRecommendations 객체가 주어져도 아무 일도 하지 않도록 설정
        doNothing().when(jobRecommendationsRepository).delete(any(JobRecommendations.class));

        // when
        assertDoesNotThrow(() -> jobRecommendationsService.deleteJobRecommendation(1L, 100L));

        // then
        verify(jobRecommendationsRepository, times(1)).delete(testRecommendation);
    }

    @Test
    @DisplayName("직업 추천 삭제 실패 - 삭제 권한 없음")
    void deleteJobRecommendation_Fail_NoAuthority() {
        // given
        // 추천 정보는 존재하지만, 소유자는 memberId=1L
        when(jobRecommendationsRepository.findById(100L)).thenReturn(Optional.of(testRecommendation));

        // when & then
        RecommendationAccessDeniedException exception = assertThrows(RecommendationAccessDeniedException.class, () -> {
            jobRecommendationsService.deleteJobRecommendation(2L, 100L);
        });
        assertTrue(exception.getMessage().contains("추천 정보를 조회/삭제할 권한이 없습니다"));

        verify(jobRecommendationsRepository, never()).delete(any(JobRecommendations.class));
    }

}
