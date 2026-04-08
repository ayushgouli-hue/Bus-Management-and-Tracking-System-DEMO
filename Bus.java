package yatraa.model; // model package holds all data classes

// Bus class - represents one bus in the Yaatri bus tracking system
// Each bus has an ID, model name, capacity, status, and location info
public class Bus {

    private String busId;              // unique bus ID, e.g. "BUS-101"
    private String model;              // bus model name, e.g. "Ashok Leyland Viking"
    private int capacity;             // how many passengers it can carry, e.g. 45
    private String registrationNumber; // number plate, e.g. "BA 2 KHA 5678"
    private String status;            // current status: "Active", "Inactive", or "Delayed"
    private String assignedDriverId;  // ID of the driver assigned to this bus
    private String assignedRouteId;   // ID of the route this bus is running on
    private String currentStop;       // the stop the bus is currently at
    private String nextStop;          // the next stop the bus is heading to
    private String estimatedArrival;  // when the bus is expected to arrive, e.g. "08:15"

    // constructor - creates a new Bus with the 4 main required fields
    // status is set to "Active" by default when a new bus is created
    public Bus(String busId, String model, int capacity, String registrationNumber) {
        this.busId = busId;                           // set the bus ID
        this.model = model;                           // set the bus model
        this.capacity = capacity;                     // set the seating capacity
        this.registrationNumber = registrationNumber; // set the number plate
        this.status = "Active";                       // new buses start as Active
    }

    // getters - let other classes READ the bus fields

    public String getBusId()              { return busId; }              // returns bus ID
    public String getModel()              { return model; }              // returns model name
    public int getCapacity()              { return capacity; }           // returns capacity
    public String getRegistrationNumber() { return registrationNumber; } // returns number plate
    public String getStatus()             { return status; }             // returns current status
    public String getAssignedDriverId()   { return assignedDriverId; }   // returns driver ID
    public String getAssignedRouteId()    { return assignedRouteId; }    // returns route ID
    public String getCurrentStop()        { return currentStop; }        // returns current stop
    public String getNextStop()           { return nextStop; }           // returns next stop
    public String getEstimatedArrival()   { return estimatedArrival; }   // returns ETA

    // setters - let other classes UPDATE the bus fields

    public void setBusId(String busId)                  { this.busId = busId; }                  // update bus ID
    public void setModel(String model)                  { this.model = model; }                  // update model
    public void setCapacity(int capacity)               { this.capacity = capacity; }             // update capacity
    public void setRegistrationNumber(String reg)       { this.registrationNumber = reg; }        // update plate
    public void setStatus(String status)                { this.status = status; }                 // update status
    public void setAssignedDriverId(String id)          { this.assignedDriverId = id; }           // update driver
    public void setAssignedRouteId(String id)           { this.assignedRouteId = id; }            // update route
    public void setCurrentStop(String stop)             { this.currentStop = stop; }              // update current stop
    public void setNextStop(String stop)                { this.nextStop = stop; }                 // update next stop
    public void setEstimatedArrival(String eta)         { this.estimatedArrival = eta; }          // update ETA

    // toString() is called when Java needs to display this Bus as text
    // for example when it shows up in a list or printed to console
    @Override
    public String toString() {
        return busId + " — " + model; // shows like: "BUS-101 — Ashok Leyland Viking"
    }
}
