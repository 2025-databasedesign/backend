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

    @ElementCollection
    @CollectionTable(name = "THEATER_SCREEN_TYPE", joinColumns = @JoinColumn(name = "THEATER_ID"))
    @Column(name = "SCREEN_TYPE_NAME")
    private List<String> screenTypes = new ArrayList<>();

    @OneToMany(mappedBy = "theater", cascade = CascadeType.ALL)
    private List<SeatEntity> seats = new ArrayList<>();
}
