# RiderService – Core Ride‑Management Logic

```java
package services;

import java.util.*;
import models.*;
```

The `RiderService` class is the central component that orchestrates ride requests, driver assignment, and ride lifecycle management for the rider side of the application. It works hand‑in‑hand with the `DriverService` and the domain models (`Rider`, `Driver`, `Ride`, `Location`) to provide a simple, in‑memory ride‑booking workflow.

---

## 1. Overview

| Layer | Responsibility |
|-------|-----------------|
| **Domain** | `Rider`, `Driver`, `Ride`, `Location` – represent the core entities. |
| **Service** | `RiderService` – handles rider‑initiated actions (request, end, query). |
| **Driver Service** | `DriverService` – supplies the pool of drivers and their availability status. |

`RiderService` is the *business logic* that:

1. **Finds the nearest available driver** for a given source location.
2. **Creates a new `Ride`** and marks the driver as busy.
3. **Ends a ride**, freeing the driver and updating the driver’s location.
4. **Keeps an in‑memory map** of active rides keyed by the rider.

It is intentionally lightweight and stateless beyond the `activeRides` map, making it easy to unit‑test and extend.

---

## 2. Detailed Breakdown

### 2.1 Fields

| Field | Type | Purpose |
|-------|------|---------|
| `driverService` | `DriverService` | Dependency that provides driver data and availability. |
| `activeRides` | `Map<Rider, Ride>` | Tracks currently active rides per rider. |

### 2.2 Constructor

```java
public RiderService(DriverService driverService) {
    this.driverService = driverService;
    this.activeRides = new HashMap<>();
}
```

*Injects* the `DriverService` and initializes the active‑ride store.

### 2.3 `findNearestAvailableDriver(Location source)`

*Algorithm*:

1. Retrieve all available drivers via `driverService.getAvailableDrivers()`.
2. Iterate over each driver, compute Euclidean (or custom) distance to the source using `Location.distanceTo()`.
3. Keep track of the driver with the smallest distance.

*Return*: The nearest `Driver` or `null` if none are available.

> **Note**: The method is `private` because it is an internal helper; it could be exposed for testing or replaced with a more sophisticated geospatial query.

### 2.4 `requestRide(Rider rider, Location source, Location destination)`

1. Calls `findNearestAvailableDriver(source)` to pick a driver.
2. If no driver is found, prints a message and returns `null`.
3. Creates a new `Ride` instance.
4. Marks the driver as unavailable (`driver.setAvailable(false)`).
5. Stores the ride in `activeRides` keyed by the rider.
6. Logs success and returns the `Ride`.

> **Side‑effects**: The driver’s availability flag is toggled, but the driver’s location is not updated until the ride ends.

### 2.5 `endRide(Rider rider)`

1. Looks up the active ride for the rider.
2. If none, logs a warning and exits.
3. Marks the ride’s `status` as `true` (completed).
4. Retrieves the driver from the ride, sets them available, and updates their location to the ride’s destination.
5. Removes the ride from `activeRides`.
6. Logs completion.

> **Assumption**: The driver ends the trip at the destination. In a real system, you might want to calculate the actual end location based on traffic or user cancellation.

### 2.6 `getRideForRider(Rider rider)`

A simple getter that returns the active ride or `null` if none exists.

---

## 3. Integrations

| Component | Interaction | Direction |
|-----------|-------------|-----------|
| **DriverService** | `driverService.getAvailableDrivers()` | RiderService → DriverService |
| **Driver** | `driver.setAvailable(boolean)`, `driver.setLocation(Location)` | RiderService ↔ Driver |
| **Ride** | `new Ride(rider, driver, source, destination)`, `ride.status` | RiderService ↔ Ride |
| **Location** | `source.distanceTo(driverLocation)` | RiderService ↔ Location |
| **Rider** | `activeRides.put(rider, ride)`, `activeRides.get(rider)` | RiderService ↔ Rider |

### Typical Flow

1. **Rider** requests a ride → `RiderService.requestRide()`.
2. **RiderService** queries **DriverService** for available drivers.
3. **DriverService** returns a list of **Driver** objects.
4. **RiderService** assigns the nearest driver, creates a **Ride**, and updates driver status.
5. When the ride completes, **RiderService.endRide()** updates the driver’s location and availability.

---

## 4. Usage Example

```java
DriverService driverService = new DriverService();
RiderService riderService = new RiderService(driverService);

Rider alice = new Rider("Alice");
Location pickup = new Location(40.7128, -74.0060);   // New York
Location drop = new Location(40.730610, -73.935242); // Brooklyn

Ride ride = riderService.requestRide(alice, pickup, drop);
// ... after some time
riderService.endRide(alice);
```

---

## 5. Extensibility & Considerations

| Area | Recommendation |
|------|----------------|
| **Distance Calculation** | Replace `Location.distanceTo()` with a geospatial library (e.g., Haversine) for real‑world accuracy. |
| **Concurrency** | `activeRides` is a plain `HashMap`. For multi‑threaded environments, consider `ConcurrentHashMap`. |
| **Persistence** | Persist rides and driver states to a database or message queue for durability. |
| **Error Handling** | Replace `System.out.println` with a proper logging framework and throw domain‑specific exceptions. |
| **Driver Assignment** | Introduce a strategy pattern to support different assignment policies (e.g., cost‑based, rating‑based). |

---

### Summary

`RiderService` encapsulates the core logic for booking, tracking, and completing rides from the rider’s perspective. It leverages the `DriverService` to discover drivers, manipulates driver availability, and maintains an in‑memory mapping of active rides. This design keeps the service simple while providing a clear contract for integration with other system components.