# `models/Location` – 2‑D Point Representation

## 1. Overview
`models.Location` is a lightweight, immutable data holder that represents a point in a two‑dimensional Cartesian coordinate system.  
It is part of the **`models`** package and is used throughout the codebase wherever spatial information is required (e.g., mapping, routing, physics simulations, or UI layout).

Key responsibilities:
- Store `x` and `y` coordinates as `double` values.
- Provide read‑only accessors (`getX()`, `getY()`).
- Compute Euclidean distance to another `Location`.

Because the class is compiled to a `.class` file, the source is not directly visible in the repository, but the byte‑code reveals the public API and the core logic.

---

## 2. Detailed Breakdown

| Feature | Implementation Details |
|---------|------------------------|
| **Package** | `models` |
| **Fields** | `private double x;`<br>`private double y;` |
| **Constructor** | `public Location(double x, double y)` – assigns the coordinates. |
| **Accessors** | `public double getX()`<br>`public double getY()` |
| **Distance Calculation** | `public double distanceTo(Location other)`<br>Uses the Euclidean formula:<br>`Math.sqrt(Math.pow(x - other.x, 2) + Math.pow(y - other.y, 2))` |
| **Immutability** | No setters; coordinates are set once at construction. |
| **Serialization** | Not explicitly shown, but the class can be serialized if it implements `Serializable` (not indicated in the byte‑code). |
| **Source File** | `Location.java` (referenced in the class file’s `SourceFile` attribute). |

### Code Snippet (Reconstructed)

```java
package models;

public class Location {
    private final double x;
    private final double y;

    public Location(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() { return x; }

    public double getY() { return y; }

    public double distanceTo(Location other) {
        double dx = this.x - other.x;
        double dy = this.y - other.y;
        return Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2));
    }
}
```

> **Note**: The byte‑code shows the use of `Math.pow` and `Math.sqrt`, confirming the Euclidean distance implementation.

---

## 3. Integrations

| System Component | Interaction | Purpose |
|------------------|-------------|---------|
| **Path / Route Calculations** | `Location` instances are used as waypoints or nodes. | Compute distances between waypoints to determine route length or to apply heuristics. |
| **Spatial Indexing / Clustering** | `distanceTo()` feeds into clustering algorithms or nearest‑neighbor searches. | Identify proximity relationships or group points. |
| **UI / Rendering** | Coordinates are passed to graphics libraries or layout engines. | Position elements on a 2‑D canvas or map. |
| **Physics / Simulation** | Used as positions for objects in a simulation. | Calculate movement, collision detection, or force calculations. |
| **Data Persistence** | Serialized or stored in databases as `x` and `y` columns. | Persist spatial data across sessions. |

> **Typical Usage Pattern**  
> ```java
> Location a = new Location(1.0, 2.0);
> Location b = new Location(4.0, 6.0);
> double dist = a.distanceTo(b); // 5.0
> ```

---

## 4. Best Practices

- **Immutability**: Once created, a `Location` cannot change. This guarantees thread‑safety and consistency when used in collections or as keys.
- **Precision**: Coordinates are `double`; be mindful of floating‑point inaccuracies when comparing distances.
- **Extensibility**: If additional spatial features (e.g., 3‑D support, vector operations) are needed, consider extending this class or creating a new `Vector2D` utility.

---

## 5. Summary

`models.Location` is a foundational building block for any feature that requires 2‑D spatial reasoning. Its simple API—coordinate storage, getters, and distance calculation—makes it easy to integrate across the application, from UI rendering to algorithmic processing.