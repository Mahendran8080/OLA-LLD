package services;

import java.util.*;
import models.*;

public class DriverService {
    List<Driver> driverList;

    public DriverService(List<Driver> driverList) {
        this.driverList = driverList;
    }

    public void RegisterDriver(Driver driver) {
        driverList.add(driver);
    }

    public void updateLocation(int driverID, Location location) {
        for (Driver driver : driverList) {
            if (driver.id == driverID) {
                driver.setLocation(location);
            }
        }
    }

    public void setAvailability(int driverID, boolean mode) {
        for (Driver driver : driverList) {
            if (driver.id == driverID) {
                driver.setAvailable(mode);
            }
        }
    }

    public List<Driver> getDrivers() {
        return this.driverList;
    }

    public List<Driver> getAvailableDrivers() {
        List<Driver> available = new ArrayList<>();
        for (Driver driver : driverList) {
            if (driver.checkAvailable()) {
                available.add(driver);
            }
        }
        return available;
    }
}
