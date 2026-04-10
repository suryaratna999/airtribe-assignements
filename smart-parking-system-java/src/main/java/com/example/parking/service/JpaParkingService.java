package com.example.parking.service;

import com.example.parking.model.*;
import com.example.parking.repository.ParkingSessionRepository;
import com.example.parking.repository.ParkingSlotRepository;
import com.example.parking.repository.VehicleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class JpaParkingService implements IParkingService {
    private final ParkingSlotRepository slotRepo;
    private final VehicleRepository vehicleRepo;
    private final ParkingSessionRepository sessionRepo;
    private final PricingService pricingService;

    public JpaParkingService(ParkingSlotRepository slotRepo, VehicleRepository vehicleRepo, ParkingSessionRepository sessionRepo, PricingService pricingService) {
        this.slotRepo = slotRepo;
        this.vehicleRepo = vehicleRepo;
        this.sessionRepo = sessionRepo;
        this.pricingService = pricingService;
    }

    @Transactional
    @Override
    public ParkingSession checkIn(String licensePlate, SlotSize type) {
        Vehicle v = vehicleRepo.findByLicensePlate(licensePlate).orElseGet(() -> vehicleRepo.save(new Vehicle(licensePlate, type)));
        // compatible sizes
        List<SlotSize> sizes = compatible(type);
        for (SlotSize s : sizes) {
            // lock candidates
            List<ParkingSlot> candidates = slotRepo.findAvailableForUpdate(s);
            // pick first available and claim
            Optional<ParkingSlot> pick = candidates.stream().filter(ParkingSlot::isAvailable).findFirst();
            if (pick.isPresent()) {
                ParkingSlot slot = pick.get();
                slot.setAvailable(false);
                slotRepo.save(slot);
                ParkingSession ps = new ParkingSession(v, slot, Instant.now());
                return sessionRepo.save(ps);
            }
        }
        throw new RuntimeException("No slot available");
    }

    @Transactional
    @Override
    public ParkingSession checkOut(Long sessionId) {
        ParkingSession ps = sessionRepo.findById(sessionId).orElseThrow(() -> new RuntimeException("session not found"));
        if (ps.getExitTime() != null) throw new RuntimeException("already exited");
        Instant exit = Instant.now();
        ps.setExitTime(exit);
        long seconds = Math.max(0, exit.getEpochSecond() - ps.getEntryTime().getEpochSecond());
        long hours = (seconds + 3600 - 1) / 3600;
        if (hours < 1) hours = 1;
        int rate = pricingService.getRateFor(ps.getVehicle().getType());
        ps.setFeeCents((long) rate * hours);
        ps.getSlot().setAvailable(true);
        // persist slot change
        slotRepo.save(ps.getSlot());
        return sessionRepo.save(ps);
    }

    @Override
    public Map<String, Long> availability() {
        List<ParkingSlot> all = slotRepo.findAll();
        return Arrays.stream(SlotSize.values()).collect(Collectors.toMap(s -> s.name().toLowerCase(), s -> all.stream().filter(x -> x.getSize() == s && x.isAvailable()).count()));
    }

    private List<SlotSize> compatible(SlotSize t){
        switch (t){
            case MOTORCYCLE: return List.of(SlotSize.MOTORCYCLE, SlotSize.CAR, SlotSize.BUS);
            case CAR: return List.of(SlotSize.CAR, SlotSize.BUS);
            case BUS: return List.of(SlotSize.BUS);
            default: return List.of();
        }
    }
}
