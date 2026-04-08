package yatraa.admin; // admin package holds all admin screens

import javafx.geometry.Insets;  // used for padding
import javafx.geometry.Pos;     // used for alignment
import javafx.scene.control.*;  // Button, Label, TextField, Alert, etc.
import javafx.scene.layout.*;   // VBox, HBox, GridPane, Pane, ScrollPane, etc.
import yatraa.model.Bus;        // Bus data class
import yatraa.ui.UIFactory;     // helper for creating styled UI elements
import yatraa.util.DataStore;   // our data storage class

// AdminBusesView class - the BUSES tab content for the admin
// Allows the admin to add, edit, and delete buses
public class AdminBusesView {

    private ScrollPane root;       // scroll pane wraps everything so admin can scroll
    private VBox content;          // main vertical layout inside the scroll pane
    private TextField busIdField;  // input for bus ID e.g. "BUS-106"
    private TextField modelField;  // input for bus model e.g. "Ashok Leyland"
    private TextField capacityField; // input for seat capacity e.g. "45"
    private TextField regField;    // input for registration number e.g. "BA 2 KHA 5678"
    private Label errorLabel;      // red label for showing validation errors
    private VBox busList;          // VBox that holds the list of bus cards
    private Bus editingBus = null; // stores the bus being edited (null if adding new)

    // constructor - builds the UI when this object is created
    public AdminBusesView() {
        buildUI(); // build all the UI elements
    }

    // buildUI() - creates the form for adding buses and the list of existing buses
    private void buildUI() {
        content = new VBox(18);                               // main vertical layout, 18px gap
        content.setPadding(new Insets(24));                   // 24px padding around edges
        content.setStyle("-fx-background-color: #f0f0f0;");  // grey background

        Label title = UIFactory.pageTitle("Manage Buses"); // large page title
        Pane div = UIFactory.divider();                      // thin horizontal line

        // --- form card for adding/editing a bus ---
        VBox formCard = UIFactory.formCard(); // white dashed-border card

        Label formTitle = UIFactory.sectionTitle("Add New Bus"); // form heading
        formTitle.setStyle("-fx-font-size: 12px; -fx-font-weight: bold;");

        busIdField    = UIFactory.paperField("e.g., BUS-106");      // bus ID input field
        modelField    = UIFactory.paperField("e.g., Ashok Leyland"); // model input field
        capacityField = UIFactory.paperField("e.g., 45");            // capacity input field
        regField      = UIFactory.paperField("e.g., BA 2 KHA 5678"); // registration input field

        errorLabel = UIFactory.errorLabel(""); // error label starts empty
        errorLabel.setVisible(false);          // hidden until there is an error

        Button saveBtn = UIFactory.primaryBtn("SAVE BUS"); // save button
        saveBtn.setOnAction(e -> handleSave());             // call handleSave() when clicked

        // add all form elements to the form card in order
        formCard.getChildren().addAll(
            formTitle,
            UIFactory.formLabel("Bus ID"),               busIdField,    // "BUS ID" label + field
            UIFactory.formLabel("Model"),                modelField,    // "MODEL" label + field
            UIFactory.formLabel("Capacity (Seats)"),     capacityField, // "CAPACITY" label + field
            UIFactory.formLabel("Registration Number"),  regField,      // "REGISTRATION" label + field
            errorLabel, saveBtn                                         // error + save button
        );

        Label listTitle = UIFactory.sectionTitle("Registered Buses"); // section heading for the list

        busList = new VBox(12); // VBox to hold bus cards with 12px gap
        refreshBusList();       // load and display all existing buses

        // add everything to the main content layout
        content.getChildren().addAll(title, div, formCard, listTitle, busList);

        root = UIFactory.wrappedScroll(content); // wrap content in a scroll pane
    }

    // refreshBusList() - clears and reloads the bus card list from DataStore
    private void refreshBusList() {
        busList.getChildren().clear(); // remove all existing cards
        for (Bus bus : DataStore.getBuses()) {          // loop through every bus
            busList.getChildren().add(buildBusCard(bus)); // create a card and add to list
        }
    }

