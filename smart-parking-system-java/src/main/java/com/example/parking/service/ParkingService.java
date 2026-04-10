package com.example.parking.service;

import com.example.parking.model.*;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class ParkingService {
    private final Map<Long, ParkingSlot> slots = new ConcurrentHashMap<>();
    private final Map<Long, ParkingSession> sessions = new ConcurrentHashMap<>();
    private final AtomicLong slotIdGen = new AtomicLong(1);
    private final AtomicLong sessionIdGen = new AtomicLong(1);
    private final Map<SlotSize, Integer> pricingCentsPerHour;

    public ParkingService(Map<SlotSize, Integer> pricingCentsPerHour) {
        this.pricingCentsPerHour = pricingCentsPerHour;
    }

    public ParkingSlot addSlot(int floor, String slotNumber, SlotSize size) {
        long id = slotIdGen.getAndIncrement();
        ParkingSlot s = new ParkingSlot(floor, slotNumber, size);
        // mimic id assignment for in-memory store
        try {
            java.lang.reflect.Field f = ParkingSlot.class.getDeclaredField("id");
            f.setAccessible(true);
            f.set(s, id);
        } catch (Exception e) {
            // ignore in-memory id set failure
        }
        s.setAvailable(true);
        slots.put(id, s);
        return s;
    }

    public Optional<ParkingSlot> allocateSlot(Vehicle vehicle) {
        // find smallest compatible available slot
        List<SlotSize> compatible;
        switch (vehicle.getType()){
            case MOTORCYCLE: compatible = List.of(SlotSize.MOTORCYCLE, SlotSize.CAR, SlotSize.BUS); break;
            case CAR: compatible = List.of(SlotSize.CAR, SlotSize.BUS); break;
            case BUS: compatible = List.of(SlotSize.BUS); break;
            default: compatible = List.of();
        }
        synchronized (slots) {
            return slots.values().stream()
                    .filter(ParkingSlot::isAvailable)
                    .filter(s -> compatible.contains(s.getSize()))
                    .sorted(Comparator.comparingInt(s -> compatible.indexOf(s.getSize())))
                    .findFirst()
                    .map(s -> { s.setAvailable(false); return s; });
        }
    }

    public ParkingSession checkIn(Vehicle vehicle) {
        ParkingSlot slot = allocateSlot(vehicle).orElseThrow(() -> new RuntimeException("No slot available"));
        long id = sessionIdGen.getAndIncrement();
        ParkingSession sess = new ParkingSession(vehicle, slot, Instant.now());
        try {
            java.lang.reflect.Field f = ParkingSession.class.getDeclaredField("id");
            f.setAccessible(true);
            f.set(sess, id);
        } catch (Exception e) { }
        sessions.put(id, sess);
        return sess;
    }

    public ParkingSession checkOut(long sessionId) {
        ParkingSession sess = sessions.get(sessionId);
        if (sess == null) throw new RuntimeException("Session not found");
        if (sess.getExitTime() != null) throw new RuntimeException("Already exited");
        Instant exit = Instant.now();
        sess.setExitTime(exit);
        Duration dur = Duration.between(sess.getEntryTime(), exit);
        long seconds = Math.max(0, dur.getSeconds());
        long hours = (seconds + 3600 - 1) / 3600;
        if (hours < 1) hours = 1;
        int rate = pricingCentsPerHour.getOrDefault(sess.getVehicle().getType(), 0);
        long fee = rate * hours;
        sess.setFeeCents(fee);
        sess.getSlot().setAvailable(true);
        return sess;
    }

    public Map<String, Long> availability() {
        Map<String, Long> counts = new HashMap<>();
        counts.put("motorcycle", 0L);
        counts.put("car", 0L);
        counts.put("bus", 0L);
        slots.values().forEach(s -> {
            if (s.isAvailable()) counts.put(s.getSize().name().toLowerCase(), counts.get(s.getSize().name().toLowerCase()) + 1);
        });
        return counts;
    }
}
