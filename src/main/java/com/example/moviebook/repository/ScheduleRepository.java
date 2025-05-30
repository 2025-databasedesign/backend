package com.example.moviebook.repository;

import com.example.moviebook.entity.ScheduleEntity;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ScheduleRepository extends JpaRepository<ScheduleEntity, Long> {
    @Query("SELECT s FROM ScheduleEntity s " +
            "JOIN FETCH s.movie " +
            "JOIN FETCH s.theater " +
            "WHERE s.startTime BETWEEN :start AND :end")
    List<ScheduleEntity> findByStartTimeBetween(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);
}
