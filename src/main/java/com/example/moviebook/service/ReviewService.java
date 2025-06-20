package com.example.moviebook.service;

import com.example.moviebook.dto.ReviewDto;
import com.example.moviebook.entity.ReviewEntity;
import com.example.moviebook.repository.ReviewRepository;
import com.example.moviebook.repository.ReservationRepository;
import com.example.moviebook.util.ReviewTargetType;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;
    @Autowired
    private ReservationRepository reservationRepository;

    public ReviewDto createReview(ReviewDto dto) {
        boolean hasReservation = reservationRepository.existsByUserEmailAndTarget(
                dto.getReviewer(),
                dto.getTargetType().name(), // enum → 문자열
                dto.getTargetId()
        );

        if (!hasReservation) {
            throw new IllegalArgumentException("해당 대상에 대해 예매한 사용자만 리뷰를 작성할 수 있습니다.");
        }

        ReviewEntity review = ReviewEntity.builder()
                .content(dto.getContent())
                .rating(dto.getRating())
                .targetType(dto.getTargetType())
                .targetId(dto.getTargetId())
                .reviewer(dto.getReviewer())
                .build();

        ReviewEntity saved = reviewRepository.save(review);

        return ReviewDto.builder()
                .content(saved.getContent())
                .rating(saved.getRating())
                .targetType(saved.getTargetType())
                .targetId(saved.getTargetId())
                .reviewer(saved.getReviewer())
                .build();
    }

    public ReviewDto updateReview(Long reviewId, ReviewDto dto) {
        ReviewEntity review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("해당 리뷰를 찾을 수 없습니다."));

        if (!review.getReviewer().equals(dto.getReviewer())) {
            throw new IllegalArgumentException("리뷰 작성자만 수정할 수 있습니다.");
        }

        review.setContent(dto.getContent());
        review.setRating(dto.getRating());
        reviewRepository.save(review);

        return ReviewDto.builder()
                .content(review.getContent())
                .rating(review.getRating())
                .targetType(review.getTargetType())
                .targetId(review.getTargetId())
                .reviewer(review.getReviewer())
                .build();
    }

    public void deleteReview(Long reviewId, String reviewer) {
        ReviewEntity review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("해당 리뷰를 찾을 수 없습니다."));

        if (!review.getReviewer().equals(reviewer)) {
            throw new IllegalArgumentException("리뷰 작성자만 삭제할 수 있습니다.");
        }

        reviewRepository.delete(review);
    }

    public List<ReviewEntity> getReviewsForTarget(ReviewTargetType type, Long targetId) {
        return reviewRepository.findByTargetTypeAndTargetId(type, targetId);
    }
}