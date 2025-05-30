package com.example.moviebook.controller;

import com.example.moviebook.dto.ScheduleDto;
import com.example.moviebook.service.ScheduleService;
import com.example.moviebook.util.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/schedules")
public class ScheduleController {

    @Autowired
    private ScheduleService scheduleService;

    @Operation(summary = "상영 일정 등록")
    @PostMapping
    public ResponseEntity<ApiResponse<Void>> registerSchedule(@RequestBody ScheduleDto dto) {
        scheduleService.registerSchedule(dto);
        return ResponseEntity.ok(new ApiResponse<>(true, "상영 일정이 등록되었습니다.", null));
    }

    @Operation(summary = "상영 일정 조회 (yyyy-MM-dd)")
    @GetMapping("/{date}")
    public ResponseEntity<ApiResponse<ScheduleDto>> getSchedule(@PathVariable String date) {
        ScheduleDto result = scheduleService.getScheduleByDate(date);
        return ResponseEntity.ok(new ApiResponse<>(true, "상영 일정 조회 성공", result));
    }
}