    // buildBusCard() - creates a card showing one bus's details with Edit and Delete buttons
    private VBox buildBusCard(Bus bus) {
        VBox card = new VBox(10);                   // vertical layout with 10px gap
        card.getStyleClass().add("bus-card");        // apply white bordered card style

        // --- top row: bus ID on left, status badge on right ---
        HBox topRow = new HBox();
        topRow.setAlignment(Pos.CENTER_LEFT);        // align to left

        Label idLabel = new Label(bus.getBusId());   // bus ID label e.g. "BUS-101"
        idLabel.getStyleClass().add("bus-id-label"); // bold large style

        Region spacer = new Region();                // spacer pushes badge to the right
        HBox.setHgrow(spacer, Priority.ALWAYS);      // spacer fills available space

        Label statusBadge = UIFactory.badge(bus.getStatus(), bus.getStatus().toLowerCase()); // status badge
        topRow.getChildren().addAll(idLabel, spacer, statusBadge); // add ID, spacer, badge

        Label modelLabel = new Label(bus.getModel());     // model name label e.g. "Ashok Leyland Viking"
        modelLabel.getStyleClass().add("bus-model-label"); // small grey style

        // --- details row: capacity and registration side by side ---
        HBox metaRow = new HBox(20); // horizontal row with 20px gap

        VBox capBox = new VBox(2); // VBox for capacity label + value
        Label capMeta = new Label("CAPACITY");     // small uppercase label
        capMeta.getStyleClass().add("meta-label"); // grey small text style
        Label capVal = new Label(bus.getCapacity() + " seats"); // value e.g. "45 seats"
        capVal.getStyleClass().add("value-label");              // bold dark text style
        capBox.getChildren().addAll(capMeta, capVal);           // add label and value

        VBox regBox = new VBox(2); // VBox for registration label + value
        Label regMeta = new Label("REGISTRATION"); // small uppercase label
        regMeta.getStyleClass().add("meta-label"); // grey small text style
        Label regVal = new Label(bus.getRegistrationNumber()); // value e.g. "BA 2 KHA 5678"
        regVal.getStyleClass().add("value-label");              // bold dark text style
        regBox.getChildren().addAll(regMeta, regVal);           // add label and value

        metaRow.getChildren().addAll(capBox, regBox); // add both columns to the row

        // --- action buttons: Edit and Delete ---
        HBox actions = new HBox(10); // horizontal row with 10px gap

        Button editBtn = UIFactory.secondaryBtn("✎ Edit"); // edit button
        editBtn.setOnAction(e -> startEdit(bus));           // call startEdit() with this bus

        Button deleteBtn = UIFactory.dangerBtn("⌫ Delete"); // delete button
        deleteBtn.setOnAction(e -> deleteBus(bus));          // call deleteBus() with this bus

        actions.getChildren().addAll(editBtn, deleteBtn); // add both buttons

        // add all sections to the card
        card.getChildren().addAll(topRow, modelLabel, metaRow, UIFactory.divider(), actions);
        return card; // return the finished bus card
    }

    // handleSave() - called when admin clicks SAVE BUS
    // validates the form and either adds a new bus or updates an existing one
    private void handleSave() {
        errorLabel.setVisible(false); // hide previous error

        String id    = busIdField.getText().trim();    // get bus ID input
        String model = modelField.getText().trim();    // get model input
        String capStr = capacityField.getText().trim(); // get capacity input
        String reg   = regField.getText().trim();      // get registration input

        // check all fields are filled in
        if (id.isEmpty() || model.isEmpty() || capStr.isEmpty() || reg.isEmpty()) {
            errorLabel.setText("✕  All fields are required."); // show error
            errorLabel.setVisible(true);
            return; // stop here
        }

        // try to convert capacity to an integer
        int capacity;
        try {
            capacity = Integer.parseInt(capStr);         // convert "45" to 45
            if (capacity <= 0) throw new NumberFormatException(); // must be positive
        } catch (NumberFormatException e) {
            errorLabel.setText("✕  Capacity must be a positive number."); // show error
            errorLabel.setVisible(true);
            return; // stop here
        }

        if (editingBus == null) { // we are adding a new bus (not editing)
            if (DataStore.busIdExists(id)) { // check if the bus ID is already taken
                errorLabel.setText("✕  Bus ID already exists."); // show error
                errorLabel.setVisible(true);
                return; // stop here
            }
            DataStore.addBus(new Bus(id, model, capacity, reg)); // add the new bus
        } else { // we are editing an existing bus
            editingBus.setBusId(id);                 // update the bus ID
            editingBus.setModel(model);               // update the model
            editingBus.setCapacity(capacity);         // update the capacity
            editingBus.setRegistrationNumber(reg);    // update the registration
            editingBus = null;                        // clear the editing state
        }

        clearForm();       // clear all the form fields
        refreshBusList();  // reload the bus list to show changes
    }

    // startEdit() - fills the form with the bus's current data for editing
    private void startEdit(Bus bus) {
        editingBus = bus;                                      // remember which bus we are editing
        busIdField.setText(bus.getBusId());                    // fill bus ID field
        modelField.setText(bus.getModel());                    // fill model field
        capacityField.setText(String.valueOf(bus.getCapacity())); // fill capacity field
        regField.setText(bus.getRegistrationNumber());         // fill registration field
    }

    // deleteBus() - shows a confirmation dialog then deletes the bus if confirmed
    private void deleteBus(Bus bus) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION); // create a YES/NO popup
        confirm.setTitle("Confirm Delete");                       // popup title
        confirm.setHeaderText("Delete " + bus.getBusId() + "?"); // popup header
        confirm.setContentText("This action cannot be undone."); // warning message

        confirm.showAndWait().ifPresent(result -> { // wait for user to click OK or Cancel
            if (result == ButtonType.OK) {          // if user clicked OK
                DataStore.removeBus(bus.getBusId()); // remove the bus from data store
                refreshBusList();                    // reload the list
            }
        });
    }

    // clearForm() - clears all input fields in the form
    private void clearForm() {
        busIdField.clear();    // clear bus ID field
        modelField.clear();    // clear model field
        capacityField.clear(); // clear capacity field
        regField.clear();      // clear registration field
        errorLabel.setVisible(false); // hide error label
    }

    // refresh() - called by AdminDashboard when switching to this tab
    public void refresh() { refreshBusList(); } // reload the bus list

    // getRoot() - returns the scroll pane to be shown in the content area
    public ScrollPane getRoot() { return root; }
}
