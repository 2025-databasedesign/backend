package com.example.moviebook.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "SCHEDULE")
@Getter
@Setter
@NoArgsConstructor
//상영정보 클래스
public class ScheduleEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "schedule_seq")
    @SequenceGenerator(name = "schedule_seq", sequenceName = "SCHEDULE_SEQ", allocationSize = 1)
    @Column(name = "SCHEDULE_ID")
    private Long scheduleId;

    @ManyToOne
    @JoinColumn(name = "THEATER_ID")
    private TheaterEntity theater;

    @ManyToOne
    @JoinColumn(name = "MOVIE_ID")
    private MovieEntity movie;

    @Column(name = "START_TIME")
    private LocalDateTime startTime;

    @Column(name = "FORMAT")
    private String format; // 예: 2D, 3D, IMAX

    @Column(name = "PRICE")
    private int price;

    @Column(name = "SUB_DUB")
    private String subDub;

    @Transient
    public LocalDateTime getEndTime() {
        return this.startTime.plusMinutes(this.movie.getRunningTime());
    }
}
