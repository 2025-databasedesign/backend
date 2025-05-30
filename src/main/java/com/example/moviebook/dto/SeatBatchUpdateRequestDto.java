package com.example.moviebook.dto;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class SeatBatchUpdateRequestDto {
    private List<SeatUpdateItem> seats;

    @Getter
    @Setter
    public static class SeatUpdateItem {
        private Long seatId;
        private String seatNumber;
        private Integer rowNo;
        private Integer colNo;
        private String status;
    }
}
