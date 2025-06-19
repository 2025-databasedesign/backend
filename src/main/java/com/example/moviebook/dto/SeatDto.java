package com.example.moviebook.dto;

import lombok.Data;

@Data
public class SeatDto {
    private Long seatId;
    private String seatNumber;
    private int rowNo;
    private int colNo;
    private String status;
}
