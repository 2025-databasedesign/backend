package com.example.moviebook.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;
import java.util.ArrayList;

@Entity
@Table(name = "THEATER")
@Getter
@Setter
@NoArgsConstructor
public class TheaterEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "THEATER_ID")
    private Long theaterId;

    @Column(name = "THEATER_NAME")
    private String theaterName;

    @Column(name = "TOTAL_SEATS")
    private int totalSeats;

    // 상영 포맷 (예: "IMAX", "2D")
    @Column(name = "FORMAT")
    private String format;

    // 해당 포맷의 가격
    @Column(name = "PRICE")
    private int price;


    @OneToMany(mappedBy = "theater", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SeatEntity> seats = new ArrayList<>();
}
