package com.example.moviebook.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TheaterDto {
    private Long theaterId;
    private String theaterName;
    private int totalSeats;
    private List<String> screenTypes;
}
