package com.example.moviebook.repository;

import com.example.moviebook.entity.ReviewEntity;
import com.example.moviebook.util.ReviewTargetType;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ReviewRepository extends JpaRepository<ReviewEntity, Long> {
    List<ReviewEntity> findByTargetTypeAndTargetId(ReviewTargetType type, Long targetId);
}