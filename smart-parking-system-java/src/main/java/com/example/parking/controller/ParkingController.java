package com.example.parking.controller;

import com.example.parking.model.ParkingSession;
import com.example.parking.model.SlotSize;
import com.example.parking.service.IParkingService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class ParkingController {
    private final IParkingService service;

    public ParkingController(IParkingService service) {
        this.service = service;
    }

    public static class CheckinRequest {
        @NotBlank public String licensePlate;
        @NotNull public SlotSize type;
    }

    public static class CheckinResponse {
        public Long sessionId;
        public Long slotId;
        public String entryTime;

        public CheckinResponse(ParkingSession p){
            this.sessionId = p.getId();
            this.slotId = p.getSlot().getId();
            this.entryTime = p.getEntryTime().toString();
        }
    }

    public static class CheckoutRequest { @NotNull public Long sessionId; }

    public static class CheckoutResponse {
        public Long sessionId;
        public Long feeCents;
        public String exitTime;
        public CheckoutResponse(ParkingSession p){ this.sessionId = p.getId(); this.feeCents = p.getFeeCents(); this.exitTime = p.getExitTime().toString(); }
    }

    @PostMapping("/vehicles/checkin")
    public ResponseEntity<CheckinResponse> checkin(@Valid @RequestBody CheckinRequest req){
        ParkingSession ps = service.checkIn(req.licensePlate, req.type);
        return ResponseEntity.status(201).body(new CheckinResponse(ps));
    }

    @PostMapping("/vehicles/checkout")
    public ResponseEntity<CheckoutResponse> checkout(@Valid @RequestBody CheckoutRequest req){
        ParkingSession ps = service.checkOut(req.sessionId);
        return ResponseEntity.ok(new CheckoutResponse(ps));
    }

    @GetMapping("/availability")
    public ResponseEntity<Map<String, Long>> availability(){
        return ResponseEntity.ok(service.availability());
    }
}
