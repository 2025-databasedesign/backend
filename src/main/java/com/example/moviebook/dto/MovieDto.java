package com.example.moviebook.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MovieDto {
    private Long movieId;
    private String title;
    private int runningTime;
    private LocalDate releaseDate;
    private String director;
    private List<String> actors;
    private String rating;
    private List<String> formats;
    private List<Long> genreIds; //등록용
    private List<String> genreNames; //조회용
}
