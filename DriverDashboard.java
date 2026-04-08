package yatraa.driver; // driver package holds the driver screen

import javafx.animation.KeyFrame;   // used to create a repeating action every second
import javafx.animation.Timeline;   // used to run the KeyFrame on a timer
import javafx.geometry.Insets;      // used for padding
import javafx.geometry.Pos;         // used for alignment
import javafx.scene.control.*;      // Button, Label, etc.
import javafx.scene.layout.*;       // VBox, HBox, BorderPane, GridPane, Region, etc.
import javafx.util.Duration;        // used to set the timer duration e.g. 1 second
import yatraa.Main;                 // to switch screens (logout)
import yatraa.model.Bus;            // Bus data class
import yatraa.model.Route;          // Route data class
import yatraa.model.Schedule;       // Schedule data class
import yatraa.model.User;           // User data class
import yatraa.ui.UIFactory;         // helper for creating styled UI elements
import yatraa.util.DataStore;       // our data storage class
import java.time.LocalTime;         // used to get the current time
import java.time.format.DateTimeFormatter; // used to format the time as text
import java.util.List;               // List interface

// DriverDashboard class - the main screen for drivers
// Shows trip information and lets the driver update their bus status in real time
public class DriverDashboard {

    private BorderPane root;              // main layout: header at top, content in center
    private Label statusLabel;            // shows the current bus status e.g. "On Time"
    private Label lastUpdatedLabel;       // shows when the status was last updated
    private Label currentLocationLabel;   // shows the current bus stop
    private Label nextStopLabel;          // shows the next bus stop

    private String currentStatus = "On Time"; // tracks the current status text
    private Bus assignedBus;              // the bus assigned to this driver
    private Schedule mySchedule;          // the schedule assigned to this driver
    private Route myRoute;               // the route this driver is running on
    private int currentStopIndex = 0;    // tracks which stop the bus is currently at

    // constructor - loads data from DataStore and builds the UI
    public DriverDashboard() {
        loadData();  // find the schedule, bus, and route for this driver
        buildUI();   // build the screen
    }

    // loadData() - finds the bus, schedule and route assigned to the current driver
    private void loadData() {
        User user = DataStore.getCurrentUser(); // get the logged-in driver user
        if (user != null) {
            mySchedule = DataStore.getScheduleForDriver(user.getId()); // find their schedule
            if (mySchedule != null) {
                assignedBus = DataStore.getBusById(mySchedule.getBusId());     // find their bus
                myRoute     = DataStore.getRouteById(mySchedule.getRouteId()); // find their route
            }
        }
    }

    // buildUI() - creates the full screen with header at top and content in center
    private void buildUI() {
        root = new BorderPane();                             // main layout container
        root.setStyle("-fx-background-color: #f0f0f0;");    // grey background
        root.setTop(buildHeader());                          // header bar at top
        root.setCenter(buildContent());                      // scrollable content in center
    }

    // buildHeader() - creates the top dark bar with app name, Driver label, and logout button
    private HBox buildHeader() {
        User user = DataStore.getCurrentUser(); // get the logged-in user

        // get the first letter of their name for the avatar button
        String initials = user != null ? String.valueOf(user.getFullName().charAt(0)).toUpperCase() : "D";

        HBox header = new HBox();                         // horizontal layout
        header.getStyleClass().add("header-bar");         // apply dark header CSS style
        header.setAlignment(Pos.CENTER_LEFT);             // align to left

        VBox titleBox = new VBox(2);                      // VBox for app name + role label
        Label appLabel = new Label("YAATRI");             // app name
        appLabel.getStyleClass().add("header-title");     // white bold style
        Label roleLabel = new Label("Driver");            // role label
        roleLabel.getStyleClass().add("header-subtitle"); // grey small style
        titleBox.getChildren().addAll(appLabel, roleLabel); // add both to titleBox

        Region spacer = new Region();                     // spacer to push avatar to the right
        HBox.setHgrow(spacer, Priority.ALWAYS);           // spacer fills available space

        Button avatarBtn = new Button(initials);          // avatar button showing driver's initial
        avatarBtn.getStyleClass().add("header-avatar");   // apply circular button style
        avatarBtn.setOnAction(e -> {
            DataStore.setCurrentUser(null); // clear the logged-in user (logout)
            Main.showLogin();               // go back to login screen
        });

        header.getChildren().addAll(titleBox, spacer, avatarBtn); // add items to header
        return header; // return the finished header
    }

    // buildContent() - creates the scrollable content area with status and route cards
    private ScrollPane buildContent() {
        VBox content = new VBox(20);                              // vertical layout with 20px gap
        content.setPadding(new Insets(24));                       // 24px padding
        content.setStyle("-fx-background-color: #f0f0f0;");      // grey background

        Label title = UIFactory.pageTitle("Driver Status Update"); // large page title
        Pane div = UIFactory.divider();                            // divider line

        VBox statusCard = buildStatusCard();     // card showing current status
        VBox routeCard  = buildRouteInfoCard();  // card showing trip info
        Label updateLabel = UIFactory.sectionTitle("Update Status"); // section heading
        VBox buttonsBox = buildActionButtons();  // status update buttons

        content.getChildren().addAll(title, div, statusCard, routeCard, updateLabel, buttonsBox);
        return UIFactory.wrappedScroll(content); // wrap in scroll pane
    }

