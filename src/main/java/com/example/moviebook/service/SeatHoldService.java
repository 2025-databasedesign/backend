package com.example.moviebook.service;

import com.example.moviebook.dto.SeatHoldRequestDto;
import com.example.moviebook.entity.ScheduleEntity;
import com.example.moviebook.entity.SeatEntity;
import com.example.moviebook.repository.SeatRepository;
import com.example.moviebook.repository.ScheduleRepository;
import com.example.moviebook.repository.ReservationSeatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SeatHoldService {

    @Autowired
    private SeatRepository seatRepository;
    @Autowired
    private ScheduleRepository scheduleRepository;
    @Autowired
    private ReservationSeatRepository reservationSeatRepository;

    private final RedisTemplate<String, String> redisTemplate;
    private static final Duration HOLD_DURATION = Duration.ofMinutes(5);

    private String buildSeatKey(Long scheduleId, String seatNumber) {
        return "hold:" + scheduleId + ":" + seatNumber;
    }

    // 현재 로그인 중인 사용자 ID 추출
    private String getCurrentUserId() {
        return SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
    }

    // 좌석 HOLD (로그인 사용자 기준)
    public void holdSeat(SeatHoldRequestDto request) {
        String userId = getCurrentUserId();
        for (String seatNumber : request.getSeatNumbers()) {
            String seatKey = buildSeatKey(request.getScheduleId(), seatNumber);
            redisTemplate.opsForValue().set(seatKey, userId, HOLD_DURATION);
        }
    }

    // HOLD 해제
    public void releaseSeat(SeatHoldRequestDto request) {
        String userId = getCurrentUserId();
        for (String seatNumber : request.getSeatNumbers()) {
            String seatKey = buildSeatKey(request.getScheduleId(), seatNumber);

            // 자기 좌석만 해제 가능하도록 보호 (선택 사항)
            String currentHolder = redisTemplate.opsForValue().get(seatKey);
            if (userId.equals(currentHolder)) {
                redisTemplate.delete(seatKey);
            }
        }
    }

    // 좌석 상태 조회
    public Map<String, Integer> getSeatStatus(Long scheduleId) {
        ScheduleEntity schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new IllegalArgumentException("스케줄 없음"));

        List<SeatEntity> allSeats = seatRepository.findByTheater_TheaterId(schedule.getTheater().getTheaterId());
        Map<String, Integer> statusMap = new HashMap<>();

        for (SeatEntity seat : allSeats) {
            String seatNumber = seat.getSeatNumber();
            String key = buildSeatKey(scheduleId, seatNumber);

            if (redisTemplate.hasKey(key)) {
                statusMap.put(seatNumber, 2); // HOLD
            } else if (reservationSeatRepository.existsByScheduleAndSeat(schedule, seat)) {
                statusMap.put(seatNumber, 3); // RESERVED
            } else {
                statusMap.put(seatNumber, 1); // AVAILABLE
            }
        }

        return statusMap;
    }
}
