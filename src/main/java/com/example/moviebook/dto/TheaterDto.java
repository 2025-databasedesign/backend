package com.example.moviebook.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TheaterDto {
    private Long theaterId;
    private String theaterName;
    private int totalSeats;
    private String screenType;
}
