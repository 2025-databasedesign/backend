package com.example.moviebook.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GenreDto {
    private Long genreId;
    private String genreName;
    private List<MovieTitleDto> movies;

    @Data
    @AllArgsConstructor
    public static class MovieTitleDto {
        private Long movieId;
        private String title;
    }
}