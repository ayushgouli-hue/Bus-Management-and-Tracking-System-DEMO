package yatraa.model; // model package holds all data classes

import java.util.ArrayList; // ArrayList is a list that can grow or shrink
import java.util.List;       // List is the interface that ArrayList uses

// Route class - represents a bus route with a list of stops
// Example: "Kathmandu - Bhaktapur" with stops at Ratna Park, Koteshwor, etc.
public class Route {

    private String routeId;      // unique route ID, e.g. "R-001"
    private String routeName;    // name of the route, e.g. "Kathmandu - Bhaktapur"
    private List<String> stops;  // list of all bus stops on this route

    // first constructor - creates a route with just an ID and name, no stops yet
    public Route(String routeId, String routeName) {
        this.routeId = routeId;        // set the route ID
        this.routeName = routeName;    // set the route name
        this.stops = new ArrayList<>(); // start with an empty list of stops
    }

    // second constructor - creates a route with an ID, name, AND a list of stops
    public Route(String routeId, String routeName, List<String> stops) {
        this.routeId = routeId;              // set the route ID
        this.routeName = routeName;          // set the route name
        this.stops = new ArrayList<>(stops); // copy the given stops into our list
    }

    // getters - let other classes READ the route fields

    public String getRouteId()      { return routeId; }   // returns the route ID
    public String getRouteName()    { return routeName; } // returns the route name
    public List<String> getStops()  { return stops; }     // returns all the stops

    // setters - let other classes UPDATE the route fields

    public void setRouteId(String routeId)     { this.routeId = routeId; }     // update route ID
    public void setRouteName(String routeName) { this.routeName = routeName; } // update route name

    // addStop() - adds one stop to the end of the stop list
    public void addStop(String stop) {
        stops.add(stop); // add the new stop to the list
    }

    // removeStop() - removes a stop from the stop list by name
    public void removeStop(String stop) {
        stops.remove(stop); // remove the matching stop from the list
    }

    // getStopsDisplay() - returns all stops joined as one readable string
    // example output: "Ratna Park → Koteshwor → Jadibuti → Bhaktapur"
    public String getStopsDisplay() {
        return String.join(" → ", stops); // join the stops with an arrow between them
    }

    // toString() is called when Java needs to show this Route as text
    @Override
    public String toString() {
        return routeId + ": " + routeName; // example: "R-001: Kathmandu - Bhaktapur"
    }
}