    // buildStatusCard() - creates the card showing the bus's current status
    private VBox buildStatusCard() {
        VBox card = new VBox(12);               // vertical layout with 12px gap
        card.getStyleClass().add("info-card");   // white bordered card style

        // top row: "Current Status" label on left, status badge on right
        HBox topRow = new HBox();
        topRow.setAlignment(Pos.CENTER_LEFT); // align to left

        Label statusTitle = new Label("Current Status"); // label text
        statusTitle.setStyle("-fx-font-size: 13px; -fx-text-fill: #666666;"); // grey style

        Region spacer = new Region();               // spacer to push badge to the right
        HBox.setHgrow(spacer, Priority.ALWAYS);     // spacer fills available space

        statusLabel = new Label("On Time"); // shows the current status text
        statusLabel.setStyle(
            "-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #333333;" +
            "-fx-border-color: #aaaaaa; -fx-border-width: 1px; -fx-padding: 3px 10px;"
        );

        topRow.getChildren().addAll(statusTitle, spacer, statusLabel); // add items to row

        lastUpdatedLabel = new Label("⏱  Last updated: --"); // shows when status was last changed
        lastUpdatedLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #888888;"); // grey small text

        card.getChildren().addAll(topRow, lastUpdatedLabel); // add to card
        return card; // return the finished status card
    }

    // buildRouteInfoCard() - creates the card showing trip information (bus, route, stops)
    private VBox buildRouteInfoCard() {
        VBox card = new VBox(12);               // vertical layout with 12px gap
        card.getStyleClass().add("info-card");   // white bordered card style

        Label title = UIFactory.sectionTitle("Trip Information"); // section heading

        if (mySchedule == null) { // no schedule found for this driver
            Label noSched = new Label("No schedule assigned for today."); // message
            noSched.setStyle("-fx-font-size: 12px; -fx-text-fill: #888888;"); // grey style
            card.getChildren().addAll(title, noSched); // add heading and message
            return card; // return early
        }

        // --- details grid: bus, departure, route, current stop, next stop ---
        GridPane grid = new GridPane();
        grid.setHgap(20); // 20px between columns
        grid.setVgap(12); // 12px between rows

        // set 2 equal columns
        ColumnConstraints c1 = new ColumnConstraints(); c1.setPercentWidth(50); // left 50%
        ColumnConstraints c2 = new ColumnConstraints(); c2.setPercentWidth(50); // right 50%
        grid.getColumnConstraints().addAll(c1, c2); // apply to grid

        // bus ID cell
        VBox busCell = locationCell("BUS ID", assignedBus != null ? assignedBus.getBusId() : mySchedule.getBusId());

        // departure time cell
        VBox depCell = locationCell("TODAY'S DEPARTURE", mySchedule.getDepartureTime());

        // route name cell
        VBox routeCell = locationCell("ROUTE", myRoute != null ? myRoute.getRouteName() : mySchedule.getRouteId());

        // current stop cell - uses a label we can update later
        VBox currentCell = new VBox(2);
        Label curMeta = new Label("CURRENT LOCATION"); // small label
        curMeta.getStyleClass().add("meta-label");     // grey small style
        currentLocationLabel = new Label(getCurrentStop()); // shows current stop
        currentLocationLabel.getStyleClass().add("value-label"); // bold dark style
        currentCell.getChildren().addAll(curMeta, currentLocationLabel);

        // next stop cell - uses a label we can update later
        VBox nextCell = new VBox(2);
        Label nxtMeta = new Label("NEXT STOP"); // small label
        nxtMeta.getStyleClass().add("meta-label"); // grey small style
        nextStopLabel = new Label(getNextStop());  // shows next stop
        nextStopLabel.getStyleClass().add("value-label"); // bold dark style
        nextCell.getChildren().addAll(nxtMeta, nextStopLabel);

        // add all cells to the grid at specific positions (column, row)
        grid.add(busCell,     0, 0); // bus ID at column 0, row 0
        grid.add(depCell,     1, 0); // departure at column 1, row 0
        grid.add(routeCell,   0, 1); // route at column 0, row 1
        grid.add(currentCell, 0, 2); // current stop at column 0, row 2
        grid.add(nextCell,    1, 2); // next stop at column 1, row 2

        card.getChildren().addAll(title, grid); // add heading and grid to card
        return card; // return the finished route card
    }

    // locationCell() - helper to create a small VBox with a label and value
    private VBox locationCell(String label, String value) {
        VBox cell = new VBox(2);                    // VBox with 2px gap
        Label lbl = new Label(label);               // the small label e.g. "BUS ID"
        lbl.getStyleClass().add("meta-label");      // grey small style
        Label val = new Label(value);               // the value e.g. "BUS-101"
        val.getStyleClass().add("value-label");     // bold dark style
        cell.getChildren().addAll(lbl, val);        // add label and value
        return cell;                                 // return the cell
    }

