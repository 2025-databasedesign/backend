package com.example.moviebook.repository;

import com.example.moviebook.entity.ReservationEntity;
import com.example.moviebook.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<ReservationEntity, Long> {

    List<ReservationEntity> findByUser(UserEntity user);

    @Query("""
        SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END
        FROM ReservationEntity r
        WHERE r.user.email = :userEmail AND (
            (:typeStr = 'MOVIE' AND r.schedule.movie.movieId = :targetId) OR
            (:typeStr = 'THEATER' AND r.schedule.theater.theaterId = :targetId)
        )
    """)
    boolean existsByUserEmailAndTarget(
            @Param("userEmail") String userEmail,
            @Param("typeStr") String typeStr,
            @Param("targetId") Long targetId
    );
}
