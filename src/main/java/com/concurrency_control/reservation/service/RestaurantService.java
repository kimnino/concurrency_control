package com.concurrency_control.reservation.service;

import com.concurrency_control.reservation.model.Restaurant;
import com.concurrency_control.reservation.repository.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class RestaurantService {

    private final RestaurantRepository restaurantRepository;

    @Transactional
    public void save(Restaurant restaurant) {
        restaurantRepository.save(restaurant);
    }
}
