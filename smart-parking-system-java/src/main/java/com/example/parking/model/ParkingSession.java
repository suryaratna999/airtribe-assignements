package com.example.parking.model;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "parking_session")
public class ParkingSession {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Vehicle vehicle;

    @ManyToOne
    private ParkingSlot slot;

    private Instant entryTime;
    private Instant exitTime;
    private Long feeCents;
    private String status = "active";

    public ParkingSession() {}

    public ParkingSession(Vehicle vehicle, ParkingSlot slot, Instant entryTime) {
        this.vehicle = vehicle;
        this.slot = slot;
        this.entryTime = entryTime;
    }

    public Long getId() { return id; }
    public Vehicle getVehicle() { return vehicle; }
    public ParkingSlot getSlot() { return slot; }
    public Instant getEntryTime() { return entryTime; }
    public Instant getExitTime() { return exitTime; }
    public void setExitTime(Instant exitTime) { this.exitTime = exitTime; }
    public Long getFeeCents() { return feeCents; }
    public void setFeeCents(Long feeCents) { this.feeCents = feeCents; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
