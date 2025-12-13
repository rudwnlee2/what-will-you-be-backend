package com.example.whatwillyoube.whatwillyoube_backend.controller;

import com.example.whatwillyoube.whatwillyoube_backend.dto.RecommendationInfoRequestDto;
import com.example.whatwillyoube.whatwillyoube_backend.dto.RecommendationInfoResponseDto;
import com.example.whatwillyoube.whatwillyoube_backend.security.UserDetailsImpl;
import com.example.whatwillyoube.whatwillyoube_backend.service.RecommendationInfoService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/recommendation-info")
public class RecommendationInfoController {

    private final RecommendationInfoService recommendationInfoService;

    @GetMapping
    public ResponseEntity<RecommendationInfoResponseDto> getMyRecommendationInfo(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        Long memberId = userDetails.getMember().getId();
        RecommendationInfoResponseDto responseDto = recommendationInfoService.getRecommendationInfo(memberId);

        return ResponseEntity.ok(responseDto);
    }

    @PutMapping
    public ResponseEntity<RecommendationInfoResponseDto> createOrUpdateMyRecommendationInfo(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @Valid @RequestBody RecommendationInfoRequestDto requestDto) {

        Long memberId = userDetails.getMember().getId();

        RecommendationInfoResponseDto responseDto = recommendationInfoService.createOrUpdateRecommendationInfo(memberId, requestDto);

        return ResponseEntity.ok(responseDto);
    }

}
