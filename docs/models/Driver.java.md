# Driver Model – `models/Driver.java`

## 1. Overview
`Driver.java` defines the **Driver** entity used throughout the ride‑hailing / logistics subsystem.  
It represents a real‑world driver with a unique identifier, a name, a current geographic location, and an availability flag.  
The class is a plain POJO (Plain Old Java Object) that is instantiated by the service layer when a new driver is registered or when driver data is fetched from a persistence store.  

> **Why it matters**  
> The driver model is the cornerstone for:
> * Matching riders to nearby drivers
> * Updating driver status during a trip
> * Calculating distances and ETA
> * Persisting driver state in a database or cache

---

## 2. Detailed Breakdown

| Section | Description |
|---------|-------------|
| **Package** | `package models;` – part of the domain layer, typically shared across services. |
| **Fields** | ```java
public int id;                // Unique driver identifier (e.g., DB primary key)
public String name;           // Human‑readable driver name
public Location location;     // Current GPS coordinates (custom Location class)
public boolean isAvailable;   // Availability flag for ride assignment
``` |
| **Constructor** | ```java
public Driver(int id, String name, Location location) {
    this.id = id;
    this.name = name;
    this.location = location;
    this.isAvailable = true; // New drivers start as available
}
``` |
| **Getters / Setters** | * `getLocation()` / `setLocation(Location)` – read/write the current position.  
* `checkAvailable()` / `setAvailable(boolean)` – query or update availability.  
* `getCurrentLocation()` – duplicate of `getLocation()` (could be legacy or for API consistency).  
* `getName()` – returns the driver's name. |
| **Business Logic** | Minimal; the class mainly holds state. All complex logic (e.g., distance calculation, assignment rules) is handled elsewhere. |
| **Design Notes** | * Public fields are used instead of private + getters/setters. This is a quick‑and‑dirty approach; in a production system you’d typically make them private and expose only necessary methods.  
* The `Location` type is assumed to encapsulate latitude/longitude and possibly other metadata. |

---

## 3. Integrations

| Component | Interaction |
|-----------|-------------|
| **DriverService** | Creates, updates, and queries `Driver` instances. It may call `setLocation()` when a driver reports a new GPS coordinate and `setAvailable()` when a trip starts or ends. |
| **RideMatcher** | Reads `driver.getCurrentLocation()` and `driver.checkAvailable()` to find the nearest free driver for a rider request. |
| **Database Layer (e.g., JPA/Hibernate)** | Persists `Driver` objects. The fields map directly to a `drivers` table (`id`, `name`, `location_lat`, `location_lon`, `is_available`). |
| **Web/API Controllers** | Expose driver data via REST or GraphQL endpoints. For example, `GET /drivers/{id}` returns a JSON representation of this class. |
| **Real‑time Tracking** | A WebSocket or MQTT publisher may broadcast `driver.getLocation()` changes to a fleet dashboard. |
| **Unit Tests** | `DriverTest` verifies that state changes propagate correctly (e.g., `setAvailable(false)` after a trip). |

---

### Example Usage

```java
// Register a new driver
Location loc = new Location(37.7749, -122.4194);
Driver driver = new Driver(101, "Alice", loc);

// Update location
driver.setLocation(new Location(37.7750, -122.4180));

// Mark as busy
driver.setAvailable(false);

// Check status
if (driver.checkAvailable()) {
    // assign ride
}
```

---

## 4. Recommendations

1. **Encapsulation** – Switch fields to `private` and expose only necessary getters/setters to enforce invariants.  
2. **Validation** – Add checks in setters (e.g., non‑null `name`, valid coordinates).  
3. **Equality / Hashing** – Override `equals()` and `hashCode()` based on `id` for collection usage.  
4. **DTO Layer** – Consider a separate `DriverDTO` for API responses to avoid leaking internal state.  

---

**Author:** AI Documentation Product  
**Last Updated:** 2025‑12‑29

---