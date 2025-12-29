# Driver Model

> **File**: `models/Driver.class`  
> **Package**: `models`  
> **Source**: `Driver.java` (compiled to the byte‑code shown above)

---

## 1. Overview

`Driver` is a plain‑old Java object (POJO) that represents a driver entity in the system.  
It is part of the **domain layer** and is used by services that manage driver state, availability, and location.  
Typical use‑cases include:

- Registering a new driver
- Updating a driver’s current location
- Toggling a driver’s availability for ride requests
- Querying driver details for UI or API responses

The class is intentionally lightweight – it contains only data fields and simple accessor methods, making it ideal for persistence frameworks (JPA/Hibernate, MyBatis, etc.) or serialization to JSON/XML.

---

## 2. Detailed Breakdown

| Member | Type | Description |
|--------|------|-------------|
| `id` | `int` | Unique identifier for the driver. |
| `name` | `String` | Human‑readable name of the driver. |
| `location` | `Location` | Current geographical location (see `models.Location`). |
| `isAvailable` | `boolean` | Flag indicating whether the driver is ready to accept rides. |

### Constructor

```java
public Driver(int id, String name, Location location)
```

Initialises a driver with the supplied `id`, `name`, and `location`.  
The availability flag defaults to `false` unless explicitly set later.

### Accessors & Mutators

| Method | Signature | Purpose |
|--------|-----------|---------|
| `getLocation()` | `Location` | Returns the current `Location`. |
| `setLocation(Location location)` | `void` | Updates the driver’s location. |
| `checkAvailable()` | `boolean` | Returns the current value of `isAvailable`. |
| `setAvailable(boolean available)` | `void` | Sets the driver’s availability status. |
| `getName()` | `String` | Returns the driver’s name. |

> **Note**: The byte‑code also references a method named `getCurrentLocation`, but the source snippet does not contain it. If present, it would likely be an alias for `getLocation()` or a convenience method that returns a formatted string.

### Implementation Highlights

* The class is **serializable** only if the `Location` type implements `Serializable`.  
* No business logic beyond state management is present – all domain rules (e.g., “a driver cannot be available if the location is null”) are enforced elsewhere.  
* The class is compiled with Java 8+ byte‑code (`LineNumberTable` indicates the original source lines).

---

## 3. Integrations

| Component | Interaction | Purpose |
|-----------|-------------|---------|
| `models.Location` | Composition | Holds latitude/longitude or address details. |
| `DriverService` | CRUD & state updates | Persists `Driver` objects, updates availability, and tracks location changes. |
| `RideRequest` / `Trip` | Assignment | A driver is matched to a ride request based on `isAvailable` and proximity (`location`). |
| REST API (`/drivers`) | DTO mapping | Exposes driver data to front‑end clients; often serialized to JSON. |
| Database (JPA/Hibernate) | Entity mapping | `Driver` may be annotated with `@Entity` in the source (not shown in byte‑code). |
| Scheduler / Background Jobs | Availability checks | Periodically sets `isAvailable` based on driver activity or timeouts. |

> **Typical Flow**  
> 1. A new driver registers → `DriverService.createDriver(...)` → persists the instance.  
> 2. The driver’s mobile app reports location → `DriverService.updateLocation(driverId, newLocation)` → updates `location`.  
> 3. When a ride request arrives, the matching algorithm queries `DriverService.getAvailableDrivers()` → filters by `isAvailable` and distance to the request’s pickup point.  
> 4. After a trip, `DriverService.setAvailable(driverId, true)` is called to make the driver ready for the next request.

---

## 4. Usage Example (Java)

```java
Location loc = new Location(37.7749, -122.4194); // San Francisco
Driver driver = new Driver(101, "Alice", loc);

driver.setAvailable(true);          // Driver is now ready
Location newLoc = new Location(37.7750, -122.4180);
driver.setLocation(newLoc);

boolean ready = driver.checkAvailable(); // true
```

---

## 5. Summary

The `Driver` class is a foundational domain model that encapsulates a driver’s identity, current location, and availability status. Its simplicity allows it to be seamlessly integrated across persistence layers, service APIs, and business logic components that orchestrate ride‑hailing or logistics operations.