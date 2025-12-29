# DriverService – Core Driver Management

```java
package services;

import java.util.*;
import models.*;
```

`DriverService` is a lightweight in‑memory service that manages a collection of `Driver` objects.  
It is used by the application to register drivers, update their status, and query for available drivers.

---

## 1. Overview

| Layer | Responsibility |
|-------|----------------|
| **Domain** | Holds the business logic for driver lifecycle operations. |
| **Service** | Exposes CRUD‑like operations on a list of `Driver` instances. |
| **Persistence** | *Not* persisted – the service works with an in‑memory `List<Driver>`. In a production system this would be backed by a database or external store. |

> **Why a Service?**  
> The class encapsulates all driver‑related operations, keeping the rest of the codebase free from direct manipulation of the driver list. It also provides a single point of change if the storage mechanism changes later.

---

## 2. Detailed Breakdown

### 2.1 Fields

| Field | Type | Purpose |
|-------|------|---------|
| `driverList` | `List<Driver>` | The in‑memory collection of all drivers. |

> **Note:** The list is injected via the constructor, allowing callers to supply a pre‑populated list or a mock for testing.

### 2.2 Constructor

```java
public DriverService(List<Driver> driverList) {
    this.driverList = driverList;
}
```

*Initialises the service with an existing list of drivers.*

### 2.3 Public API

| Method | Signature | Description |
|--------|-----------|-------------|
| `RegisterDriver` | `void RegisterDriver(Driver driver)` | Adds a new driver to `driverList`. |
| `updateLocation` | `void updateLocation(int driverID, Location location)` | Finds the driver by `id` and updates its location. |
| `setAvailability` | `void setAvailability(int driverID, boolean mode)` | Sets the driver’s availability flag. |
| `getDrivers` | `List<Driver> getDrivers()` | Returns the full driver list. |
| `getAvailableDrivers` | `List<Driver> getAvailableDrivers()` | Returns a filtered list of drivers that are currently available. |

#### 2.3.1 `RegisterDriver`

```java
public void RegisterDriver(Driver driver) {
    driverList.add(driver);
}
```

*Simple append operation. No duplicate check – callers must ensure uniqueness.*

#### 2.3.2 `updateLocation`

```java
public void updateLocation(int driverID, Location location) {
    for (Driver driver : driverList) {
        if (driver.id == driverID) {
            driver.setLocation(location);
        }
    }
}
```

*Iterates over the list, finds the driver by `id`, and delegates to `Driver.setLocation`.  
If the driver is not found, the method silently does nothing.*

#### 2.3.3 `setAvailability`

```java
public void setAvailability(int driverID, boolean mode) {
    for (Driver driver : driverList) {
        if (driver.id == driverID) {
            driver.setAvailable(mode);
        }
    }
}
```

*Similar to `updateLocation`, but toggles the driver’s availability flag.*

#### 2.3.4 `getDrivers`

```java
public List<Driver> getDrivers() {
    return this.driverList;
}
```

*Returns the raw list – callers can modify it. Consider returning an unmodifiable view for safety.*

#### 2.3.5 `getAvailableDrivers`

```java
public List<Driver> getAvailableDrivers() {
    List<Driver> available = new ArrayList<>();
    for (Driver driver : driverList) {
        if (driver.checkAvailable()) {
            available.add(driver);
        }
    }
    return available;
}
```

*Filters the list based on `Driver.checkAvailable()`.  
The method creates a new list, preserving encapsulation of the internal collection.*

### 2.4 Thread Safety & Performance

* The current implementation is **not thread‑safe**. Concurrent modifications could corrupt the list or cause `ConcurrentModificationException`.  
* For a multi‑threaded environment, consider using `CopyOnWriteArrayList`, `Collections.synchronizedList`, or a proper database.

### 2.5 Error Handling

* No explicit error handling – missing drivers simply result in no action.  
* In a real system, you might throw a custom `DriverNotFoundException` or return a boolean indicating success.

---

## 3. Integrations

| Component | Interaction | Notes |
|-----------|-------------|-------|
| `models.Driver` | Holds driver state (`id`, `location`, `available`). | `DriverService` manipulates these objects directly. |
| `models.Location` | Passed to `updateLocation`. | Represents geographic coordinates. |
| **Controller / API Layer** | Calls `DriverService` methods to expose endpoints like `/drivers/register`, `/drivers/{id}/location`, `/drivers/available`. | The service is a dependency of the controller. |
| **Persistence Layer** | Currently absent; the service uses an in‑memory list. In a future refactor, replace with a DAO or repository. | Allows easy swapping of storage without changing business logic. |
| **Unit Tests** | Mock `List<Driver>` or use a test fixture. | `DriverService` is testable due to constructor injection. |

### Example Usage

```java
List<Driver> initialDrivers = new ArrayList<>();
DriverService driverService = new DriverService(initialDrivers);

// Register a new driver
Driver d = new Driver(1, "Alice");
driverService.RegisterDriver(d);

// Update location
driverService.updateLocation(1, new Location(37.7749, -122.4194));

// Toggle availability
driverService.setAvailability(1, true);

// Query
List<Driver> available = driverService.getAvailableDrivers();
```

---

## 4. Suggested Enhancements

1. **Return Types** – Use `Optional<Driver>` for lookup methods to avoid silent failures.
2. **Thread Safety** – Wrap `driverList` with a concurrent collection or synchronize critical sections.
3. **Persistence** – Abstract storage behind an interface (`DriverRepository`) to allow database or cache backends.
4. **Validation** – Check for duplicate IDs on registration.
5. **Logging** – Add logs for key operations (registration, updates).

---

### TL;DR

`DriverService` is a simple, in‑memory manager for `Driver` objects, providing registration, location updates, availability toggling, and querying of available drivers. It sits between the domain models and higher‑level layers (controllers, APIs), and can be extended to support persistence, concurrency, and richer error handling.