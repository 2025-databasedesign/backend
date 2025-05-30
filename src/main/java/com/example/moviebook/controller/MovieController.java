package com.example.moviebook.controller;

import com.example.moviebook.dto.MovieDto;
import com.example.moviebook.service.MovieService;
import com.example.moviebook.util.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/movies")
public class MovieController {

    @Autowired
    private MovieService movieService;


    @PostMapping
    public ResponseEntity<ApiResponse<MovieDto>> registerMovie(@RequestBody MovieDto dto) {
        MovieDto result = movieService.registerMovie(dto);
        return ResponseEntity.ok(new ApiResponse<>(true, "영화 등록 성공", result));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<MovieDto>>> getAllMovies() {
        return ResponseEntity.ok(new ApiResponse<>(true, "전체 영화 조회", movieService.getAllMovies()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteMovie(@PathVariable Long id) {
        movieService.deleteMovie(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "영화 삭제 성공", null));
    }

    @GetMapping("/grades")
    public ResponseEntity<ApiResponse<List<String>>> getAllRatings() {
        return ResponseEntity.ok(new ApiResponse<>(true, "등급 목록 조회", movieService.getAllGrades()));
    }

    @GetMapping("/grade/{grade}")
    public ResponseEntity<ApiResponse<List<MovieDto>>> getMoviesByRating(@PathVariable String grade) {
        return ResponseEntity.ok(new ApiResponse<>(true, "등급별 영화 조회", movieService.getMoviesByGrade(grade)));
    }
}
