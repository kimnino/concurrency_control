package com.concurrency_control.reservation.repository;

import com.concurrency_control.reservation.enums.Status;
import com.concurrency_control.reservation.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    List<Reservation> findAllByRestaurantIdAndStatus(Long restaurantId, Status status);
}