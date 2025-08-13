import models.*;
import services.*;

import java.util.*;

public class Main {
    public static void main(String[] args) {
        // Create some initial drivers
        Driver d1 = new Driver(1, "Driver A", new Location(2, 3));
        Driver d2 = new Driver(2, "Driver B", new Location(10, 20));
        Driver d3 = new Driver(3, "Driver C", new Location(4, 4));

        List<Driver> driverList = new ArrayList<>();
        driverList.add(d1);
        driverList.add(d2);
        driverList.add(d3);

        // Initialize services
        DriverService driverService = new DriverService(driverList);
        RiderService rideService = new RiderService(driverService);

        // Create a rider
        Rider r1 = new Rider(100, "Rider One");

        // Request a ride
        System.out.println("\n--- Rider requests a ride ---");
        Location source = new Location(3, 3);
        Location destination = new Location(7, 8);
        Ride ride = rideService.requestRide(r1, source, destination);

        if (ride != null) {
            System.out.println("Ride started from: (" + source.getX() + ", " + source.getY() + ")");
            System.out.println("Destination: (" + destination.getX() + ", " + destination.getY() + ")");
        }

        //  System.out.println(rideService.activeRides);

        // End the ride
        System.out.println("\n--- Ride is ending ---");
        rideService.endRide(r1);

        // Try getting ride again (should return null)
        System.out.println("\n--- Get Ride Info After Ending ---");
        Ride rideInfo = rideService.getRideForRider(r1);
        if (rideInfo == null) {
            System.out.println("No active ride for Rider One.");
        }
       
    }
}
