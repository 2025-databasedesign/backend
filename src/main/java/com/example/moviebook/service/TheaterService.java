package com.example.moviebook.service;

import com.example.moviebook.dto.TheaterDto;
import com.example.moviebook.entity.TheaterEntity;
import com.example.moviebook.repository.TheaterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class TheaterService {

    @Autowired
    private TheaterRepository theaterRepository;

    public TheaterDto registerTheater(TheaterDto dto) {
        TheaterEntity entity = new TheaterEntity();
        entity.setTheaterName(dto.getTheaterName());
        entity.setTotalSeats(dto.getTotalSeats());
        entity.setScreenType(dto.getScreenType());

        TheaterEntity saved = theaterRepository.save(entity);

        return new TheaterDto(
                saved.getTheaterId(),
                saved.getTheaterName(),
                saved.getTotalSeats(),
                saved.getScreenType());
    }

    public List<TheaterEntity> getAllTheaters() {
        return theaterRepository.findAll();
    }

    public TheaterDto getTheaterById(Long id) {
        TheaterEntity theaterEntity = theaterRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("이력서를 찾을 수 없습니다."));
        return new TheaterDto(
                theaterEntity.getTheaterId(),
                theaterEntity.getTheaterName(),
                theaterEntity.getTotalSeats(),
                theaterEntity.getScreenType()
        );
    }
}