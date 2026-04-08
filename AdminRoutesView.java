package yatraa.admin; // admin package holds all admin screens

import javafx.geometry.Insets;  // used for padding
import javafx.geometry.Pos;     // used for alignment
import javafx.scene.control.*;  // Button, Label, TextField, Alert, etc.
import javafx.scene.layout.*;   // VBox, HBox, GridPane, Pane, ScrollPane, etc.
import yatraa.model.Route;      // Route data class
import yatraa.ui.UIFactory;     // helper for creating styled UI elements
import yatraa.util.DataStore;   // our data storage class
import java.util.ArrayList;     // used to hold list of stop text fields
import java.util.List;           // List interface

// AdminRoutesView class - the ROUTES tab content for the admin
// Allows the admin to add routes with multiple stops, and delete existing routes
public class AdminRoutesView {

    private ScrollPane root;               // scroll pane wraps everything
    private VBox content;                  // main vertical layout
    private TextField routeNameField;      // input for the route name
    private VBox stopsContainer;           // VBox that holds the stop input fields
    private List<TextField> stopFields = new ArrayList<>(); // list of all stop input fields
    private Label errorLabel;              // red label for validation errors
    private VBox routeList;               // VBox that holds the route cards
    private Route editingRoute = null;    // the route being edited (null if adding new)

    // constructor - builds the UI
    public AdminRoutesView() {
        buildUI(); // build all the UI elements
    }

    // buildUI() - creates the add-route form and the list of existing routes
    private void buildUI() {
        content = new VBox(18);                               // main layout with 18px gap
        content.setPadding(new Insets(24));                   // 24px padding
        content.setStyle("-fx-background-color: #f0f0f0;");  // grey background

        Label title = UIFactory.pageTitle("Manage Routes"); // large page title
        Pane div = UIFactory.divider();                      // thin divider line

        // --- form card for adding a route ---
        VBox formCard = UIFactory.formCard(); // white dashed-border card

        Label formTitle = UIFactory.sectionTitle("Add New Route"); // form heading

        routeNameField = UIFactory.paperField("e.g., Kathmandu - Lalitpur"); // route name input

        stopsContainer = new VBox(8); // VBox to hold the stop input rows, 8px gap
        addStopField();               // add the first stop field by default

        // button to add another stop field
        Button addStopBtn = new Button("+ Add Stop");
        addStopBtn.setStyle(
            "-fx-background-color: #eeeeee; -fx-border-color: #aaaaaa; " +
            "-fx-border-width: 1px; -fx-font-size: 12px; " +
            "-fx-cursor: hand; -fx-padding: 5px 12px;"
        );
        addStopBtn.setOnAction(e -> addStopField()); // add a new stop field when clicked

        errorLabel = UIFactory.errorLabel(""); // error label
        errorLabel.setVisible(false);          // hidden by default

        Button saveBtn = UIFactory.primaryBtn("SAVE ROUTE"); // save button
        saveBtn.setOnAction(e -> handleSave());               // call handleSave() when clicked

        // add all form elements to the form card
        formCard.getChildren().addAll(
            formTitle,
            UIFactory.formLabel("Route Name"), routeNameField, // "ROUTE NAME" + input
            UIFactory.formLabel("Route Stops"), stopsContainer, // "ROUTE STOPS" + stop fields
            addStopBtn, errorLabel, saveBtn                     // add stop btn + error + save
        );

        Label listTitle = UIFactory.sectionTitle("All Routes"); // section heading for route list

        routeList = new VBox(12); // VBox to hold route cards with 12px gap
        refreshRouteList();       // load and show all existing routes

        // add everything to the main content layout
        content.getChildren().addAll(title, div, formCard, listTitle, routeList);
        root = UIFactory.wrappedScroll(content); // wrap in scroll pane
    }

    // addStopField() - adds one numbered stop input field to the stopsContainer
    // called when the form first loads and when admin clicks "+ Add Stop"
    private void addStopField() {
        HBox row = new HBox(8);              // horizontal row with 8px gap
        row.setAlignment(Pos.CENTER_LEFT);   // align to left

        // number label showing which stop this is e.g. "1", "2", "3"
        Label num = new Label(String.valueOf(stopFields.size() + 1));
        num.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-min-width: 24px; -fx-text-fill: #333333;");

        TextField stopField = UIFactory.paperField("Stop name"); // stop name input
        HBox.setHgrow(stopField, Priority.ALWAYS); // stretch field to fill available width
        stopFields.add(stopField);                 // add to our list of stop fields

        row.getChildren().addAll(num, stopField);      // add number and field to row
        stopsContainer.getChildren().add(row);          // add row to the stops container
    }

