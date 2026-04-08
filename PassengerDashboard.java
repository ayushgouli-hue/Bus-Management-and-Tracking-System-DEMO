package yatraa.passenger; // passenger package holds the passenger screen

import javafx.animation.KeyFrame;        // used to create a repeating action every second
import javafx.animation.Timeline;        // used to run the KeyFrame on a timer
import javafx.geometry.Insets;           // used for padding
import javafx.geometry.Pos;              // used for alignment
import javafx.scene.control.*;           // Button, Label, etc.
import javafx.scene.layout.*;            // VBox, HBox, BorderPane, GridPane, Region, Pane, etc.
import javafx.util.Duration;             // used to set the timer interval e.g. 1 second
import yatraa.Main;                      // to switch screens (logout)
import yatraa.model.Bus;                 // Bus data class
import yatraa.model.Route;               // Route data class
import yatraa.ui.UIFactory;              // helper for creating styled UI elements
import yatraa.util.DataStore;            // our data storage class
import java.time.LocalTime;              // used to get the current system time
import java.time.format.DateTimeFormatter; // used to format the time as readable text

// PassengerDashboard class - the main screen for passengers
// Shows all active buses with their current stop, next stop, and estimated arrival
// The bus list refreshes automatically every 5 seconds
public class PassengerDashboard {

    private BorderPane root;           // main layout: header at top, content in center
    private VBox busListContainer;     // VBox that holds all the bus cards
    private Label timeLabel;           // label showing the current time
    private Label countdownLabel;      // label showing seconds until next refresh
    private Timeline autoRefresh;      // the timer that refreshes the list every 5 seconds
    private int countdown = 5;         // countdown starts at 5 seconds

    // constructor - builds the UI and starts the auto-refresh timer
    public PassengerDashboard() {
        buildUI();         // build all the screen elements
        startAutoRefresh(); // start the 5-second refresh timer
    }

    // buildUI() - creates the full screen layout with header and content
    private void buildUI() {
        root = new BorderPane();                             // main layout container
        root.setStyle("-fx-background-color: #f0f0f0;");    // grey background
        root.setTop(buildHeader());                          // header bar at top
        root.setCenter(buildContent());                      // scrollable content in center
    }

    // buildHeader() - creates the top dark bar with app name, Passenger label, and logout button
    private HBox buildHeader() {
        HBox header = new HBox();                         // horizontal layout for header
        header.getStyleClass().add("header-bar");         // apply dark header CSS style
        header.setAlignment(Pos.CENTER_LEFT);             // align items to the left

        VBox titleBox = new VBox(2);                      // VBox for app name + role label
        Label appLabel = new Label("YAATRI");             // app name shown in header
        appLabel.getStyleClass().add("header-title");     // white bold style
        Label roleLabel = new Label("Passenger");         // role label below app name
        roleLabel.getStyleClass().add("header-subtitle"); // grey small style
        titleBox.getChildren().addAll(appLabel, roleLabel); // add both to titleBox

        Region spacer = new Region();                     // spacer to push avatar to the right
        HBox.setHgrow(spacer, Priority.ALWAYS);           // spacer grows to fill available space

        Button avatarBtn = new Button("P");               // avatar button showing "P" for Passenger
        avatarBtn.getStyleClass().add("header-avatar");   // apply circular button style
        avatarBtn.setOnAction(e -> {
            DataStore.setCurrentUser(null); // clear the logged-in user (logout)
            Main.showLogin();               // go back to the login screen
        });

        header.getChildren().addAll(titleBox, spacer, avatarBtn); // add all items to header
        return header; // return the finished header
    }

