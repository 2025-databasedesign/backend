package com.example.moviebook.controller;

import com.example.moviebook.dto.SeatBatchUpdateRequestDto;
import com.example.moviebook.dto.SeatInitRequestDto;
import com.example.moviebook.dto.SeatUpdateRequestDto;
import com.example.moviebook.entity.SeatEntity;
import com.example.moviebook.entity.TheaterEntity;
import com.example.moviebook.repository.SeatRepository;
import com.example.moviebook.repository.TheaterRepository;
import com.example.moviebook.util.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/theaters")
@RequiredArgsConstructor
//좌석 추가용 클래스
public class TheaterAdminController {

    @Autowired
    private TheaterRepository theaterRepository;
    @Autowired
    private SeatRepository seatRepository;

    @PostMapping("/{theaterId}/seats")
    public ResponseEntity<ApiResponse<Void>> initializeSeats(@PathVariable Long theaterId,
                                                             @RequestBody SeatInitRequestDto request) {
        TheaterEntity theater = theaterRepository.findById(theaterId)
                .orElseThrow(() -> new IllegalArgumentException("상영관이 존재하지 않습니다."));

        List<List<Integer>> layout = request.getLayout();

        for (int row = 0; row < layout.size(); row++) {
            int seatColCount = 0; // 좌석이 실제로 생성된 열 순서 (좌석명에 사용)
            for (int col = 0; col < layout.get(row).size(); col++) {
                Integer value = layout.get(row).get(col);
                if (value != null && value.intValue() == 1) {
                    SeatEntity seat = new SeatEntity();
                    seat.setRowNo(row);
                    seat.setColNo(col);
                    seat.setSeatNumber(generateSeatNumber(row, seatColCount)); // 이걸로 변경!
                    seat.setStatus("AVAILABLE");
                    seat.setTheater(theater);
                    seatRepository.save(seat);
                    seatColCount++; // 좌석이 있을 때만 증가
                }
            }
        }

        return ResponseEntity.ok(new ApiResponse<>(true, "좌석 초기화 완료", null));
    }

    // A1, B5 등 형식 지정용 함수
    private String generateSeatNumber(int row, int col) {
        char rowChar = (char) ('A' + row); // 0 → A
        return rowChar + String.valueOf(col + 1); // 0 → 1
    }

    //좌석 삭제
    @DeleteMapping("/{theaterId}/seats")
    public ResponseEntity<ApiResponse<Void>> deleteAllSeats(@PathVariable Long theaterId) {
        TheaterEntity theater = theaterRepository.findById(theaterId)
                .orElseThrow(() -> new IllegalArgumentException("상영관이 존재하지 않습니다."));

        List<SeatEntity> seats = seatRepository.findByTheater(theater);
        seatRepository.deleteAll(seats);

        return ResponseEntity.ok(new ApiResponse<>(true, "해당 상영관의 모든 좌석이 삭제되었습니다.", null));
    }

    //좌석 수정
    @PatchMapping("/seats/{seatId}")
    public ResponseEntity<ApiResponse<Void>> updateSeat(@PathVariable Long seatId,
                                                        @RequestBody SeatUpdateRequestDto request) {
        SeatEntity seat = seatRepository.findById(seatId)
                .orElseThrow(() -> new IllegalArgumentException("해당 좌석이 존재하지 않습니다."));

        if (request.getSeatNumber() != null) seat.setSeatNumber(request.getSeatNumber());
        if (request.getRowNo() != null) seat.setRowNo(request.getRowNo());
        if (request.getColNo() != null) seat.setColNo(request.getColNo());
        if (request.getStatus() != null) seat.setStatus(request.getStatus());

        seatRepository.save(seat);

        return ResponseEntity.ok(new ApiResponse<>(true, "좌석 정보가 수정되었습니다.", null));
    }

    //여러 좌석 수정
    @PatchMapping("/seats/batch")
    public ResponseEntity<ApiResponse<Void>> updateSeatsBatch(@RequestBody SeatBatchUpdateRequestDto request) {
        List<SeatBatchUpdateRequestDto.SeatUpdateItem> items = request.getSeats();

        for (SeatBatchUpdateRequestDto.SeatUpdateItem item : items) {
            SeatEntity seat = seatRepository.findById(item.getSeatId())
                    .orElseThrow(() -> new IllegalArgumentException("해당 좌석이 존재하지 않습니다. ID: " + item.getSeatId()));

            if (item.getSeatNumber() != null) seat.setSeatNumber(item.getSeatNumber());
            if (item.getRowNo() != null) seat.setRowNo(item.getRowNo());
            if (item.getColNo() != null) seat.setColNo(item.getColNo());
            if (item.getStatus() != null) seat.setStatus(item.getStatus());

            seatRepository.save(seat);
        }

        return ResponseEntity.ok(new ApiResponse<>(true, "좌석이 일괄 수정되었습니다.", null));
    }
}