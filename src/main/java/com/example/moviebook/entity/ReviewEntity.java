package com.example.moviebook.entity;

import com.example.moviebook.util.ReviewTargetType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String content;

    private int rating;

    @Enumerated(EnumType.STRING)
    private ReviewTargetType targetType;

    private Long targetId; // 영화 ID 또는 상영관 ID

    private String reviewer; // 리뷰 작성자 (추후 유저 엔티티와 연동 가능)

    private LocalDateTime createdAt;

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}

