package com.example.moviebook.repository;

import com.example.moviebook.entity.SeatEntity;
import com.example.moviebook.entity.TheaterEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SeatRepository extends JpaRepository<SeatEntity, Long> {
    List<SeatEntity> findBySeatNumberInAndTheater_TheaterId(List<String> seatNumbers, Long theaterId);

    List<SeatEntity> findByTheater(TheaterEntity theater);

    List<SeatEntity> findByTheater_TheaterId(Long theaterId);
}
