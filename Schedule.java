package yatraa.model; // model package holds all data classes

// Schedule class - represents one bus trip schedule
// It links together a bus, a route, a driver, and a departure time
public class Schedule {

    private String scheduleId;    // unique ID for this schedule, e.g. "SCH-001"
    private String busId;         // which bus is running, e.g. "BUS-101"
    private String routeId;       // which route it runs on, e.g. "R-001"
    private String departureTime; // when it departs, e.g. "07:00"
    private String driverId;      // which driver is assigned, e.g. "U002"
    private String status;        // current status of the schedule, e.g. "Scheduled"

    // constructor - creates a new Schedule with all required information
    // status is set to "Scheduled" by default
    public Schedule(String scheduleId, String busId, String routeId,
                    String departureTime, String driverId) {
        this.scheduleId = scheduleId;       // set the schedule ID
        this.busId = busId;                 // set which bus runs this schedule
        this.routeId = routeId;             // set which route it follows
        this.departureTime = departureTime; // set the departure time
        this.driverId = driverId;           // set who drives this bus
        this.status = "Scheduled";          // default status when first created
    }

    // getters - let other classes READ the schedule fields

    public String getScheduleId()    { return scheduleId; }    // returns schedule ID
    public String getBusId()         { return busId; }         // returns bus ID
    public String getRouteId()       { return routeId; }       // returns route ID
    public String getDepartureTime() { return departureTime; } // returns departure time
    public String getDriverId()      { return driverId; }      // returns driver ID
    public String getStatus()        { return status; }        // returns status

    // setters - let other classes UPDATE the schedule fields

    public void setBusId(String busId)             { this.busId = busId; }             // update bus
    public void setRouteId(String routeId)         { this.routeId = routeId; }         // update route
    public void setDepartureTime(String time)      { this.departureTime = time; }      // update time
    public void setDriverId(String driverId)       { this.driverId = driverId; }       // update driver
    public void setStatus(String status)           { this.status = status; }           // update status
}
