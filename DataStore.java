package yatraa.util; // util package holds helper/utility classes

import yatraa.model.*; // import all model classes (User, Bus, Route, Schedule)
import java.util.*;    // import ArrayList, List, Arrays and other utility classes

// DataStore class - stores all app data in memory (like a simple database)
// all methods are static so we call them without creating a DataStore object
// example: DataStore.getBuses() — no need to do new DataStore()
public class DataStore {

    private static List<User>     users     = new ArrayList<>(); // list of all users
    private static List<Bus>      buses     = new ArrayList<>(); // list of all buses
    private static List<Route>    routes    = new ArrayList<>(); // list of all routes
    private static List<Schedule> schedules = new ArrayList<>(); // list of all schedules
    private static User currentUser = null; // the user who is currently logged in (null if nobody)

    // initialize() - fills in sample/demo data when the app first starts
    // called once in Main.java start() method
    public static void initialize() {

        // --- seed users (sample accounts for testing) ---
        // format: id, fullName, phoneNumber, password, role, age, sex, contactDetail
        users.add(new User("U001", "Admin User",    "9800000001", "admin123",  "admin",     35, "Male",   "admin@yaatri.com"));
        users.add(new User("U002", "Ram Shrestha",  "9800000002", "driver123", "driver",    30, "Male",   "ram@yaatri.com"));
        users.add(new User("U003", "Sita Tamang",   "9800000003", "driver123", "driver",    28, "Female", "sita@yaatri.com"));
        users.add(new User("U004", "Passenger One", "9800000004", "pass123",   "passenger", 22, "Male",   "p1@yaatri.com"));

        // --- seed buses (sample bus data) ---

        Bus b1 = new Bus("BUS-101", "Ashok Leyland Viking", 45, "BA 2 KHA 5678"); // create bus 1
        b1.setAssignedRouteId("R-001");    // assign this bus to route R-001
        b1.setAssignedDriverId("U002");    // assign driver U002 (Ram Shrestha) to this bus
        b1.setCurrentStop("Koteshwor");    // bus is currently at Koteshwor
        b1.setNextStop("Jadibuti");        // bus is heading to Jadibuti next
        b1.setEstimatedArrival("08:15");   // expected to arrive at 08:15

        Bus b2 = new Bus("BUS-102", "Tata Starbus", 40, "BA 2 KHA 1234"); // create bus 2
        b2.setAssignedRouteId("R-002");    // assign this bus to route R-002
        b2.setAssignedDriverId("U003");    // assign driver U003 (Sita Tamang) to this bus
        b2.setCurrentStop("Tripureshwor"); // bus is currently at Tripureshwor
        b2.setNextStop("Jawalakhel");      // bus is heading to Jawalakhel next
        b2.setEstimatedArrival("08:20");   // expected to arrive at 08:20
        b2.setStatus("In Transit");        // bus is currently in transit

        Bus b3 = new Bus("BUS-103", "Eicher Skyline", 38, "BA 2 KHA 9012"); // create bus 3
        b3.setStatus("Delayed");           // this bus is delayed

        buses.add(b1); // add bus 1 to the list
        buses.add(b2); // add bus 2 to the list
        buses.add(b3); // add bus 3 to the list

        // --- seed routes (sample route data) ---

        // route 1: Kathmandu to Bhaktapur with 5 stops
        Route r1 = new Route("R-001", "Kathmandu - Bhaktapur",
            Arrays.asList("Ratna Park", "New Bus Park", "Koteshwor", "Jadibuti", "Bhaktapur"));

        // route 2: Kathmandu to Patan with 4 stops
        Route r2 = new Route("R-002", "Kathmandu - Patan",
            Arrays.asList("Ratna Park", "Tripureshwor", "Jawalakhel", "Patan"));

        // route 3: Kathmandu to Lalitpur with 4 stops
        Route r3 = new Route("R-003", "Kathmandu - Lalitpur",
            Arrays.asList("Sundhara", "Maitighar", "Thapathali", "Lalitpur"));

        routes.add(r1); // add route 1 to the list
        routes.add(r2); // add route 2 to the list
        routes.add(r3); // add route 3 to the list

        // --- seed schedules (sample schedule data) ---

        // schedule 1: BUS-101 on R-001 driven by U002, departing at 07:00
        schedules.add(new Schedule("SCH-001", "BUS-101", "R-001", "07:00", "U002"));
        // schedule 2: BUS-102 on R-002 driven by U003, departing at 07:30
        schedules.add(new Schedule("SCH-002", "BUS-102", "R-002", "07:30", "U003"));
        // schedule 3: BUS-103 on R-003 driven by U002, departing at 08:00
        schedules.add(new Schedule("SCH-003", "BUS-103", "R-003", "08:00", "U002"));
    }

    // ─────────────────────────────────────────────
    // USER METHODS
    // ─────────────────────────────────────────────

