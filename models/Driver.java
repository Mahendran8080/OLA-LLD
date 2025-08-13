package models;

public class Driver {
    public int id;
    public String name;
    public Location location;
    public boolean isAvailable;

    public Driver(int id, String name, Location location) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.isAvailable = true;
    }

    public Location getLocation() {
        return this.location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public boolean checkAvailable() {
        return this.isAvailable;
    }

    public void setAvailable(boolean mode) {
        this.isAvailable = mode;
    }

    public Location getCurrentLocation() {
        return this.location;
    }

    public String getName() {
        return this.name;
    }
}
