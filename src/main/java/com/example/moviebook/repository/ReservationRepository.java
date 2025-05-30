package com.example.moviebook.repository;

import com.example.moviebook.entity.ReservationEntity;
import com.example.moviebook.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<ReservationEntity, Long> {

    List<ReservationEntity> findByUserId(Long userId);

    List<ReservationEntity> findByUser(UserEntity user);
}
