package yatraa.admin; // admin package holds all admin screens

import javafx.geometry.Insets;  // used for padding
import javafx.geometry.Pos;     // used for alignment
import javafx.scene.control.*;  // Button, Label, ComboBox, TextField, Alert, etc.
import javafx.scene.layout.*;   // VBox, HBox, GridPane, Pane, ScrollPane, etc.
import yatraa.model.Bus;        // Bus data class
import yatraa.model.Route;      // Route data class
import yatraa.model.Schedule;   // Schedule data class
import yatraa.model.User;       // User data class
import yatraa.ui.UIFactory;     // helper for creating styled UI elements
import yatraa.util.DataStore;   // our data storage class

// AdminScheduleView class - the SCHEDULE tab content for the admin
// Allows the admin to create a new schedule by picking a bus, route, driver, and time
public class AdminScheduleView {

    private ScrollPane root;              // scroll pane wraps everything
    private VBox content;                 // main vertical layout
    private ComboBox<String> busCombo;    // dropdown to pick a bus
    private ComboBox<String> routeCombo;  // dropdown to pick a route
    private ComboBox<String> driverCombo; // dropdown to pick a driver
    private TextField departureField;     // input for departure time e.g. "07:00"
    private Label errorLabel;             // red label for validation errors
    private VBox scheduleList;           // VBox that holds the schedule cards

    // constructor - builds the UI
    public AdminScheduleView() {
        buildUI(); // build all the UI elements
    }

    // buildUI() - creates the create-schedule form and the list of existing schedules
    private void buildUI() {
        content = new VBox(18);                               // main layout with 18px gap
        content.setPadding(new Insets(24));                   // 24px padding
        content.setStyle("-fx-background-color: #f0f0f0;");  // grey background

        Label title = UIFactory.pageTitle("Schedule");       // large page title
        Pane div = UIFactory.divider();                      // thin divider line

        // --- form card for creating a schedule ---
        VBox formCard = UIFactory.formCard(); // white dashed-border card

        Label formTitle = UIFactory.sectionTitle("Create Schedule"); // form heading

        busCombo     = UIFactory.paperCombo(); // dropdown for selecting a bus
        routeCombo   = UIFactory.paperCombo(); // dropdown for selecting a route
        driverCombo  = UIFactory.paperCombo(); // dropdown for selecting a driver
        departureField = UIFactory.paperField("e.g., 07:00"); // departure time input

        populateCombos(); // fill the dropdowns with buses, routes, and drivers from DataStore

        errorLabel = UIFactory.errorLabel(""); // error label
        errorLabel.setVisible(false);          // hidden by default

        Button saveBtn = UIFactory.primaryBtn("CREATE SCHEDULE"); // save button
        saveBtn.setOnAction(e -> handleSave());                    // call handleSave() when clicked

        // add all form elements to the form card
        formCard.getChildren().addAll(
            formTitle,
            UIFactory.formLabel("Bus"),                     busCombo,       // "BUS" + dropdown
            UIFactory.formLabel("Route"),                   routeCombo,     // "ROUTE" + dropdown
            UIFactory.formLabel("Driver"),                  driverCombo,    // "DRIVER" + dropdown
            UIFactory.formLabel("Departure Time (HH:MM)"),  departureField, // "DEPARTURE TIME" + input
            errorLabel, saveBtn                                             // error + save button
        );

        Label listTitle = UIFactory.sectionTitle("All Schedules"); // section heading

        scheduleList = new VBox(12); // VBox to hold schedule cards with 12px gap
        refreshList();               // load and show all existing schedules

        // add everything to the main content layout
        content.getChildren().addAll(title, div, formCard, listTitle, scheduleList);
        root = UIFactory.wrappedScroll(content); // wrap in scroll pane
    }

    // populateCombos() - fills the dropdowns with data from DataStore
    // called when the form first loads and when we refresh
    private void populateCombos() {
        busCombo.getItems().clear();    // clear existing items
        routeCombo.getItems().clear();  // clear existing items
        driverCombo.getItems().clear(); // clear existing items

        // add all buses to the bus dropdown
        for (Bus b : DataStore.getBuses())
            busCombo.getItems().add(b.getBusId() + " — " + b.getModel()); // e.g. "BUS-101 — Ashok Leyland"

        // add all routes to the route dropdown
        for (Route r : DataStore.getRoutes())
            routeCombo.getItems().add(r.getRouteId() + ": " + r.getRouteName()); // e.g. "R-001: Kathmandu - Bhaktapur"

        // add only driver users to the driver dropdown
        for (User u : DataStore.getUsers()) {
            if ("driver".equals(u.getRole())) // only add users with role "driver"
                driverCombo.getItems().add(u.getId() + " — " + u.getFullName()); // e.g. "U002 — Ram Shrestha"
        }
    }

