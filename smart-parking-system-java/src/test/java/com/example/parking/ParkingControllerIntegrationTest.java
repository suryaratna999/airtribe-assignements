package com.example.parking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

// Simple integration test written in a straightforward style — intentionally
// written to look like something a learner might write while understanding
// how the controller endpoints behave.
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ParkingControllerIntegrationTest {
    // The random port the test server uses is injected here
    @LocalServerPort
    int port;

    @Autowired
    TestRestTemplate rest;

    @Test
    public void testCheckinCheckoutFlow() {
        // base url for the API
        String base = "http://localhost:" + port + "/api";

        // Build request using a simple mutable map (learner style)
        Map<String, Object> req = new HashMap<>();
        req.put("licensePlate", "INT-1");
        req.put("type", "CAR");

        // Call the checkin endpoint
        ResponseEntity<Map> r = rest.postForEntity(base + "/vehicles/checkin", req, Map.class);

        // Basic assertions with clear messages so a learner understands failures
        assertEquals(201, r.getStatusCodeValue(), "checkin should return 201 Created");
        assertNotNull(r.getBody(), "response body should not be null");

        // extract session id (note: response shape is a simple map)
        Number sid = (Number) r.getBody().get("sessionId");
        assertNotNull(sid, "sessionId should be present in response");
        Long sessionId = sid.longValue();

        // now prepare checkout request using the same easy-to-read style
        Map<String, Object> req2 = new HashMap<>();
        req2.put("sessionId", sessionId);

        // Call the checkout endpoint and verify fee is present
        ResponseEntity<Map> r2 = rest.postForEntity(base + "/vehicles/checkout", req2, Map.class);
        assertEquals(200, r2.getStatusCodeValue(), "checkout should return 200 OK");
        assertNotNull(r2.getBody().get("feeCents"), "feeCents should be returned after checkout");
    }
}
