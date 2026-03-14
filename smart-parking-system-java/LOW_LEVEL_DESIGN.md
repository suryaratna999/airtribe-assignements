Smart Parking System — Low-Level Design (LLD)

Overview
--------
This document captures a practical, implementation-focused design for a Smart Parking System. It is written as a working engineering note: it explains the data model, core classes, allocation and billing logic, concurrency considerations, and recommended next steps you can act on.

Quick checklist (what this doc contains)
- [x] Purpose, scope and assumptions
- [x] Data model and DDL notes you can copy into your database
- [x] Class responsibilities and method signatures for a Java implementation
- [x] Detailed allocation algorithm with a safe DB-backed pattern
- [x] Fee calculation with examples and edge-case handling
- [x] Concurrency, scaling and recovery strategies
- [x] Testing plan and deploy/run steps

Context and goals
-----------------
We are building a backend that operates a multi-floor parking lot. The system should:
- Automatically pick a suitable parking spot for each incoming vehicle.
- Record entry/exit timestamps and calculate parking fees on exit.
- Keep availability counts up to date in real time.
- Handle concurrent entry/exit operations without double-booking slots.

Out of scope: detailed UI, payment gateway integration, or advanced access control. This is focused on the backend behavior and data model.

Rationale and key decisions
---------------------------
- Slot assignment should prefer the smallest compatible slot to avoid wasting large slots.
- For production we strongly recommend a relational DB (Postgres) so allocations can use row-level locking (SELECT ... FOR UPDATE). That pattern is robust and easy to reason about.
- The reference Java implementation uses in-memory maps + per-slot synchronization so the logic is visible and testable. The LLD below shows how to move this to a DB-backed implementation.

Data model (summary and copyable DDL)
------------------------------------
The schema below is intentionally simple and easy to adapt. It covers slots, vehicles, sessions and pricing rules.

-- Postgres-friendly DDL (copy into a migration file)

CREATE TYPE slot_size AS ENUM ('motorcycle','car','bus');

CREATE TABLE parking_lot (
  id BIGSERIAL PRIMARY KEY,
  name TEXT NOT NULL
);

CREATE TABLE floor (
  id BIGSERIAL PRIMARY KEY,
  parking_lot_id BIGINT REFERENCES parking_lot(id),
  level INTEGER NOT NULL
);

CREATE TABLE parking_slot (
  id BIGSERIAL PRIMARY KEY,
  floor_id BIGINT REFERENCES floor(id),
  slot_number TEXT NOT NULL,
  size slot_size NOT NULL,
  is_covered BOOLEAN DEFAULT FALSE,
  is_available BOOLEAN DEFAULT TRUE
);

CREATE TABLE vehicle (
  id BIGSERIAL PRIMARY KEY,
  license_plate TEXT UNIQUE NOT NULL,
  vehicle_type TEXT NOT NULL
);

CREATE TABLE parking_session (
  id BIGSERIAL PRIMARY KEY,
  vehicle_id BIGINT REFERENCES vehicle(id),
  slot_id BIGINT REFERENCES parking_slot(id),
  entry_time TIMESTAMP WITH TIME ZONE NOT NULL,
  exit_time TIMESTAMP WITH TIME ZONE,
  fee_cents BIGINT,
  status TEXT NOT NULL DEFAULT 'active'
);

CREATE TABLE pricing_rule (
  id BIGSERIAL PRIMARY KEY,
  vehicle_type TEXT NOT NULL,
  base_rate_cents_per_hour INTEGER NOT NULL,
  grace_period_minutes INTEGER DEFAULT 0
);

Notes on schema
- `is_available` is a quick flag for queries; in production you may prefer a small `version` column and optimistic updates.
- Keep pricing rules separate so rates can be changed without touching code.

Key classes and responsibilities (Java)
--------------------------------------
These map to files already in the reference project.

- `ParkingSlot` (model)
  - Fields: id, floor, slotNumber, size (enum), isAvailable
  - Mutators: setAvailable(boolean)

- `Vehicle` (model)
  - Fields: licensePlate, type (SlotSize enum)

- `ParkingSession` (model)
  - Fields: id, vehicle, slot, entryTime, exitTime, feeCents, status

- `ParkingService` (core service)
  - addSlot(int floor, String slotNumber, SlotSize size)
  - Optional<ParkingSlot> allocateSlot(Vehicle vehicle)
  - ParkingSession checkIn(Vehicle vehicle)
  - ParkingSession checkOut(long sessionId)
  - Map<String, Long> availability()

Behavioral examples
-------------------
- Vehicle `CAR` arrives:
  - Compatible sizes: `CAR`, `BUS` (in that order). We scan for an available `CAR`, then `BUS` if needed.
  - Claim the slot atomically, create a session with `entry_time = now()`.

