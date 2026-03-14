package com.example.parking.model;

import jakarta.persistence.*;

@Entity
@Table(name = "vehicle")
public class Vehicle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String licensePlate;

    @Enumerated(EnumType.STRING)
    private SlotSize type;

    public Vehicle() {}

    public Vehicle(String licensePlate, SlotSize type) {
        this.licensePlate = licensePlate;
        this.type = type;
    }

    public Long getId() { return id; }
    public String getLicensePlate() { return licensePlate; }
    public SlotSize getType() { return type; }
}
