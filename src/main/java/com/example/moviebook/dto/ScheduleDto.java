package com.example.moviebook.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Schema(description = "상영 일정 요청 DTO")
public class ScheduleDto {

    @Schema(description = "상영 날짜 (yyyy-MM-dd)", example = "2025-06-01")
    private String date;

    @Schema(description = "영화 스케줄 목록")
    private List<MovieSchedule> schedules;

    @Getter
    @Setter
    @Schema(description = "영화 스케줄")
    public static class MovieSchedule {

        @Schema(description = "영화 ID", example = "1")
        private Long movieId;

        @Schema(hidden = true)
        private String movieName;

        @Schema(hidden = true)
        private int durationMinutes;

        @Schema(hidden = true)
        private String grade;

        private List<TheaterSchedule> theaters;
    }

    @Getter
    @Setter
    @Schema(description = "극장 스케줄")
    public static class TheaterSchedule {

        @Schema(example = "A")
        private String theaterId;

        private String theaterName;
        private String format;
        private String subDub;
        private int availSeat;
        private int totalSeat;

        @Schema(description = "시작 시간 목록", example = "[\"08:00\", \"10:30\"]")
        private List<String> startTimes;

        @Schema(description = "종료 시간 목록", example = "[\"10:28\", \"12:58\"]")
        private List<String> endTimes;

        @Schema(description = "상영일정 ID 목록", example = "[101, 102]")
        private List<Long> scheduleIds;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SimpleSchedule {
        private Long scheduleId;
        private String movieTitle;
        private String theaterName;
        private LocalDateTime startTime;
        private String format;
        private String subDub;
    }
}
