package com.example.moviebook.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "TICKET")
@Getter
@Setter
@NoArgsConstructor
public class TicketEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TICKET_ID")
    private Long ticketId;

    @Column(name = "BOOKED_AT")
    private LocalDateTime bookedAt;

    @Column(name = "PRICE")
    private int price;

    @Column(name = "PAYMENT_STATUS")
    private String paymentStatus; // 예: PAID, UNPAID

    @Column(name = "ISSUED")
    private boolean issued;

    @ManyToOne
    @JoinColumn(name = "MOVIE_ID")
    private MovieEntity movie;

    @ManyToOne
    @JoinColumn(name = "THEATER_ID")
    private TheaterEntity theater;
}