    // authenticate() - checks if phone and password match a user in the list
    // returns the User if found, or null if login failed
    public static User authenticate(String phone, String password) {
        return users.stream() // go through each user in the list
            .filter(u -> u.getPhoneNumber().equals(phone) && u.getPassword().equals(password)) // find matching phone+password
            .findFirst()   // get the first match
            .orElse(null); // return null if no match found
    }

    // phoneExists() - checks if a phone number is already registered
    // used in register screen to prevent duplicate accounts
    public static boolean phoneExists(String phone) {
        return users.stream().anyMatch(u -> u.getPhoneNumber().equals(phone)); // true if any user has this phone
    }

    public static void addUser(User user)  { users.add(user); }  // add a new user to the list
    public static List<User> getUsers()    { return users; }      // return all users

    // getUserById() - finds a user by their ID
    // returns the User or null if not found
    public static User getUserById(String id) {
        return users.stream().filter(u -> u.getId().equals(id)).findFirst().orElse(null);
    }

    public static User getCurrentUser()          { return currentUser; }       // return the logged-in user
    public static void setCurrentUser(User user) { currentUser = user; }       // set who is logged in

    // generateUserId() - creates a new unique user ID like "U005"
    public static String generateUserId() {
        return "U" + String.format("%03d", users.size() + 1); // format as 3 digits with leading zeros
    }

    // ─────────────────────────────────────────────
    // BUS METHODS
    // ─────────────────────────────────────────────

    public static List<Bus> getBuses() { return buses; } // return all buses

    // getBusById() - finds a bus by its ID, returns null if not found
    public static Bus getBusById(String id) {
        return buses.stream().filter(b -> b.getBusId().equals(id)).findFirst().orElse(null);
    }

    public static void addBus(Bus bus) { buses.add(bus); } // add a new bus to the list

    // removeBus() - removes a bus from the list by its ID
    public static void removeBus(String busId) {
        buses.removeIf(b -> b.getBusId().equals(busId)); // remove the bus that matches the ID
    }

    // busIdExists() - checks if a bus ID is already taken
    public static boolean busIdExists(String id) {
        return buses.stream().anyMatch(b -> b.getBusId().equals(id)); // true if any bus has this ID
    }

    // getActiveBusCount() - counts buses that are NOT inactive
    public static int getActiveBusCount() {
        return (int) buses.stream().filter(b -> !b.getStatus().equals("Inactive")).count();
    }

    // getDelayedBusCount() - counts how many buses are delayed
    public static int getDelayedBusCount() {
        return (int) buses.stream().filter(b -> b.getStatus().equals("Delayed")).count();
    }

    // ─────────────────────────────────────────────
    // ROUTE METHODS
    // ─────────────────────────────────────────────

    public static List<Route> getRoutes() { return routes; } // return all routes

    // getRouteById() - finds a route by its ID, returns null if not found
    public static Route getRouteById(String id) {
        return routes.stream().filter(r -> r.getRouteId().equals(id)).findFirst().orElse(null);
    }

    public static void addRoute(Route route) { routes.add(route); } // add a new route

    // removeRoute() - removes a route from the list by its ID
    public static void removeRoute(String routeId) {
        routes.removeIf(r -> r.getRouteId().equals(routeId));
    }

    // routeIdExists() - checks if a route ID is already taken
    public static boolean routeIdExists(String id) {
        return routes.stream().anyMatch(r -> r.getRouteId().equals(id));
    }

    // generateRouteId() - creates a new unique route ID like "R-004"
    public static String generateRouteId() {
        return "R-" + String.format("%03d", routes.size() + 1);
    }

    // ─────────────────────────────────────────────
    // SCHEDULE METHODS
    // ─────────────────────────────────────────────

    public static List<Schedule> getSchedules() { return schedules; } // return all schedules

    // getScheduleById() - finds a schedule by its ID
    public static Schedule getScheduleById(String id) {
        return schedules.stream().filter(s -> s.getScheduleId().equals(id)).findFirst().orElse(null);
    }

    // getScheduleForDriver() - finds the schedule assigned to a specific driver
    public static Schedule getScheduleForDriver(String driverId) {
        return schedules.stream().filter(s -> s.getDriverId().equals(driverId)).findFirst().orElse(null);
    }

    public static void addSchedule(Schedule schedule) { schedules.add(schedule); } // add a new schedule

    // removeSchedule() - removes a schedule from the list by its ID
    public static void removeSchedule(String scheduleId) {
        schedules.removeIf(s -> s.getScheduleId().equals(scheduleId));
    }

    // generateScheduleId() - creates a new unique schedule ID like "SCH-004"
    public static String generateScheduleId() {
        return "SCH-" + String.format("%03d", schedules.size() + 1);
    }

    public static int getActiveRouteCount()    { return routes.size(); }    // return total number of routes
    public static int getScheduledTripCount()  { return schedules.size(); } // return total number of schedules
}
