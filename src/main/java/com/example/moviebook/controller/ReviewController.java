package com.example.moviebook.controller;

import com.example.moviebook.dto.ReviewDto;
import com.example.moviebook.entity.ReviewEntity;
import com.example.moviebook.util.ReviewTargetType;
import com.example.moviebook.service.ReviewService;
import com.example.moviebook.util.ApiResponse;
import com.example.moviebook.security.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    @Autowired
    private ReviewService reviewService;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    // 리뷰 작성
    @PostMapping
    public ResponseEntity<ApiResponse<ReviewDto>> writeReview(
            @RequestBody ReviewDto reviewDto,
            HttpServletRequest request
    ) {
        try {
            // JWT에서 이메일 추출
            String token = jwtTokenProvider.resolveToken(request);
            String email = jwtTokenProvider.getUserEmailFromToken(token);

            // 이메일을 DTO에 세팅
            reviewDto.setReviewer(email);

            ReviewDto savedReview = reviewService.createReview(reviewDto);
            return ResponseEntity.ok(new ApiResponse<>(true, "리뷰가 성공적으로 작성되었습니다.", savedReview));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    // 특정 영화 or 상영관에 대한 리뷰 조회
    @GetMapping
    public ResponseEntity<ApiResponse<List<ReviewEntity>>> getReviews(
            @RequestParam ReviewTargetType type,
            @RequestParam Long targetId
    ) {
        List<ReviewEntity> reviews = reviewService.getReviewsForTarget(type, targetId);
        return ResponseEntity.ok(new ApiResponse<>(true, "리뷰 조회 성공", reviews));
    }

    // 리뷰 수정
    @PutMapping("/{reviewId}")
    public ResponseEntity<ApiResponse<ReviewDto>> updateReview(
            @PathVariable Long reviewId,
            @RequestBody ReviewDto reviewDto
    ) {
        try {
            ReviewDto updated = reviewService.updateReview(reviewId, reviewDto);
            return ResponseEntity.ok(new ApiResponse<>(true, "리뷰가 성공적으로 수정되었습니다.", updated));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    // 리뷰 삭제
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<ApiResponse<Void>> deleteReview(
            @PathVariable Long reviewId,
            @RequestParam String reviewer
    ) {
        try {
            reviewService.deleteReview(reviewId, reviewer);
            return ResponseEntity.ok(new ApiResponse<>(true, "리뷰가 성공적으로 삭제되었습니다.", null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }
}