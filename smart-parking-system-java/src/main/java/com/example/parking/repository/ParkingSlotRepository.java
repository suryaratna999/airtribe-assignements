package com.example.parking.repository;

import com.example.parking.model.ParkingSlot;
import com.example.parking.model.SlotSize;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;

public interface ParkingSlotRepository extends JpaRepository<ParkingSlot, Long> {
    List<ParkingSlot> findBySizeAndAvailable(SlotSize size, boolean available);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from ParkingSlot p where p.size = :size and p.available = true")
    List<ParkingSlot> findAvailableForUpdate(@Param("size") SlotSize size);
}
