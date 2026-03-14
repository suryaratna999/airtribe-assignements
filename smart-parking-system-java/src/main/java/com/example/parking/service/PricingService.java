package com.example.parking.service;

import com.example.parking.model.SlotSize;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Simple pricing service with single responsibility: return rate for a vehicle type.
 * This can be extended to read from DB or config.
 */
@Service
public class PricingService {
    private final Map<SlotSize,Integer> rates = Map.of(
            SlotSize.MOTORCYCLE, 100,
            SlotSize.CAR, 200,
            SlotSize.BUS, 500
    );

    public int getRateFor(SlotSize size){
        return rates.getOrDefault(size, 0);
    }
}
