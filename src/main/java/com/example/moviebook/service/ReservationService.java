package com.example.moviebook.service;

import com.example.moviebook.dto.ReservationRequestDto;
import com.example.moviebook.dto.ReservationResponseDto;
import com.example.moviebook.dto.ReservationUpdateRequestDto;
import com.example.moviebook.entity.*;
import com.example.moviebook.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.security.core.context.SecurityContextHolder;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReservationService {

    @Autowired
    private SeatRepository seatRepository;
    @Autowired
    private TicketRepository ticketRepository;
    @Autowired
    private ReservationRepository reservationRepository;
    @Autowired
    private ReservationSeatRepository reservationSeatRepository;
    @Autowired
    private MovieRepository movieRepository;

    @Transactional
    public ReservationResponseDto reserve(ReservationRequestDto request) {
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        List<SeatEntity> seats = seatRepository.findAllById(request.getSeatIds());

        for (SeatEntity seat : seats) {
            if (!"AVAILABLE".equals(seat.getStatus())) {
                throw new IllegalStateException("이미 예약된 좌석이 있습니다: " + seat.getSeatNumber());
            }
        }

        for (SeatEntity seat : seats) {
            seat.setStatus("RESERVED");
        }

        // 티켓 생성
        TicketEntity ticket = new TicketEntity();
        ticket.setBookedAt(LocalDateTime.now());
        ticket.setPrice(request.getPrice());
        ticket.setPaymentStatus("PAID");
        ticket.setIssued(true);

        // ⬇ 요청에 영화 ID가 있다고 가정하고 설정
        MovieEntity movie = movieRepository.findById(request.getMovieId())
                .orElseThrow(() -> new IllegalArgumentException("해당 영화가 존재하지 않습니다."));
        ticket.setMovie(movie);

        ticketRepository.save(ticket);

        // 예매 생성
        ReservationEntity reservation = new ReservationEntity();
        reservation.setUserId(userId);
        reservation.setTicket(ticket);
        reservation.setReservedAt(LocalDateTime.now());
        reservation.setTotalPrice(request.getPrice());
        reservation.setPaymentMethod(request.getPaymentMethod());
        reservationRepository.save(reservation);

        // 좌석-예매 연결
        for (SeatEntity seat : seats) {
            ReservationSeatEntity rs = new ReservationSeatEntity();
            rs.setReservation(reservation);
            rs.setSeat(seat);
            reservationSeatRepository.save(rs);
        }

        return new ReservationResponseDto(
                reservation.getReservationId(),
                movie.getTitle(),
                seats.get(0).getTheater().getTheaterName(),
                seats.stream().map(SeatEntity::getSeatNumber).toList(),
                reservation.getTotalPrice(),
                seats.size(),
                reservation.getReservedAt()
        );
    }

    public List<ReservationResponseDto> getMyReservations() {
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<ReservationEntity> reservations = reservationRepository.findByUserId(userId);

        return reservations.stream().map(res -> {
            TicketEntity ticket = res.getTicket();
            String movieTitle = ticket.getMovie().getTitle();

            List<ReservationSeatEntity> reservationSeats = reservationSeatRepository.findByReservation(res);

            List<String> seatNumbers = reservationSeats.stream()
                    .map(rs -> rs.getSeat().getSeatNumber())
                    .toList();

            String theaterName = reservationSeats.stream()
                    .findFirst()
                    .map(rs -> rs.getSeat().getTheater().getTheaterName())
                    .orElse("미정");

            return new ReservationResponseDto(
                    res.getReservationId(),
                    movieTitle,
                    theaterName,
                    seatNumbers,
                    res.getTotalPrice(),
                    seatNumbers.size(),
                    res.getReservedAt()
            );
        }).toList();
    }

    @Transactional
    public ReservationResponseDto updateReservation(Long reservationId, ReservationUpdateRequestDto request) {
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        ReservationEntity reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("해당 예매가 존재하지 않습니다."));

        // 로그인한 사용자와 예매 소유자 일치 확인
        if (!reservation.getUserId().equals(userId)) {
            throw new SecurityException("본인의 예매만 변경할 수 있습니다.");
        }

        // 기존 좌석 예약 해제
        List<ReservationSeatEntity> oldSeats = reservationSeatRepository.findByReservation(reservation);
        for (ReservationSeatEntity rs : oldSeats) {
            SeatEntity seat = rs.getSeat();
            seat.setStatus("AVAILABLE");
        }
        reservationSeatRepository.deleteAll(oldSeats);

        // 새 좌석 확인 및 예약
        List<SeatEntity> newSeats = seatRepository.findAllById(request.getNewSeatIds());
        for (SeatEntity seat : newSeats) {
            if (!"AVAILABLE".equals(seat.getStatus())) {
                throw new IllegalStateException("이미 예약된 좌석이 있습니다: " + seat.getSeatNumber());
            }
            seat.setStatus("RESERVED");

            ReservationSeatEntity rs = new ReservationSeatEntity();
            rs.setReservation(reservation);
            rs.setSeat(seat);
            reservationSeatRepository.save(rs);
        }

        // 예매 변경 (상영시간은 Ticket에 저장한다고 가정)
        TicketEntity ticket = reservation.getTicket();
        ticket.setBookedAt(request.getNewShowTime()); // 상영시간으로 사용
        ticketRepository.save(ticket);

        return new ReservationResponseDto(
                reservation.getReservationId(),
                ticket.getMovie().getTitle(),
                newSeats.get(0).getTheater().getTheaterName(),
                newSeats.stream().map(SeatEntity::getSeatNumber).toList(),
                reservation.getTotalPrice(),
                newSeats.size(),
                reservation.getReservedAt()
        );
    }
}
