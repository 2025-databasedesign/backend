package com.example.moviebook.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SeatUpdateRequestDto {
    private String seatNumber;  // 수정할 좌석번호
    private Integer rowNo;
    private Integer colNo;
    private String status;
}