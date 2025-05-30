package com.example.moviebook.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservationDto {
    private Long reservationId;          // 응답용
    private Long scheduleId;             // 요청용
    private List<String> seatNumbers;    // 요청/응답 공용
    private int totalPrice;              // 응답용
    private int seatCount;               // 응답용
    private LocalDateTime reservedAt;    // 응답용
    private String movieTitle;           // 응답용
    private String theaterName;          // 응답용
    private String paymentMethod;        // 요청용
    private LocalDateTime newShowTime;   // 변경 요청용
}