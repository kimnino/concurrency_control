package com.concurrency_control.reservation.model;

import com.concurrency_control.reservation.enums.Status;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


@Entity
@Getter
@Setter
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id")
    private Restaurant restaurant;

    private String memberName;

    @Enumerated(EnumType.STRING)
    private Status status;
}