- Checkout:
  - Set `exit_time = now()` and compute billable hours as `ceil((exit - entry)/3600)`.
  - Fee = `base_rate * billable_hours`. Example: 1h20m -> ceil(80/60)=2 hours billed.

Allocation algorithm (production-ready pattern)
-----------------------------------------------
Goal: safe, consistent slot assignment under concurrency.

1) Determine ordered compatible sizes for the vehicle (e.g. motorcycle -> [motorcycle, car, bus]).
2) Start a DB transaction.
3) For each size in order:
   a) SELECT id FROM parking_slot
      WHERE size = :size AND is_available = true
      ORDER BY floor ASC, slot_number ASC
      LIMIT 1 FOR UPDATE SKIP LOCKED;  -- SKIP LOCKED makes it friendly for concurrency
   b) If a row is returned, UPDATE parking_slot SET is_available = false WHERE id = :id;
   c) Insert parking_session with entry_time and slot_id, commit transaction and return session.
4) If no slot found for all sizes, rollback and return a clear "no availability" response.

Why use `FOR UPDATE SKIP LOCKED`?
- It avoids long waits between competing transactions: locked rows are skipped so other transactions can try other rows. This pattern is standard for work-queue style allocation.

Fee calculation (detailed)
--------------------------
- Compute duration in seconds: `seconds = exit_time - entry_time`.
- Billable hours = `ceil(seconds / 3600)`. Always bill at least 1 hour for any non-zero positive duration.
- Apply grace period: if duration <= grace_period_minutes treat fee as 0.
- Example rates (cents/hour): motorcycle=100, car=200, bus=500.
  - parked 0m->10m and grace_period=15m -> fee 0.
  - parked 1h20m -> hours = ceil(80/60)=2 -> fee = rate * 2.

Concurrency & scaling notes
---------------------------
Options summarized by scale:
- Single process (small parking lot): in-memory data structures with per-slot synchronization (synchronized blocks) are fine.
- Single DB-backed service (recommended): use transactions + SELECT FOR UPDATE SKIP LOCKED for allocation.
- High throughput / multi-node:
  - Use Redis sets per slot-size with atomic pop operations to allocate quickly.
  - Or funnel allocations through a single allocation service (message queue) to serialize decisions.

Recovery and resilience
-----------------------
- A background reconciliation job should find sessions that are `active` for an unusually long time (e.g. > 7 days) and alert/log or mark as abandoned.
- Provide admin endpoints to forcibly free a slot or close a session when manual intervention is required.
- Keep operations idempotent: repeated checkout calls on the same session should return the same result after the first completion.

API surface (suggested)
-----------------------
- POST /api/vehicles/checkin
  - Request: { licensePlate, type }
  - Response: { sessionId, slot: { id, floor, slotNumber }, entryTime }

- POST /api/vehicles/checkout
  - Request: { sessionId }
  - Response: { sessionId, feeCents, exitTime }

- GET /api/availability
  - Response: { car: 12, motorcycle: 4, bus: 1 }

Validation & error handling
---------------------------
- Always validate request payloads. Return 400 for malformed input.
- Return 409 or 503 for no-slot scenarios (choose based on UX: 409 if caller should fix input, 503 if it is transient).
- For allocation DB conflicts, retry a small fixed number of times with short backoff before returning error.

Testing strategy
----------------
- Unit tests: target `ParkingService` behavior (allocation order, fee calculation, slot release).
- Concurrency tests: multi-threaded tests that simulate many concurrent check-ins (detect race conditions).
- Integration tests: run the service with an embedded H2 DB and validate transaction locking works as expected with multiple threads/processes.
- E2E/API tests: validate the full checkin -> checkout flow including database state and billing.

Observability and monitoring
---------------------------
- Emit metrics: `active_sessions`, `allocations/sec`, `allocation_failures`, `avg_allocation_latency`.
- Log allocation decisions and allocation failures with structured JSON fields: sessionId, vehicle, chosenSlotId, reason.
- Add tracing spans to the allocation and billing path.

Operational checklist (runbook)
------------------------------
- Start service(s) and confirm DB migrations applied.
- Verify pricing rules exist and are correct in the DB.
- Smoke test:
  1. POST /api/vehicles/checkin -> expect success and a slot
  2. POST /api/vehicles/checkout -> expect fee and slot released
- If allocation failures increase: check DB locks, connection pool exhaustion, or unexpected slot state.

Trade-offs and alternatives (short)
----------------------------------
- Optimistic updates (UPDATE ... WHERE is_available=true) have simpler SQL but need retries when conflicts spike. SELECT FOR UPDATE SKIP LOCKED has more predictable latency under contention.
- Redis-based allocation is very fast but moves state outside the canonical DB — requires reconciliation logic to handle drift.
