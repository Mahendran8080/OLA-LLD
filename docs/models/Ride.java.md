# Ride Model – `models/Ride.java`

## 1. Overview  
`Ride` is a **domain entity** that represents a single ride request in the application.  
It encapsulates the core data required to track a ride’s lifecycle: the rider, the driver, the pickup and drop‑off locations, and the current status of the ride.  
In a typical ride‑hailing architecture, this class would be persisted in a database, exposed via a REST API, and manipulated by service layers that handle booking, assignment, and completion logic.

---

## 2. Detailed Breakdown

| Field | Type | Description | Default |
|-------|------|-------------|---------|
| `public Rider rider` | `Rider` | Reference to the user who requested the ride. | – |
| `public Driver driver` | `Driver` | Reference to the driver assigned to the ride. | – |
| `public Location source` | `Location` | Pickup location coordinates or address. | – |
| `public Location destination` | `Location` | Drop‑off location coordinates or address. | – |
| `public boolean status` | `boolean` | Indicates whether the ride is active (`true`) or not (`false`). | `false` (initialized in constructor) |

### Constructor
```java
public Ride(Rider rider, Driver driver, Location source, Location destination) {
    this.rider = rider;
    this.driver = driver;
    this.source = source;
    this.destination = destination;
    this.status = false; 
}
```
* **Parameters** – All four fields are required to create a valid `Ride` instance.  
* **Initialization** – The `status` flag is set to `false` by default, implying the ride has not yet started.  
* **Immutability** – The fields are public and mutable; in a production setting you might want to make them private with getters/setters or use a builder pattern for better encapsulation.

---

## 3. Integrations

| Layer | Interaction | Notes |
|-------|-------------|-------|
| **Persistence** | Likely mapped to a database table (e.g., `rides`). | ORM frameworks (Hibernate/JPA) would use annotations (not shown) to persist the fields. |
| **Service Layer** | `RideService` or `BookingService` would create, update, and query `Ride` objects. | Status changes (`true` → `false`) would be handled by service methods like `startRide()` or `completeRide()`. |
| **API Controllers** | REST endpoints (`/rides`) would accept/return `Ride` DTOs. | Serialization to/from JSON would expose the same fields. |
| **Notification System** | When `status` changes, push notifications could be sent to the rider and driver. | Requires integration with messaging or push services. |
| **Geolocation & Routing** | `source` and `destination` feed into mapping APIs to calculate routes and ETA. | `Location` objects typically contain latitude/longitude. |
| **Driver Assignment** | The `driver` field is populated by an algorithm that matches available drivers to a ride request. | May involve a `DriverPool` or `DispatchService`. |

---

### Suggested Enhancements

1. **Encapsulation** – Make fields private and expose getters/setters or use Lombok annotations.  
2. **Status Enum** – Replace the boolean with an enum (`PENDING`, `IN_PROGRESS`, `COMPLETED`, `CANCELLED`) for richer state management.  
3. **Validation** – Add checks in the constructor to ensure non‑null references.  
4. **Annotations** – Add JPA (`@Entity`, `@Id`, `@ManyToOne`) or JSON (`@JsonProperty`) annotations for persistence and serialization.  

---

**File Location:** `models/Ride.java`  
**Package:** `models`  

This class is a foundational building block for ride‑management functionality, tying together user, driver, and location data into a single, actionable entity.