package com.example.moviebook.controller;

import com.example.moviebook.service.ReservationService;
import com.example.moviebook.util.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.moviebook.dto.ReservationDto;
import java.util.List;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    @Autowired
    private ReservationService reservationService;

    // 영화 예매
    @PostMapping
    public ResponseEntity<ApiResponse<ReservationDto>> makeReservation(@RequestBody ReservationDto request) {
        ReservationDto result = reservationService.reserve(request);
        return ResponseEntity.ok(new ApiResponse<>(true, "예매 성공", result));
    }

    // 예애내역 조회
    @GetMapping("/mine")
    public ResponseEntity<ApiResponse<List<ReservationDto>>> getMyReservations() {
        List<ReservationDto> result = reservationService.getMyReservations();
        return ResponseEntity.ok(new ApiResponse<>(true, "예매 내역 조회 성공", result));
    }

    // 예매 수정
    @PutMapping("/{reservationId}")
    public ResponseEntity<ApiResponse<ReservationDto>> updateReservation(
            @PathVariable Long reservationId,
            @RequestBody ReservationDto request) {

        ReservationDto result = reservationService.updateReservation(reservationId, request);
        return ResponseEntity.ok(new ApiResponse<>(true, "예매 변경 성공", result));
    }

    // 예매 취소
    @DeleteMapping("/{reservationId}")
    public ResponseEntity<ApiResponse<Void>> cancelReservation(@PathVariable Long reservationId) {
        reservationService.cancelReservation(reservationId);
        return ResponseEntity.ok(new ApiResponse<>(true, "예매 취소 성공", null));
    }
}
