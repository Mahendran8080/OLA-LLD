# Main.java

> **Location**: `src/main/java/Main.java`  
> **Purpose**: Entry point of the application – orchestrates driver/rider creation, ride requests, and ride lifecycle management.

---

## 1. Overview

`Main.java` is the **application bootstrap**.  
It demonstrates a minimal end‑to‑end flow of the ride‑hailing system:

1. **Instantiate domain objects** (`Driver`, `Rider`, `Location`).
2. **Create services** (`DriverService`, `RiderService`) that encapsulate business logic.
3. **Simulate a ride request** and its completion.
4. **Print results** to the console for quick verification.

This file is not part of the production API; it serves as a **sample usage / test harness** for developers and QA engineers.

---

## 2. Detailed Breakdown

| Section | Purpose | Key Points |
|---------|---------|------------|
| **Imports** | Pulls in domain models and service classes. | `import models.*; import services.*;` – assumes packages `models` and `services`. |
| **Driver Creation** | Sets up a small pool of drivers with unique IDs, names, and starting locations. | `Driver d1 = new Driver(1, "Driver A", new Location(2, 3));` etc. |
| **Driver List** | Aggregates drivers into a `List<Driver>` for the `DriverService`. | `List<Driver> driverList = new ArrayList<>();` |
| **Service Instantiation** | `DriverService` receives the driver list; `RiderService` depends on `DriverService`. | `DriverService driverService = new DriverService(driverList);` |
| **Rider Creation** | A single rider instance used for the demo. | `Rider r1 = new Rider(100, "Rider One");` |
| **Ride Request** | Calls `rideService.requestRide()` with source/destination coordinates. | `Ride ride = rideService.requestRide(r1, source, destination);` |
| **Output** | Prints ride start and destination coordinates if a ride is successfully created. | `System.out.println("Ride started from: (" + source.getX() + ", " + source.getY() + ")");` |
| **Ride End** | Simulates finishing the ride. | `rideService.endRide(r1);` |
| **Post‑End Check** | Verifies that the rider no longer has an active ride. | `Ride rideInfo = rideService.getRideForRider(r1);` |
| **Commented Code** | `// System.out.println(rideService.activeRides);` – left for debugging. |

### Parameters & Configuration

- **Driver IDs**: `1`, `2`, `3` – unique identifiers.
- **Rider ID**: `100`.
- **Locations**: `Location` objects use integer `x` and `y` coordinates.
- **Services**: No external configuration files; everything is hard‑coded for demonstration.

---

## 3. Integrations

| Component | Interaction | Notes |
|-----------|-------------|-------|
| **`models` package** | `Driver`, `Rider`, `Location`, `Ride` | Domain objects used throughout the flow. |
| **`services` package** | `DriverService`, `RiderService` | Business logic for driver management and ride lifecycle. |
| **`DriverService`** | Provides driver lookup/assignment to `RiderService`. | Likely contains methods like `findNearestDriver(Location)` or `updateDriverLocation`. |
| **`RiderService`** | Handles ride requests, ends, and queries. | Exposes `requestRide`, `endRide`, `getRideForRider`. |
| **Console** | Output statements | Simple logging for demonstration; replace with proper logging in production. |

> **Dependency Flow**  
> `Main` → `DriverService` (initial driver list) → `RiderService` (uses `DriverService`) → `Ride` (created/ended) → `Driver`/`Rider` state changes.

---

## 4. Usage Tips

- **Testing**: Run `Main` to see a quick simulation. Replace hard‑coded values with dynamic input for more extensive tests.
- **Extending**: Add more drivers or riders, or integrate with a UI/CLI by wiring `Main` into a framework (Spring Boot, etc.).
- **Logging**: Swap `System.out.println` with a logging framework (SLF4J, Log4j) for production readiness.

---

## 5. Summary

`Main.java` is a **demo harness** that stitches together the core domain models and services to showcase a simple ride‑hailing workflow. It is ideal for developers to understand the interaction between `DriverService` and `RiderService`, and to verify that the system behaves as expected before integrating with other layers (API, persistence, UI).