    // buildContent() - creates the scrollable content with refresh bar and bus list
    private ScrollPane buildContent() {
        VBox content = new VBox(20);                             // vertical layout with 20px gap
        content.setPadding(new Insets(24));                      // 24px padding around edges
        content.setStyle("-fx-background-color: #f0f0f0;");     // grey background

        Label title = UIFactory.pageTitle("Live Bus Information"); // large page title
        Pane div = UIFactory.divider();                            // thin divider line

        HBox refreshBar = buildRefreshBar(); // build the refresh info bar

        // current time label shown next to the bus count
        timeLabel = new Label(getCurrentTime()); // show current time
        timeLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #888888;"); // grey small text

        // row showing "Active Buses (3)" on the left and current time on the right
        HBox headerRow = new HBox();
        headerRow.setAlignment(Pos.CENTER_LEFT); // align to left

        Region spacer = new Region();              // spacer to push time label to the right
        HBox.setHgrow(spacer, Priority.ALWAYS);    // spacer fills available space

        headerRow.getChildren().addAll(
            UIFactory.sectionTitle("Active Buses (" + DataStore.getBuses().size() + ")"), // bus count heading
            spacer,     // push time to the right
            timeLabel   // current time
        );

        busListContainer = new VBox(14); // VBox to hold bus cards with 14px gap
        refreshBusList();                // load and display all active buses

        content.getChildren().addAll(title, div, refreshBar, headerRow, busListContainer);
        return UIFactory.wrappedScroll(content); // wrap in scroll pane so user can scroll
    }

    // buildRefreshBar() - creates the bar showing auto-refresh info and a manual refresh button
    private HBox buildRefreshBar() {
        HBox bar = new HBox(10); // horizontal layout with 10px gap
        bar.setStyle(
            "-fx-background-color: #e8e8e8; -fx-border-color: #cccccc;" +
            "-fx-border-width: 1px; -fx-padding: 10px 14px;" // light grey bar with border
        );
        bar.setAlignment(Pos.CENTER_LEFT); // align content to the left

        VBox infoBox = new VBox(2); // VBox for the two info labels

        Label refreshInfo = new Label("⟳  Updates every 5 seconds"); // info text
        refreshInfo.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #333333;");

        countdownLabel = new Label("Next refresh in " + countdown + "s"); // countdown text
        countdownLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #888888;"); // grey small text

        infoBox.getChildren().addAll(refreshInfo, countdownLabel); // add both labels

        Region spacer = new Region();              // spacer to push button to the right
        HBox.setHgrow(spacer, Priority.ALWAYS);    // spacer fills available space

        // manual refresh button
        Button refreshBtn = new Button("Refresh Now");
        refreshBtn.setStyle(
            "-fx-background-color: #ffffff; -fx-border-color: #aaaaaa; -fx-border-width: 1px;" +
            "-fx-font-size: 11px; -fx-font-weight: bold; -fx-cursor: hand; -fx-padding: 5px 12px;"
        );
        refreshBtn.setOnAction(e -> {
            refreshBusList(); // manually refresh the bus list right now
            countdown = 5;    // reset the countdown back to 5 seconds
        });

        bar.getChildren().addAll(infoBox, spacer, refreshBtn); // add info, spacer, and button
        return bar; // return the finished refresh bar
    }

    // refreshBusList() - clears and reloads the bus list from DataStore
    // only shows buses that are NOT inactive
    private void refreshBusList() {
        busListContainer.getChildren().clear();   // remove all existing bus cards
        timeLabel.setText(getCurrentTime());       // update the time label

        for (Bus bus : DataStore.getBuses()) {                    // loop through every bus
            if (!bus.getStatus().equals("Inactive")) {            // skip inactive buses
                busListContainer.getChildren().add(buildBusCard(bus)); // add card for active buses
            }
        }
    }

