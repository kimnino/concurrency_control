package com.concurrency_control.reservation.service;

import com.concurrency_control.reservation.model.Reservation;
import com.concurrency_control.reservation.model.Restaurant;
import com.concurrency_control.reservation.repository.ReservationRepository;
import com.concurrency_control.reservation.repository.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
@RequiredArgsConstructor
@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final RestaurantRepository restaurantRepository;
    private final ConcurrentHashMap<Long, ReentrantLock> locks = new ConcurrentHashMap<>();

    @Transactional
    public void saveReservation(Reservation reservation) {

        ReentrantLock lock = locks.computeIfAbsent(reservation.getRestaurant().getId(), k -> new ReentrantLock());

        lock.lock();
        try {
            Restaurant restaurant = restaurantRepository.getReferenceById(reservation.getRestaurant().getId());
            List<Reservation> reservations = reservationRepository.findAllByRestaurantIdAndStatus(reservation.getRestaurant().getId(), reservation.getStatus());

            if(restaurant.getTotalSlots() <= reservations.size()) {
                log.info("해당 음식점에 예약요청을 할 수 없습니다.");
                throw new IllegalStateException("해당 음식점에 예약요청을 할 수 없습니다.");
            }

            reservationRepository.save(reservation);
        } finally {
            lock.unlock();
        }
    }
}
