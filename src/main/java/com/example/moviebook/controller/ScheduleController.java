package com.example.moviebook.controller;

import com.example.moviebook.dto.ScheduleDto;
import com.example.moviebook.service.ScheduleService;
import com.example.moviebook.util.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;

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

    @Operation(summary = "특정 영화의 모든 상영일정 조회")
    @GetMapping("/movie/{movieId}")
    public ResponseEntity<ApiResponse<List<ScheduleDto.SimpleSchedule>>> getSchedulesByMovie(@PathVariable Long movieId) {
        List<ScheduleDto.SimpleSchedule> result = scheduleService.getSchedulesByMovie(movieId);
        return ResponseEntity.ok(new ApiResponse<>(true, "해당 영화의 상영일정 조회 성공", result));
    }

    @Operation(summary = "기준 일정 이후 일정 일괄 수정")
    @PutMapping("/batch-update")
    public ResponseEntity<ApiResponse<?>> updateSchedules(
            @RequestParam Long baseScheduleId,
            @RequestParam Long movieId,
            @RequestParam Long theaterId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime newStartTime,
            @RequestParam String subtitle
    ) {
        try {
            var updated = scheduleService.updateSchedulesFrom(
                    baseScheduleId, movieId, theaterId, newStartTime, subtitle
            );
            return ResponseEntity.ok(new ApiResponse<>(true, "상영 일정이 일괄 수정되었습니다.", updated));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }


    @Operation(summary = "특정 영화의 상영 일정 전체 삭제")
    @DeleteMapping("/movie/{movieId}")
    public ResponseEntity<ApiResponse<Void>> deleteSchedulesByMovie(@PathVariable Long movieId) {
        scheduleService.deleteSchedulesByMovie(movieId);
        return ResponseEntity.ok(new ApiResponse<>(true, "영화 상영 일정이 모두 삭제되었습니다.", null));
    }
}
