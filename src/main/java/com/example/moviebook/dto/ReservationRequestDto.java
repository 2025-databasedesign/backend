package com.example.moviebook.dto;

import lombok.Data;
import java.util.List;

@Data
public class ReservationRequestDto {
    private List<Long> seatIds;
    private Long movieId;
    private int price;
    private String paymentMethod;
}
