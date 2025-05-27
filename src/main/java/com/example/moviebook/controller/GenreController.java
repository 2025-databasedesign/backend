// GenreController.java
package com.example.moviebook.controller;

import com.example.moviebook.dto.GenreDto;
import com.example.moviebook.entity.GenreEntity;
import com.example.moviebook.service.GenreService;
import com.example.moviebook.util.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/genres")
public class GenreController {

    @Autowired
    private GenreService genreService;

    @PostMapping
    public ResponseEntity<ApiResponse<GenreDto>> registerGenre(@RequestBody GenreDto Dto) {
        GenreDto registeredGenre = genreService.registerGenre(Dto);
        return ResponseEntity.ok(new ApiResponse<>(true, "장르 등록 성공", registeredGenre));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<GenreEntity>>> getAllGenres() {
        return ResponseEntity.ok(new ApiResponse<>(true, "장르 목록 조회", genreService.getAllGenres()));
    }
}
