package com.example.moviebook.controller;

import com.example.moviebook.dto.TheaterDto;
import com.example.moviebook.entity.TheaterEntity;
import com.example.moviebook.service.TheaterService;
import com.example.moviebook.util.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/theaters")
public class TheaterController {

    @Autowired
    private TheaterService theaterService;

    //상영관 등록
    @PostMapping
    public ResponseEntity<ApiResponse<TheaterDto>> registerTheater(@RequestBody TheaterDto dto) {
        TheaterDto result = theaterService.registerTheater(dto);
        return ResponseEntity.ok(new ApiResponse<>(true, "상영관 등록 완료", result));
    }

    //모든 상영관 조회
    @GetMapping
    public ResponseEntity<ApiResponse<List<TheaterEntity>>> getAllTheaters() {
        return ResponseEntity.ok(new ApiResponse<>(true, "모든 상영관 조회 성공", theaterService.getAllTheaters()));
    }

    //특정 상영관 조회
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TheaterDto>> getTheater(@PathVariable Long id) {
        TheaterDto theater = theaterService.getTheaterById(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "상영관 조회 성공", theater));
    }
}