    // handleSave() - validates the form and creates a new Schedule
    private void handleSave() {
        errorLabel.setVisible(false); // hide previous error

        // check all fields are filled
        if (busCombo.getValue() == null || routeCombo.getValue() == null
                || driverCombo.getValue() == null || departureField.getText().trim().isEmpty()) {
            errorLabel.setText("✕  All fields are required."); // show error
            errorLabel.setVisible(true);
            return; // stop here
        }

        // extract just the ID from each dropdown selection
        String busId    = busCombo.getValue().split(" — ")[0];  // get "BUS-101" from "BUS-101 — Ashok..."
        String routeId  = routeCombo.getValue().split(":")[0];  // get "R-001" from "R-001: Kathmandu..."
        String driverId = driverCombo.getValue().split(" — ")[0]; // get "U002" from "U002 — Ram..."
        String departure = departureField.getText().trim();         // get the departure time

        // validate that the time format is HH:MM e.g. "07:00"
        if (!departure.matches("\\d{2}:\\d{2}")) {
            errorLabel.setText("✕  Time format must be HH:MM (e.g., 07:30)."); // show error
            errorLabel.setVisible(true);
            return; // stop here
        }

        String id = DataStore.generateScheduleId(); // generate a new unique schedule ID
        DataStore.addSchedule(new Schedule(id, busId, routeId, departure, driverId)); // add the schedule

        // clear all the form fields after saving
        busCombo.setValue(null);    // reset bus dropdown
        routeCombo.setValue(null);  // reset route dropdown
        driverCombo.setValue(null); // reset driver dropdown
        departureField.clear();     // clear departure time

        refreshList(); // reload the schedule list to show the new one
    }

    // refreshList() - clears and reloads the schedule card list from DataStore
    private void refreshList() {
        scheduleList.getChildren().clear(); // clear existing cards
        for (Schedule sched : DataStore.getSchedules()) {        // loop through all schedules
            scheduleList.getChildren().add(buildScheduleCard(sched)); // build and add a card for each
        }
    }

    // buildScheduleCard() - creates a card showing one schedule's details with a Delete button
    private VBox buildScheduleCard(Schedule sched) {
        VBox card = new VBox(10);                   // vertical layout with 10px gap
        card.getStyleClass().add("schedule-card");   // apply schedule card style

        // --- top row: schedule ID on left, status badge on right ---
        HBox topRow = new HBox();
        topRow.setAlignment(Pos.CENTER_LEFT); // align to left

        Label idLbl = new Label(sched.getScheduleId()); // schedule ID e.g. "SCH-001"
        idLbl.getStyleClass().add("bus-id-label");       // bold large style

        Region spacer = new Region();               // spacer pushes badge to the right
        HBox.setHgrow(spacer, Priority.ALWAYS);     // spacer fills available space

        Label statusBadge = UIFactory.badge(sched.getStatus(), sched.getStatus().toLowerCase()); // status badge
        topRow.getChildren().addAll(idLbl, spacer, statusBadge); // add ID, spacer, badge

        // look up the route, bus, and driver objects using the IDs stored in the schedule
        Route route  = DataStore.getRouteById(sched.getRouteId());   // find the Route object
        Bus bus      = DataStore.getBusById(sched.getBusId());        // find the Bus object
        User driver  = DataStore.getUserById(sched.getDriverId());    // find the Driver User object

        // --- details grid: 2 columns with bus, route, driver, departure ---
        GridPane grid = new GridPane();
        grid.setHgap(20); // 20px gap between columns
        grid.setVgap(6);  // 6px gap between rows

        addGridCell(grid, "BUS",       sched.getBusId()  + (bus    != null ? " — " + bus.getModel()       : ""), 0, 0);
        addGridCell(grid, "ROUTE",     route != null ? route.getRouteName() : sched.getRouteId(),                 1, 0);
        addGridCell(grid, "DRIVER",    driver != null ? driver.getFullName() : sched.getDriverId(),               0, 1);
        addGridCell(grid, "DEPARTURE", sched.getDepartureTime(),                                                  1, 1);

        // --- delete button ---
        HBox actions = new HBox(10); // horizontal row
        Button deleteBtn = UIFactory.dangerBtn("⌫ Delete"); // delete button
        deleteBtn.setOnAction(e -> {
            DataStore.removeSchedule(sched.getScheduleId()); // remove this schedule from data store
            refreshList();                                    // reload the schedule list
        });
        actions.getChildren().add(deleteBtn); // add delete button to row

        // add all sections to the card
        card.getChildren().addAll(topRow, grid, UIFactory.divider(), actions);
        return card; // return the finished schedule card
    }

    // addGridCell() - helper to add a label+value cell at a specific grid position
    // label is the small header e.g. "BUS", value is the content e.g. "BUS-101 — Ashok Leyland"
    private void addGridCell(GridPane grid, String label, String value, int col, int row) {
        VBox cell = new VBox(2); // VBox with 2px gap

        Label lbl = new Label(label);          // small uppercase label
        lbl.getStyleClass().add("meta-label"); // grey small text style

        Label val = new Label(value);           // the value text
        val.getStyleClass().add("value-label"); // bold dark text style
        val.setWrapText(true);                  // wrap if text is too long

        cell.getChildren().addAll(lbl, val); // add both to the cell
        grid.add(cell, col, row);            // add the cell to the grid at col/row position
    }

    // refresh() - called by AdminDashboard when switching to this tab
    public void refresh() {
        populateCombos(); // re-fill dropdowns in case buses/routes changed
        refreshList();    // reload schedule list
    }

    // getRoot() - returns the scroll pane to be shown in the content area
    public ScrollPane getRoot() { return root; }
}