    // buildActionButtons() - creates the 4 status update buttons for the driver
    private VBox buildActionButtons() {
        VBox box = new VBox(10); // vertical layout with 10px gap

        // "Departed from Start" button - sets bus as departed from first stop
        Button departedBtn = new Button("↗  Departed from Start");
        departedBtn.getStyleClass().add("status-departed-btn"); // apply departed button style
        departedBtn.setMaxWidth(Double.MAX_VALUE);              // stretch to full width
        departedBtn.setOnAction(e -> {
            currentStopIndex = 0;          // reset to first stop
            updateStatus("Departed");      // update status to "Departed"
            updateLocationLabels();        // refresh the stop labels on screen
        });

        // "Arrived at Next Stop" button - moves bus to the next stop
        Button arrivedBtn = new Button("✓  Arrived at Next Stop");
        arrivedBtn.getStyleClass().add("status-arrived-btn"); // apply arrived button style
        arrivedBtn.setMaxWidth(Double.MAX_VALUE);             // stretch to full width
        arrivedBtn.setOnAction(e -> {
            advanceStop();                 // move to the next stop
            updateStatus("In Transit");    // update status to "In Transit"
            updateLocationLabels();        // refresh the stop labels on screen
        });

        // bottom row with two buttons side by side
        HBox bottomRow = new HBox(10); // horizontal row with 10px gap

        // "Delayed" button - marks bus as delayed
        Button delayedBtn = new Button("⚠  Delayed");
        delayedBtn.getStyleClass().add("status-delayed-btn"); // apply delayed button style
        delayedBtn.setMaxWidth(Double.MAX_VALUE);             // stretch to full width
        HBox.setHgrow(delayedBtn, Priority.ALWAYS);           // fill available space
        delayedBtn.setOnAction(e -> {
            updateStatus("Delayed");                          // update status to "Delayed"
            if (assignedBus != null) assignedBus.setStatus("Delayed"); // update bus status too
        });

        // "On Time / Reset" button - marks bus as on time again
        Button onTimeBtn = new Button("✓  On Time / Reset");
        onTimeBtn.getStyleClass().add("status-on-time-btn"); // apply on-time button style
        onTimeBtn.setMaxWidth(Double.MAX_VALUE);             // stretch to full width
        HBox.setHgrow(onTimeBtn, Priority.ALWAYS);           // fill available space
        onTimeBtn.setOnAction(e -> {
            updateStatus("On Time");                         // update status to "On Time"
            if (assignedBus != null) assignedBus.setStatus("Active"); // reset bus status
        });

        bottomRow.getChildren().addAll(delayedBtn, onTimeBtn); // add both buttons to row
        box.getChildren().addAll(departedBtn, arrivedBtn, bottomRow); // add all to VBox
        return box; // return the finished button box
    }

    // updateStatus() - updates the status label and the last-updated time label
    private void updateStatus(String status) {
        currentStatus = status;        // save the new status
        statusLabel.setText(status);   // update the label on screen

        // format the current time and show it in the last updated label
        String time = LocalTime.now().format(DateTimeFormatter.ofPattern("h:mm:ss a"));
        lastUpdatedLabel.setText("⏱  Last updated: " + time);
    }

    // advanceStop() - moves the bus to the next stop along the route
    private void advanceStop() {
        if (myRoute != null && currentStopIndex < myRoute.getStops().size() - 1) {
            currentStopIndex++; // increment the stop index if we are not at the last stop
        }
    }

    // updateLocationLabels() - refreshes the current and next stop labels on screen
    // also updates the bus object in DataStore so passengers can see the change
    private void updateLocationLabels() {
        if (currentLocationLabel != null) currentLocationLabel.setText(getCurrentStop()); // update current stop label
        if (nextStopLabel         != null) nextStopLabel.setText(getNextStop());          // update next stop label
        if (assignedBus != null) {
            assignedBus.setCurrentStop(getCurrentStop()); // update the bus's current stop in DataStore
            assignedBus.setNextStop(getNextStop());       // update the bus's next stop in DataStore
        }
    }

    // getCurrentStop() - returns the name of the stop the bus is currently at
    private String getCurrentStop() {
        if (myRoute == null || myRoute.getStops().isEmpty()) return "—"; // no route data
        List<String> stops = myRoute.getStops();                         // get all stops
        return currentStopIndex < stops.size()                           // if index is valid
            ? stops.get(currentStopIndex)                                // return current stop
            : stops.get(stops.size() - 1);                              // else return last stop
    }

    // getNextStop() - returns the name of the next stop the bus is heading to
    private String getNextStop() {
        if (myRoute == null || myRoute.getStops().isEmpty()) return "—"; // no route data
        List<String> stops = myRoute.getStops();                         // get all stops
        int nextIdx = currentStopIndex + 1;                             // the index of the next stop
        return nextIdx < stops.size()                                    // if next stop exists
            ? stops.get(nextIdx)                                         // return next stop name
            : "Final Stop Reached";                                      // else show this message
    }

    // getRoot() - returns the root BorderPane so it can be placed in a Scene
    public BorderPane getRoot() { return root; }
}
