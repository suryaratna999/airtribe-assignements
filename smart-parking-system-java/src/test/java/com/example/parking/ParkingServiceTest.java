package com.example.parking;

import com.example.parking.model.*;
import com.example.parking.service.ParkingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class ParkingServiceTest {
    private ParkingService svc;

    @BeforeEach
    public void setup(){
        svc = new ParkingService(Map.of(SlotSize.MOTORCYCLE, 100, SlotSize.CAR, 200, SlotSize.BUS, 500));
        svc.addSlot(1,"A1", SlotSize.CAR);
        svc.addSlot(1,"A2", SlotSize.MOTORCYCLE);
        svc.addSlot(1,"B1", SlotSize.BUS);
    }

    @Test
    public void testCheckInAndOut() {
        Vehicle v = new Vehicle("ABC-123", SlotSize.CAR);
        var sess = svc.checkIn(v);
        assertNotNull(sess);
        assertFalse(sess.getSlot().isAvailable());
        // check out
        var out = svc.checkOut(sess.getId());
        assertTrue(out.getSlot().isAvailable());
        assertTrue(out.getFeeCents() >= 200);
    }

    @Test
    public void testNoSlot() {
        svc.checkIn(new Vehicle("C1", SlotSize.CAR));
        svc.checkIn(new Vehicle("C2", SlotSize.CAR));
        assertThrows(RuntimeException.class, () -> svc.checkIn(new Vehicle("BUS1", SlotSize.BUS)));
    }

    @Test
    public void testAvailabilityCounts(){
        Map<String, Long> counts = svc.availability();
        assertEquals(1L, counts.get("car"));
        assertEquals(1L, counts.get("motorcycle"));
        assertEquals(1L, counts.get("bus"));
    }
}
