package com.example.moviebook.repository;

import com.example.moviebook.entity.ReservationEntity;
import com.example.moviebook.entity.ReservationSeatEntity;
import com.example.moviebook.entity.SeatEntity;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.example.moviebook.entity.ScheduleEntity;
import java.util.List;

@Repository
public interface ReservationSeatRepository extends JpaRepository<ReservationSeatEntity, Long> {
    List<ReservationSeatEntity> findByReservation(ReservationEntity reservation);

    boolean existsByScheduleAndSeat(ScheduleEntity schedule, SeatEntity seat);

    //스케줄 단위 예약 좌석 수 계산 (단건)
    int countBySchedule(ScheduleEntity schedule);

    //여러 스케줄의 예약 좌석 수를 한 번에 계산
    @Query("SELECT r.schedule.scheduleId, COUNT(r) FROM ReservationSeatEntity r " +
            "WHERE r.schedule IN :schedules GROUP BY r.schedule.scheduleId")
    List<Object[]> countReservedSeatsGrouped(@Param("schedules") List<ScheduleEntity> schedules);

}
