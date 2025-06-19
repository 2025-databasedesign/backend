package com.example.moviebook.service;

import com.example.moviebook.entity.ReservationEntity;
import com.example.moviebook.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RevenueService {

    private final ReservationRepository reservationRepository;

    // 🎬 영화별 매출
    public Map<String, Integer> getRevenueByMovie() {
        return reservationRepository.findAll().stream()
                .collect(Collectors.groupingBy(
                        r -> r.getSchedule().getMovie().getTitle(),
                        Collectors.summingInt(ReservationEntity::getTotalPrice) // ✅ 실제 예매 금액 기준
                ));
    }

    // 🏟 상영관별 매출
    public Map<String, Integer> getRevenueByTheater() {
        return reservationRepository.findAll().stream()
                .collect(Collectors.groupingBy(
                        r -> r.getSchedule().getTheater().getTheaterName(),
                        Collectors.summingInt(ReservationEntity::getTotalPrice) // ✅ 실제 예매 금액 기준
                ));
    }

    // 📆 날짜별 매출
    public Map<LocalDate, Integer> getRevenueByDate() {
        return reservationRepository.findAll().stream()
                .collect(Collectors.groupingBy(
                        r -> r.getSchedule().getStartTime().toLocalDate(),
                        Collectors.summingInt(ReservationEntity::getTotalPrice) // ✅ 실제 예매 금액 기준
                ));
    }
}
