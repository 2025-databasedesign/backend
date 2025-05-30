package com.example.moviebook.controller;

import com.example.moviebook.dto.SeatHoldRequestDto;
import com.example.moviebook.service.SeatHoldService;
import com.example.moviebook.util.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/seats")
@RequiredArgsConstructor
public class SeatHoldController {

    @Autowired
    private SeatHoldService seatHoldService;

    // 좌석 Hold 요청 (예: 상태 2)
    @PostMapping("/hold")
    public ResponseEntity<ApiResponse<Void>> holdSeats(@RequestBody SeatHoldRequestDto request) {
        seatHoldService.holdSeat(request);
        return ResponseEntity.ok(new ApiResponse<>(true, "좌석이 임시 점유되었습니다.", null));
    }

    // 좌석 Hold 해제
    @DeleteMapping("/hold")
    public ResponseEntity<ApiResponse<Void>> releaseSeats(@RequestBody SeatHoldRequestDto request) {
        seatHoldService.releaseSeat(request);
        return ResponseEntity.ok(new ApiResponse<>(true, "좌석 임시 점유가 해제되었습니다.",null));
    }

    // 특정 스케줄의 좌석 상태 조회 (0,1,2,3)
    @GetMapping("/schedules/{scheduleId}/seats/status")
    public ResponseEntity<ApiResponse<Map<String, Integer>>> getSeatStatus(@PathVariable Long scheduleId) {
        Map<String, Integer> seatStatusMap = seatHoldService.getSeatStatus(scheduleId);
        return ResponseEntity.ok(new ApiResponse<>(true, "좌석 상태 조회 성공", seatStatusMap));
    }
}