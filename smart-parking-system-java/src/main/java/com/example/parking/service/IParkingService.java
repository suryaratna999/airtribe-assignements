package com.example.parking.service;

import com.example.parking.model.ParkingSession;
import com.example.parking.model.SlotSize;

import java.util.Map;

/**
 * Service contract for parking operations.
 * This interface enables dependency inversion so controllers depend on abstractions.
 */
public interface IParkingService {
    ParkingSession checkIn(String licensePlate, SlotSize type);
    ParkingSession checkOut(Long sessionId);
    Map<String, Long> availability();
}
