package com.example.moviebook.dto;

import com.example.moviebook.util.ReviewTargetType;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewDto {
    private String content;
    private int rating;
    private ReviewTargetType targetType;
    private Long targetId;
    private String reviewer;
}