package com.example.parking.model;

import jakarta.persistence.*;

@Entity
@Table(name = "parking_slot")
public class ParkingSlot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int floor;
    private String slotNumber;

    @Enumerated(EnumType.STRING)
    private SlotSize size;

    private boolean available = true;

    public ParkingSlot() {}

    public ParkingSlot(int floor, String slotNumber, SlotSize size) {
        this.floor = floor;
        this.slotNumber = slotNumber;
        this.size = size;
        this.available = true;
    }

    public Long getId() { return id; }
    public int getFloor() { return floor; }
    public String getSlotNumber() { return slotNumber; }
    public SlotSize getSize() { return size; }
    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }
}
