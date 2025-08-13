package models;

public class Ride {
    public Rider rider;
    public Driver driver;
    public Location source;
    public Location destination;
    public boolean status;
    
    public Ride(Rider rider,Driver driver,Location source,Location destination) {
        this.rider = rider;
        this.driver = driver;
        this.source = source;
        this.destination = destination;
        this.status = false; 
    }
}