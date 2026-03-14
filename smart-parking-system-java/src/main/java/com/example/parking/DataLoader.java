package com.example.parking;

import com.example.parking.model.ParkingSlot;
import com.example.parking.model.SlotSize;
import com.example.parking.repository.ParkingSlotRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements CommandLineRunner {
    private final ParkingSlotRepository slotRepo;
    public DataLoader(ParkingSlotRepository slotRepo) { this.slotRepo = slotRepo; }

    @Override
    public void run(String... args) throws Exception {
        // create some demo slots if empty
        if (slotRepo.count() == 0) {
            slotRepo.save(new ParkingSlot(1, "A1", SlotSize.CAR));
            slotRepo.save(new ParkingSlot(1, "A2", SlotSize.MOTORCYCLE));
            slotRepo.save(new ParkingSlot(2, "B1", SlotSize.BUS));
        }
    }
}
