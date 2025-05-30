package com.example.moviebook.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SeatInitRequestDto {
    private int[][] layout; // 2차원 배열
}