    // handleSave() - validates form and saves the new or edited route
    private void handleSave() {
        errorLabel.setVisible(false); // hide previous error

        String name = routeNameField.getText().trim(); // get route name

        if (name.isEmpty()) { // check that route name is not empty
            errorLabel.setText("✕  Route name is required."); // show error
            errorLabel.setVisible(true);
            return; // stop here
        }

        // collect all the stop names that are not empty
        List<String> stops = new ArrayList<>();
        for (TextField tf : stopFields) {    // loop through all stop fields
            String s = tf.getText().trim();  // get the text, remove extra spaces
            if (!s.isEmpty()) stops.add(s); // only add if not empty
        }

        // require at least 2 stops for a valid route
        if (stops.size() < 2) {
            errorLabel.setText("✕  At least 2 stops are required."); // show error
            errorLabel.setVisible(true);
            return; // stop here
        }

        if (editingRoute == null) { // adding a new route
            String id = DataStore.generateRouteId();        // generate a new route ID
            DataStore.addRoute(new Route(id, name, stops)); // add the new route
        } else { // editing an existing route
            editingRoute.setRouteName(name);    // update the route name
            editingRoute.getStops().clear();    // clear the old stops
            stops.forEach(editingRoute::addStop); // add each new stop
            editingRoute = null;                // clear editing state
        }

        clearForm();       // clear the form fields
        refreshRouteList(); // reload the route list
    }

    // refreshRouteList() - clears and reloads the route card list from DataStore
    private void refreshRouteList() {
        routeList.getChildren().clear(); // clear existing cards
        for (Route route : DataStore.getRoutes()) {          // loop through all routes
            routeList.getChildren().add(buildRouteCard(route)); // build card for each route
        }
    }

    // buildRouteCard() - creates a card showing one route's details with Edit and Delete buttons
    private VBox buildRouteCard(Route route) {
        VBox card = new VBox(10);                  // vertical layout with 10px gap
        card.getStyleClass().add("bus-card");       // apply white bordered card style

        // --- top row: route ID on left, stop count badge on right ---
        HBox topRow = new HBox();
        topRow.setAlignment(Pos.CENTER_LEFT); // align to left

        Label idLabel = new Label(route.getRouteId()); // route ID e.g. "R-001"
        idLabel.getStyleClass().add("bus-id-label");   // bold large style

        Region spacer = new Region();               // spacer pushes badge to the right
        HBox.setHgrow(spacer, Priority.ALWAYS);     // spacer fills available space

        Label stopCount = new Label(route.getStops().size() + " stops"); // stop count badge
        stopCount.getStyleClass().add("badge-active"); // apply badge style

        topRow.getChildren().addAll(idLabel, spacer, stopCount); // add ID, spacer, badge

        Label nameLabel = new Label(route.getRouteName()); // route name e.g. "Kathmandu - Bhaktapur"
        nameLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #222222;");

        Label stopsLbl = new Label("Stops: " + route.getStopsDisplay()); // all stops joined with →
        stopsLbl.getStyleClass().add("stops-label"); // small grey text style
        stopsLbl.setWrapText(true);                  // wrap to next line if too long

        // --- action buttons: Edit and Delete ---
        HBox actions = new HBox(10); // horizontal row with 10px gap

        Button editBtn = UIFactory.secondaryBtn("✎ Edit"); // edit button
        editBtn.setOnAction(e -> startEdit(route));         // fill form with this route's data

        Button deleteBtn = UIFactory.dangerBtn("⌫ Delete"); // delete button
        deleteBtn.setOnAction(e -> deleteRoute(route));      // delete this route

        actions.getChildren().addAll(editBtn, deleteBtn); // add both buttons

        card.getChildren().addAll(topRow, nameLabel, stopsLbl, UIFactory.divider(), actions);
        return card; // return the finished route card
    }

    // startEdit() - fills the form with the route's current data for editing
    private void startEdit(Route route) {
        editingRoute = route;                    // remember which route we are editing
        routeNameField.setText(route.getRouteName()); // fill the route name field

        stopsContainer.getChildren().clear(); // clear existing stop fields from the container
        stopFields.clear();                   // clear the stop fields list too

        for (String stop : route.getStops()) { // loop through each stop of the route
            addStopField();                    // add a new stop input field
            stopFields.get(stopFields.size() - 1).setText(stop); // fill it with the stop name
        }
    }

    // deleteRoute() - shows confirmation then deletes the route
    private void deleteRoute(Route route) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION); // YES/NO popup
        confirm.setTitle("Delete Route?");
        confirm.setContentText("Delete " + route.getRouteId() + ": " + route.getRouteName() + "?");

        confirm.showAndWait().ifPresent(r -> { // wait for user response
            if (r == ButtonType.OK) {          // if user clicked OK
                DataStore.removeRoute(route.getRouteId()); // remove the route
                refreshRouteList();                         // reload the list
            }
        });
    }

    // clearForm() - clears all form inputs and resets stop fields to one empty field
    private void clearForm() {
        routeNameField.clear();           // clear route name field
        stopsContainer.getChildren().clear(); // remove all stop field rows
        stopFields.clear();               // clear the list of stop fields
        addStopField();                   // add one empty stop field back
        errorLabel.setVisible(false);     // hide error
    }

    // refresh() - called by AdminDashboard when switching to this tab
    public void refresh() { refreshRouteList(); } // reload route list

    // getRoot() - returns the scroll pane to be shown in the content area
    public ScrollPane getRoot() { return root; }
}
