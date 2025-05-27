package com.example.moviebook.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "SEAT")
@Getter
@Setter
@NoArgsConstructor
public class SeatEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SEAT_ID")
    private Long seatId;

    @Column(name = "SEAT_NUMBER")
    private String seatNumber;

    @Column(name = "ROW_NO")
    private int rowNo;

    @Column(name = "COL_NO")
    private int colNo;

    @Column(name = "STATUS")
    private String status; // 예: AVAILABLE, RESERVED

    @ManyToOne
    @JoinColumn(name = "THEATER_ID")
    private TheaterEntity theater;
}
