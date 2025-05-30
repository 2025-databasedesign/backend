package com.example.moviebook.dto;

import lombok.Data;
import java.util.List;

@Data
public class SeatHoldRequestDto {
    private Long scheduleId;
    private List<String> seatNumbers;
}
