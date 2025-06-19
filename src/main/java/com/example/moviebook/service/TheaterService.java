package com.example.moviebook.service;

import com.example.moviebook.dto.SeatDto;
import com.example.moviebook.dto.TheaterDto;
import com.example.moviebook.entity.SeatEntity;
import com.example.moviebook.entity.TheaterEntity;
import com.example.moviebook.repository.TheaterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TheaterService {

    @Autowired
    private TheaterRepository theaterRepository;

    public TheaterDto registerTheater(TheaterDto dto) {
        TheaterEntity entity = new TheaterEntity();
        entity.setTheaterName(dto.getTheaterName());
        entity.setTotalSeats(dto.getTotalSeats());
        entity.setFormat(dto.getFormat());
        entity.setPrice(dto.getPrice());

        TheaterEntity saved = theaterRepository.save(entity);

        return new TheaterDto(
                saved.getTheaterId(),
                saved.getTheaterName(),
                saved.getTotalSeats(),
                saved.getFormat(),
                saved.getPrice(),
                null
        );
    }

    public List<TheaterDto> getAllTheaters() {
        return theaterRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public TheaterDto getTheaterById(Long id) {
        TheaterEntity theaterEntity = theaterRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("이력서를 찾을 수 없습니다."));
        return convertToDto(theaterEntity);
    }

    private TheaterDto convertToDto(TheaterEntity entity) {
        List<SeatDto> seatDtos = null;
        if (entity.getSeats() != null) {
            seatDtos = entity.getSeats().stream()
                    .map(this::convertSeatToDto)
                    .collect(Collectors.toList());
        }

        return new TheaterDto(
                entity.getTheaterId(),
                entity.getTheaterName(),
                entity.getTotalSeats(),
                entity.getFormat(),
                entity.getPrice(),
                seatDtos
        );
    }

    private SeatDto convertSeatToDto(SeatEntity seat) {
        SeatDto dto = new SeatDto();
        dto.setSeatId(seat.getSeatId());
        dto.setSeatNumber(seat.getSeatNumber());
        dto.setRowNo(seat.getRowNo());
        dto.setColNo(seat.getColNo());
        dto.setStatus(seat.getStatus());
        return dto;
    }
}