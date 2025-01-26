package com.concurrency_control.reservation.service;

import com.concurrency_control.reservation.enums.Status;
import com.concurrency_control.reservation.model.Reservation;
import com.concurrency_control.reservation.model.Restaurant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class ReservationServiceTest {

    @Autowired
    private RestaurantService restaurantService;
    private Restaurant restaurant = new Restaurant();
    @Autowired
    private ReservationService reservationService;

    @BeforeEach
    void setUp() {
        restaurant.setName("A음식점");
        restaurant.setTotalSlots(2);
        restaurantService.save(restaurant);

    }

    @Test
    @DisplayName("A음식점에 동시에 10개의 요청이 들어왔다면")
    public void reservationServiceTest() throws InterruptedException {
                int numberOfThreads = 10;
                CountDownLatch latch = new CountDownLatch(numberOfThreads);
                ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
                AtomicInteger successCount = new AtomicInteger(0);
                AtomicInteger failureCount = new AtomicInteger(0);

                List<Runnable> tasks = new ArrayList<>();
                for (int i = 0; i < numberOfThreads; i++) {
                    int index = i;
                    tasks.add(() -> {
                        try {
                            Reservation reservation = new Reservation();
                            reservation.setRestaurant(restaurant);
                            reservation.setMemberName("김민혁"+ index);
                            reservation.setStatus(Status.PENDING);
                            reservationService.saveReservation(reservation);
                            successCount.incrementAndGet();
                        } catch (IllegalStateException e) {
                            failureCount.incrementAndGet();
                        } finally {
                            latch.countDown();
                        }
                    });
                }

                // 모든 스레드 실행
                tasks.forEach(executorService::execute);
                latch.await(); // 모든 스레드가 완료될 때까지 대기

                // 성공 횟수 확인 (2개의 예약만 성공해야 함)
                assertEquals(2, successCount.get());
                // 실패 횟수 확인 (8개의 예약은 실패해야 함)
                assertEquals(8, failureCount.get());
    }
}
