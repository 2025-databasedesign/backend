package com.example.moviebook.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class ReservationUpdateRequestDto {
    private List<Long> newSeatIds;           // 새 좌석 ID 리스트
    private LocalDateTime newShowTime;       // 새 상영 시간
}
