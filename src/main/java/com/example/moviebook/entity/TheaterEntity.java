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

    @Column(name = "SCREEN_TYPE")
    private String screenType; // 예: IMAX, Dolby

    @OneToMany(mappedBy = "theater", cascade = CascadeType.ALL)
    private List<SeatEntity> seats = new ArrayList<>();
}
