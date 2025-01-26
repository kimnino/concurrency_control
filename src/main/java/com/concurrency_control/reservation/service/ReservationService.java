package com.concurrency_control.reservation.service;

import com.concurrency_control.lock.LockService;
import com.concurrency_control.reservation.enums.Status;
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
    private final LockService lockService;

    @Transactional
    public void saveReservation(Reservation reservation) {

        ReentrantLock lock = lockService.acquireLock("Restaurant_" + reservation.getRestaurant().getId());

        try {
            Restaurant restaurant = restaurantRepository.getReferenceById(reservation.getRestaurant().getId());

            // PENDING 상태의 예약정보를 가져온다.
            List<Reservation> reservations = reservationRepository.findAllByRestaurantIdAndStatus(reservation.getRestaurant().getId(), Status.PENDING);

            if(restaurant.getTotalSlots() <= reservations.size()) { // 음식점 마다 예약접수를 최대로 받을 수 있는 카운트를 검사
                log.info(reservation.getMemberName()+"님은 해당 음식점에 예약요청을 할 수 없습니다.");
                throw new IllegalStateException("해당 음식점에 예약요청을 할 수 없습니다.");
            }

            log.info(reservation.getMemberName()+"님은 예약 요청을 성공하셨습니다.");
            reservationRepository.save(reservation);
        } finally {
            lockService.releaseLock(lock);
        }
    }
}
