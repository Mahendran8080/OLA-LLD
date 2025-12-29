# RiderService – Service Layer for Rider Operations

> **File:** `services/RiderService.class`  
> **Compiled from:** `services/RiderService.java` (Java 8+)

---

## 1. Overview

`RiderService` is the core business component that handles all rider‑centric ride‑management logic in the application.  
It sits between the presentation layer (e.g., REST controllers) and the domain model, delegating driver‑selection to `DriverService` while maintaining a local map of active rides.

Key responsibilities:

| Responsibility | What it does |
|-----------------|--------------|
| **Ride request** | Finds the nearest available driver, creates a `Ride`, marks the driver as busy, and stores the ride in `activeRides`. |
| **Ride completion** | Ends an ongoing ride, frees the driver, and removes the ride from the active map. |
| **Ride lookup** | Retrieves the current ride for a given rider. |

The class is intentionally lightweight – it does not persist data, but relies on in‑memory structures (`HashMap`) for demo or unit‑test scenarios.

---

## 2. Detailed Breakdown

### 2.1 Class Structure

```java
package services;

public class RiderService {
    // Dependencies
    private DriverService driverService;

    // State
    private Map<Rider, Ride> activeRides = new HashMap<>();

    // Public API
    public Ride requestRide(Rider rider, Location start, Location destination);
    public void endRide(Rider rider);
    public Ride getRideForRider(Rider rider);

    // Internal helpers
    private Driver findNearestAvailableDriver(Location start);
}
```

> **Note:** The class is compiled to a `.class` file; the source is inferred from the constant pool and bytecode.

### 2.2 Fields

| Field | Type | Purpose |
|-------|------|---------|
| `driverService` | `DriverService` | Delegates driver lookup and status updates. |
| `activeRides` | `Map<Rider, Ride>` | Tracks ongoing rides per rider. |

### 2.3 Constructor

A default no‑arg constructor is generated. The `driverService` is expected to be injected (e.g., via a DI framework or manual setter).

### 2.4 Core Methods

#### `requestRide(Rider rider, Location start, Location destination) : Ride`

1. **Find a driver**  
   Calls `findNearestAvailableDriver(start)` to locate the closest driver that is currently available.

2. **Handle no‑driver case**  
   If no driver is found, prints `"No available driver at the moment."` and returns `null`.

3. **Create a ride**  
   ```java
   Ride ride = new Ride(rider, driver, start, destination);
   ```

4. **Update state**  
   * Marks the driver as unavailable: `driver.setAvailable(false);`  
   * Stores the ride: `activeRides.put(rider, ride);`

5. **Logging**  
   Prints `"Ride booked successfully with driver: " + driver.getName()`.

6. **Return** the newly created `Ride`.

#### `endRide(Rider rider) : void`

1. **Retrieve the ride**  
   `Ride ride = activeRides.get(rider);`

2. **Handle missing ride**  
   If `ride` is `null`, prints `"No active ride for this rider."`.

3. **Finish the ride**  
   * Marks the driver as available again: `ride.getDriver().setAvailable(true);`  
   * Removes the mapping: `activeRides.remove(rider);`

4. **Logging**  
   Prints `"Ride ended for rider: " + rider.getName()`.

#### `getRideForRider(Rider rider) : Ride`

Simply returns `activeRides.get(rider)`. Useful for status queries.

#### `findNearestAvailableDriver(Location start) : Driver` *(private)*

1. **Iterate over available drivers**  
   ```java
   for (Driver d : driverService.getAvailableDrivers()) {
       double dist = d.getCurrentLocation().distanceTo(start);
       // keep the driver with the smallest distance
   }
   ```

2. **Return** the driver with the minimal distance, or `null` if none are available.

### 2.5 Dependencies & Interactions

| Dependency | Role |
|------------|------|
| `DriverService` | Provides the list of currently available drivers. |
| `models.Driver` | Holds driver data (name, location, availability). |
| `models.Location` | Provides `distanceTo(Location)` for distance calculation. |
| `models.Ride` | Represents an active ride; contains rider, driver, start & destination. |
| `models.Rider` | Represents the user requesting a ride. |
| `java.util.*` | `List`, `Iterator`, `Map`, `HashMap` for collections. |

---

## 3. Integrations

| Layer | Interaction |
|-------|-------------|
| **Controller / API** | A REST endpoint would call `requestRide` or `endRide` and return the resulting `Ride` or status. |
| **DriverService** | `RiderService` relies on `driverService.getAvailableDrivers()` to fetch drivers. It also updates driver availability via `driver.setAvailable(...)`. |
| **Persistence** | Currently in‑memory; for production, a repository layer would replace the `HashMap` and persist `Ride` objects. |
| **Logging** | Uses `System.out.println` for simplicity; replace with a proper logging framework (e.g., SLF4J) in real deployments. |
| **Concurrency** | The current implementation is **not thread‑safe**. In a multi‑threaded environment, `activeRides` should be a `ConcurrentHashMap` and driver availability updates should be atomic. |

---

## 4. Usage Example

```java
DriverService driverService = new DriverService(); // pre‑populated with drivers
RiderService riderService = new RiderService();
riderService.setDriverService(driverService);

Rider rider = new Rider("Alice");
Location start = new Location(40.7128, -74.0060); // New York
Location dest  = new Location(40.730610, -73.935242); // Brooklyn

Ride ride = riderService.requestRide(rider, start, dest);
if (ride != null) {
    // ... rider is on the way
    riderService.endRide(rider);
}
```

---

## 5. Potential Improvements

| Area | Recommendation |
|------|-----------------|
| **Thread safety** | Replace `HashMap` with `ConcurrentHashMap`; synchronize driver status changes. |
| **Logging** | Use a logging framework instead of `System.out`. |
| **Error handling** | Throw custom exceptions (`NoDriverAvailableException`, `NoActiveRideException`) instead of printing messages. |
| **Persistence** | Persist rides and driver status to a database or external store. |
| **Scalability** | Offload driver lookup to a spatial index (e.g., R‑tree) for large driver pools. |
| **Testing** | Add unit tests for each method, mocking `DriverService`. |

---

## 6. Summary

`RiderService` encapsulates the core logic for booking, tracking, and completing rides. It leverages `DriverService` to find drivers, uses in‑memory maps for active ride tracking, and exposes a clean API for higher layers. While functional, the class is a good candidate for refactoring toward a more robust, concurrent, and testable design.