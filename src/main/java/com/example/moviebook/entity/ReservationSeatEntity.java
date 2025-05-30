package com.example.moviebook.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "RESERVATION_SEAT")
@Getter
@Setter
@NoArgsConstructor
public class ReservationSeatEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "RESERVATION_ID")
    private ReservationEntity reservation;

    @ManyToOne
    @JoinColumn(name = "SEAT_ID")
    private SeatEntity seat;

    @ManyToOne
    @JoinColumn(name = "SCHEDULE_ID")
    private ScheduleEntity schedule;
}
