package com.example.moviebook.service;

import com.example.moviebook.dto.ReservationDto;
import com.example.moviebook.entity.*;
import com.example.moviebook.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.security.core.context.SecurityContextHolder;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReservationService {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    @Autowired
    private SeatRepository seatRepository;
    @Autowired
    private TicketRepository ticketRepository;
    @Autowired
    private ReservationRepository reservationRepository;
    @Autowired
    private ReservationSeatRepository reservationSeatRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ScheduleRepository scheduleRepository;

    //예매 기능
    @Transactional
    public ReservationDto reserve(ReservationDto request) {
        Long userId = Long.valueOf(SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString());
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자 정보가 없습니다."));

        ScheduleEntity schedule = scheduleRepository.findById(request.getScheduleId())
                .orElseThrow(() -> new IllegalArgumentException("상영 정보를 찾을 수 없습니다."));

        List<SeatEntity> seats = seatRepository.findBySeatNumberInAndTheater_TheaterId(
                request.getSeatNumbers(), schedule.getTheater().getTheaterId());

        for (SeatEntity seat : seats) {
            if (reservationSeatRepository.existsByScheduleAndSeat(schedule, seat)) {
                throw new IllegalStateException("이미 예약된 좌석이 있습니다: " + seat.getSeatNumber());
            }
        }

        int totalPrice = schedule.getPrice() * seats.size();

        TicketEntity ticket = new TicketEntity();
        ticket.setBookedAt(LocalDateTime.now());
        ticket.setPrice(totalPrice);
        ticket.setPaymentStatus("PAID");
        ticket.setIssued(true);
        ticket.setMovie(schedule.getMovie());
        ticket.setTheater(schedule.getTheater());
        ticketRepository.save(ticket);

        ReservationEntity reservation = new ReservationEntity();
        reservation.setUser(user);
        reservation.setTicket(ticket);
        reservation.setReservedAt(LocalDateTime.now());
        reservation.setTotalPrice(totalPrice);
        reservation.setPaymentMethod(request.getPaymentMethod());
        reservation.setSchedule(schedule);
        reservationRepository.save(reservation);

        for (SeatEntity seat : seats) {
            ReservationSeatEntity rs = new ReservationSeatEntity();
            rs.setReservation(reservation);
            rs.setSeat(seat);
            rs.setSchedule(schedule);
            reservationSeatRepository.save(rs);
        }

        for (SeatEntity seat : seats) {
            String seatKey = "hold:" + schedule.getScheduleId() + ":" + seat.getSeatNumber();
            redisTemplate.delete(seatKey); // 예매 확정 후 HOLD 해제
        }

        return new ReservationDto(
                reservation.getReservationId(),
                request.getScheduleId(),
                request.getSeatNumbers(),
                totalPrice,
                seats.size(),
                reservation.getReservedAt(),
                schedule.getMovie().getTitle(),
                schedule.getTheater().getTheaterName(),
                request.getPaymentMethod(),
                null
        );
    }

    // 예매 조회
    public List<ReservationDto> getMyReservations() {
        Long userId = Long.valueOf(SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString());
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자 정보가 없습니다."));

        List<ReservationEntity> reservations = reservationRepository.findByUser(user);

        return reservations.stream().map(res -> {
            TicketEntity ticket = res.getTicket();
            String movieTitle = ticket.getMovie().getTitle();
            List<ReservationSeatEntity> reservationSeats = reservationSeatRepository.findByReservation(res);
            List<String> seatNumbers = reservationSeats.stream()
                    .map(rs -> rs.getSeat().getSeatNumber())
                    .toList();
            String theaterName = ticket.getTheater().getTheaterName();

            return new ReservationDto(
                    res.getReservationId(),
                    reservationSeats.get(0).getSchedule().getScheduleId(),
                    seatNumbers,
                    res.getTotalPrice(),
                    seatNumbers.size(),
                    res.getReservedAt(),
                    movieTitle,
                    theaterName,
                    res.getPaymentMethod(),
                    null
            );
        }).toList();
    }

    // 예매 변경
    @Transactional
    public ReservationDto updateReservation(Long reservationId, ReservationDto request) {
        Long userId = Long.valueOf(SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString());

        ReservationEntity reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("해당 예매가 존재하지 않습니다."));

        if (!reservation.getUser().getId().equals(userId)) {
            throw new SecurityException("본인의 예매만 변경할 수 있습니다.");
        }

        List<ReservationSeatEntity> oldSeats = reservationSeatRepository.findByReservation(reservation);
        reservationSeatRepository.deleteAll(oldSeats);

        List<SeatEntity> newSeats = seatRepository.findBySeatNumberInAndTheater_TheaterId(
                request.getSeatNumbers(), oldSeats.get(0).getSeat().getTheater().getTheaterId());

        for (SeatEntity seat : newSeats) {
            if (reservationSeatRepository.existsByScheduleAndSeat(oldSeats.get(0).getSchedule(), seat)) {
                throw new IllegalStateException("이미 예약된 좌석이 있습니다: " + seat.getSeatNumber());
            }

            ReservationSeatEntity rs = new ReservationSeatEntity();
            rs.setReservation(reservation);
            rs.setSeat(seat);
            rs.setSchedule(oldSeats.get(0).getSchedule());
            reservationSeatRepository.save(rs);
        }

        TicketEntity ticket = reservation.getTicket();
        ticket.setBookedAt(request.getNewShowTime());
        ticketRepository.save(ticket);

        return new ReservationDto(
                reservation.getReservationId(),
                oldSeats.get(0).getSchedule().getScheduleId(),
                request.getSeatNumbers(),
                reservation.getTotalPrice(),
                request.getSeatNumbers().size(),
                reservation.getReservedAt(),
                ticket.getMovie().getTitle(),
                oldSeats.get(0).getSeat().getTheater().getTheaterName(),
                reservation.getPaymentMethod(),
                request.getNewShowTime()
        );
    }

    public void cancelReservation(Long reservationId) {
        ReservationEntity reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 예매입니다."));

        List<ReservationSeatEntity> reservedSeats = reservationSeatRepository.findByReservation(reservation);

        for (ReservationSeatEntity rs : reservedSeats) {
            SeatEntity seat = rs.getSeat();
            seat.setStatus("AVAILABLE");
            reservationSeatRepository.delete(rs);
        }

        TicketEntity ticket = reservation.getTicket();
        if (ticket != null) {
            reservation.setTicket(null);
            reservationRepository.save(reservation);
            ticketRepository.delete(ticket);
        }

        reservationRepository.delete(reservation);
    }
}
