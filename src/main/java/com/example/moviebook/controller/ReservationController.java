package com.example.moviebook.controller;

import com.example.moviebook.dto.ReservationRequestDto;
import com.example.moviebook.dto.ReservationResponseDto;
import com.example.moviebook.dto.ReservationUpdateRequestDto;
import com.example.moviebook.service.ReservationService;
import com.example.moviebook.util.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    @Autowired
    private ReservationService reservationService;

    //영화 예매
    @PostMapping
    public ResponseEntity<ApiResponse<ReservationResponseDto>> makeReservation(@RequestBody ReservationRequestDto request) {
        ReservationResponseDto result = reservationService.reserve(request);
        return ResponseEntity.ok(new ApiResponse<>(true, "예매 성공", result));
    }

    //예애내역 조회
    @GetMapping("/mine")
    public ResponseEntity<ApiResponse<List<ReservationResponseDto>>> getMyReservations() {
        List<ReservationResponseDto> result = reservationService.getMyReservations();
        return ResponseEntity.ok(new ApiResponse<>(true, "예매 내역 조회 성공", result));
    }

    //예매 수정
    @PutMapping("/{reservationId}")
    public ResponseEntity<ApiResponse<ReservationResponseDto>> updateReservation(
            @PathVariable Long reservationId,
            @RequestBody ReservationUpdateRequestDto request) {

        ReservationResponseDto result = reservationService.updateReservation(reservationId, request);
        return ResponseEntity.ok(new ApiResponse<>(true, "예매 변경 성공", result));
    }
}
