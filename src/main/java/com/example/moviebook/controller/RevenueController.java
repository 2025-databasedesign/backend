package com.example.moviebook.controller;

import com.example.moviebook.service.RevenueService;
import com.example.moviebook.util.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/api/revenue")
@RequiredArgsConstructor
public class RevenueController {

    private final RevenueService revenueService;

    @GetMapping("/movie")
    public ResponseEntity<ApiResponse<Map<String, Integer>>> getRevenueByMovie() {
        Map<String, Integer> result = revenueService.getRevenueByMovie();
        return ResponseEntity.ok(new ApiResponse<>(true, "영화별 매출 조회 성공", result));
    }

    @GetMapping("/theater")
    public ResponseEntity<ApiResponse<Map<String, Integer>>> getRevenueByTheater() {
        Map<String, Integer> result = revenueService.getRevenueByTheater();
        return ResponseEntity.ok(new ApiResponse<>(true, "상영관별 매출 조회 성공", result));
    }

    @GetMapping("/date")
    public ResponseEntity<ApiResponse<Map<LocalDate, Integer>>> getRevenueByDate() {
        Map<LocalDate, Integer> result = revenueService.getRevenueByDate();
        return ResponseEntity.ok(new ApiResponse<>(true, "날짜별 매출 조회 성공", result));
    }
}
