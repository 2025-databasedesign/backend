package com.example.moviebook.repository;

import com.example.moviebook.entity.ReservationEntity;
import com.example.moviebook.entity.ReservationSeatEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReservationSeatRepository extends JpaRepository<ReservationSeatEntity, Long> {
    List<ReservationSeatEntity> findByReservation(ReservationEntity reservation);
}
