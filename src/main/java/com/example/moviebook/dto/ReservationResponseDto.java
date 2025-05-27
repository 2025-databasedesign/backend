package com.example.moviebook.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
public class ReservationResponseDto {
    private Long reservationId;
    private String movieTitle;
    private String theaterName;
    private List<String> seatNumbers;
    private int totalPrice;
    private int audienceCount;
    private LocalDateTime reservedAt;
}
