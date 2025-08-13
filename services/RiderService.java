package services;

import java.util.*;
import models.*;

public class RiderService {
    private DriverService driverService;
    public Map<Rider, Ride> activeRides;

    public RiderService(DriverService driverService) {
        this.driverService = driverService;
        this.activeRides = new HashMap<>();
    }

    private Driver findNearestAvailableDriver(Location source) {
        List<Driver> availableDrivers = driverService.getAvailableDrivers();
        Driver nearest = null;
        double minDistance = Double.MAX_VALUE;

        for (Driver driver : availableDrivers) {
            Location driverLocation = driver.getCurrentLocation();
            double distance = source.distanceTo(driverLocation);
            if (distance < minDistance) {
                minDistance = distance;
                nearest = driver;
            }
        }
        return nearest;
    }

    public Ride requestRide(Rider rider, Location source, Location destination) {
        Driver driver = findNearestAvailableDriver(source);
        if (driver == null) {
            System.out.println("No available driver at the moment.");
            return null;
        }

        Ride ride = new Ride(rider, driver, source, destination);
        driver.setAvailable(false);
        activeRides.put(rider, ride);

        System.out.println("Ride booked successfully with driver: " + driver.getName());
        return ride;
    }

    public void endRide(Rider rider) {
        Ride ride = activeRides.get(rider);
        if (ride == null) {
            System.out.println("No active ride for this rider.");
            return;
        }

        ride.status = true;
        Driver driver = ride.driver;
        driver.setAvailable(true);
        driver.setLocation(ride.destination); // assuming driver ends at destination
        activeRides.remove(rider);

        System.out.println("Ride ended for rider: " + rider.getName());
    }

    public Ride getRideForRider(Rider rider) {
        return activeRides.getOrDefault(rider, null);
    }
}
