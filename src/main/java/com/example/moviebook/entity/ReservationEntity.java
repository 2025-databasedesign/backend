package com.example.moviebook.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "RESERVATION")
@Getter
@Setter
@NoArgsConstructor
public class ReservationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "RESERVATION_ID")
    private Long reservationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID")
    private UserEntity user;

    @OneToOne
    @JoinColumn(name = "TICKET_ID")
    private TicketEntity ticket;

    @Column(name = "RESERVED_AT")
    private LocalDateTime reservedAt;

    @Column(name = "TOTAL_PRICE")
    private int totalPrice;

    @ManyToOne
    @JoinColumn(name = "SCHEDULE_ID")
    private ScheduleEntity schedule;

    @Column(name = "PAYMENT_METHOD")
    private String paymentMethod; // 예: CREDIT_CARD, KAKAO_PAY
}