    // buildBusCard() - creates a card showing one bus's live information
    // shows: bus ID, status badge, route name, current stop, next stop, estimated arrival
    private VBox buildBusCard(Bus bus) {
        VBox card = new VBox(12);                  // vertical layout with 12px gap
        card.getStyleClass().add("bus-card");       // apply white bordered card style

        // change the left border color based on bus status
        String status = bus.getStatus(); // get the current bus status
        String leftBorderColor =
            status.equals("Delayed")    ? "#888888" : // darker grey for delayed
            status.equals("In Transit") ? "#aaaaaa" : // medium grey for in transit
                                          "#cccccc";  // light grey for active/other

        card.setStyle(
            "-fx-background-color: #ffffff;" +
            "-fx-border-color: " + leftBorderColor + " #cccccc #cccccc #cccccc;" + // thicker left border
            "-fx-border-width: 1px 1px 1px 4px;" +  // 4px on left, 1px on other sides
            "-fx-padding: 16px;"
        );

        // --- top row: bus ID on left, status badge on right ---
        HBox topRow = new HBox();
        topRow.setAlignment(Pos.CENTER_LEFT); // align to left

        Label busIdLbl = new Label(bus.getBusId()); // bus ID e.g. "BUS-101"
        busIdLbl.getStyleClass().add("bus-id-label"); // bold large style

        Region spacer = new Region();               // spacer to push badge to the right
        HBox.setHgrow(spacer, Priority.ALWAYS);     // spacer fills available space

        Label badge = UIFactory.badge(status, status.toLowerCase()); // status badge
        topRow.getChildren().addAll(busIdLbl, spacer, badge); // add ID, spacer, badge

        // --- route name below the bus ID ---
        Route route = bus.getAssignedRouteId() != null      // check if bus has a route
            ? DataStore.getRouteById(bus.getAssignedRouteId()) // find the Route object
            : null;                                             // null if no route assigned

        Label routeLabel = new Label(route != null ? route.getRouteName() : "—"); // route name or dash
        routeLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #666666;"); // grey style

        Pane divider = UIFactory.divider(); // thin divider line inside the card

        // --- info grid: current stop, next stop, estimated arrival ---
        GridPane grid = new GridPane();
        grid.setHgap(20); // 20px between columns
        grid.setVgap(8);  // 8px between rows

        // set 2 equal columns
        ColumnConstraints c1 = new ColumnConstraints(); c1.setPercentWidth(50); // left 50%
        ColumnConstraints c2 = new ColumnConstraints(); c2.setPercentWidth(50); // right 50%
        grid.getColumnConstraints().addAll(c1, c2); // apply column constraints

        // get the values, show "—" if data is not available
        String currentStop = bus.getCurrentStop()    != null ? bus.getCurrentStop()    : "—";
        String nextStop    = bus.getNextStop()        != null ? bus.getNextStop()        : "—";
        String eta         = bus.getEstimatedArrival()!= null ? bus.getEstimatedArrival(): "—";

        addInfoRow(grid, "📍 Current Stop",      currentStop, 0, 0); // current stop at col 0 row 0
        addInfoRow(grid, "→ Next Stop",           nextStop,    1, 0); // next stop at col 1 row 0
        addInfoRow(grid, "⏱ Estimated Arrival",  eta,         0, 1); // ETA at col 0 row 1

        card.getChildren().addAll(topRow, routeLabel, divider, grid); // add all to card
        return card; // return the finished bus card
    }

    // addInfoRow() - helper to add a label+value cell to the grid at a specific position
    // label is the small header e.g. "📍 Current Stop", value is the content e.g. "Koteshwor"
    private void addInfoRow(GridPane grid, String label, String value, int col, int row) {
        VBox cell = new VBox(3);                    // VBox with 3px gap

        Label lbl = new Label(label);               // the small label
        lbl.getStyleClass().add("meta-label");      // grey small text style

        Label val = new Label(value);               // the value text
        val.getStyleClass().add("value-label");     // bold dark text style
        val.setWrapText(true);                      // wrap if text is too long

        cell.getChildren().addAll(lbl, val);        // add label and value to cell
        grid.add(cell, col, row);                   // add the cell to the grid at col/row
    }

    // startAutoRefresh() - starts a 1-second timer that counts down from 5
    // every 5 seconds it refreshes the bus list automatically
    private void startAutoRefresh() {
        autoRefresh = new Timeline(                // create a timeline (repeating timer)
            new KeyFrame(Duration.seconds(1), e -> { // run this every 1 second
                countdown--;                          // subtract 1 from the countdown
                countdownLabel.setText("Next refresh in " + countdown + "s"); // update label

                if (countdown <= 0) { // if countdown reached zero
                    countdown = 5;    // reset countdown back to 5
                    refreshBusList(); // refresh the bus list
                }
            })
        );
        autoRefresh.setCycleCount(Timeline.INDEFINITE); // run forever (until app closes)
        autoRefresh.play();                             // start the timer

        // stop the timer if this screen is removed from the window
        // this prevents the timer running in the background after logout
        root.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene == null) autoRefresh.stop(); // stop timer when screen is removed
        });
    }

    // getCurrentTime() - returns the current time as a formatted string e.g. "3:45:22 PM"
    private String getCurrentTime() {
        return LocalTime.now().format(DateTimeFormatter.ofPattern("h:mm:ss a")); // format time
    }

    // getRoot() - returns the root BorderPane so it can be placed in a Scene
    public BorderPane getRoot() { return root; }
}
