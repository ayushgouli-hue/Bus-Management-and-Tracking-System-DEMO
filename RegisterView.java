package yatraa.auth; // this file belongs to the auth package which handles login and register

import javafx.geometry.Insets;   // Insets is used to add padding (space) around elements
import javafx.geometry.Pos;      // Pos is used to align elements like CENTER, TOP_LEFT etc.
import javafx.scene.Scene;       // Scene is the container that holds everything shown in the window
import javafx.scene.control.*;   // imports Button, Label, TextField, ComboBox, CheckBox, Alert, ScrollPane
import javafx.scene.layout.*;    // imports VBox, HBox, GridPane, Pane, ColumnConstraints etc.
import javafx.scene.text.Text;   // Text is used for plain unformatted text inside layouts
import yatraa.Main;              // Main gives us access to the primary window (Stage) to switch screens
import yatraa.model.User;        // User is the data class that holds one person's info
import yatraa.ui.UIFactory;      // UIFactory has helper methods that create pre-styled UI elements
import yatraa.util.Database;     // Database gives us getConnection() to talk to MySQL

import java.sql.Connection;      // Connection represents a live link to the MySQL database
import java.sql.PreparedStatement; // PreparedStatement lets us write safe SQL queries with ? placeholders
import java.sql.ResultSet;       // ResultSet holds the rows returned after running a SELECT query
import java.sql.SQLException;    // SQLException is the error thrown when a database operation fails

// RegisterView class — builds and manages the Register screen
// when a user wants to create a new account, this screen is shown
public class RegisterView {

    private VBox root;               // root is the outermost container — everything goes inside this
    private TextField nameField;     // text input for the user's full name
    private TextField contactField;  // text input for email or address
    private TextField ageField;      // text input for age (we convert it to int later)
    private TextField phoneField;    // text input for phone number (used as login ID)
    private ComboBox<String> sexCombo;  // dropdown menu to select Male / Female / Other
    private ComboBox<String> roleCombo; // dropdown menu to select passenger or driver
    private PasswordField passwordField; // password input — hides characters as the user types
    private CheckBox termsCheck;     // checkbox the user must tick before registering
    private Label errorLabel;        // red label shown when something goes wrong

    // constructor — called when someone does: new RegisterView()
    // it immediately calls buildUI() to create all the visual elements
    public RegisterView() {
        buildUI(); // build the entire screen layout
    }

    // buildUI() — creates every visual element on the register screen
    // this method sets up the layout, fields, buttons and wires everything together
    private void buildUI() {

        // ── outer background container ────────────────────────
        root = new VBox();                               // VBox stacks children vertically one by one
        root.setStyle("-fx-background-color: #f0f0f0;"); // set the background to light grey
        root.setAlignment(Pos.CENTER);                   // center all children horizontally and vertically

        // ── white card in the middle that holds the form ──────
        VBox card = new VBox(16);                  // VBox with 16px gap between each child element
        card.getStyleClass().add("main-card");      // apply CSS class "main-card" — white box with shadow
        card.setMaxWidth(460);                      // card will not grow wider than 460 pixels
        card.setPadding(new Insets(28));            // add 28px of inner space on all 4 sides

        // ── header section: title and subtitle ───────────────
        VBox headerBox = new VBox(4);              // VBox with 4px gap between title and subtitle
        headerBox.setAlignment(Pos.CENTER);         // center title and subtitle text

        Label title = new Label("REGISTER");        // large bold title shown at the top of the card
        title.getStyleClass().add("logo-label");    // apply CSS class for large dark blue bold text

        Label sub = new Label("CREATE YOUR ACCOUNT"); // smaller subtitle below the title
        sub.getStyleClass().add("logo-sub");          // apply CSS class for small grey subtitle text

        headerBox.getChildren().addAll(title, sub); // add title then subtitle into the header VBox

        Pane div = UIFactory.divider(); // create a thin horizontal grey line to separate the header

        // ── Row 1: Full Name and Contact Detail side by side ──
        GridPane grid1 = new GridPane(); // GridPane arranges children in a table of rows and columns
        grid1.setHgap(12); // set 12px horizontal gap between the two columns
        grid1.setVgap(8);  // set 8px vertical gap between the label row and the field row

        ColumnConstraints c1 = new ColumnConstraints(); // create constraint for the left column
        c1.setPercentWidth(50);                         // left column takes 50% of available width
        ColumnConstraints c2 = new ColumnConstraints(); // create constraint for the right column
        c2.setPercentWidth(50);                         // right column takes the other 50%
        grid1.getColumnConstraints().addAll(c1, c2);    // apply both constraints to the grid

        nameField    = UIFactory.paperField("e.g., Ram Shrestha");     // styled text field for full name
        contactField = UIFactory.paperField("e.g., email or address"); // styled text field for contact

        grid1.add(UIFactory.formLabel("Full Name"),      0, 0); // "FULL NAME" label at column 0, row 0
        grid1.add(UIFactory.formLabel("Contact Detail"), 1, 0); // "CONTACT DETAIL" label at column 1, row 0
        grid1.add(nameField,    0, 1); // full name input field at column 0, row 1 (below its label)
        grid1.add(contactField, 1, 1); // contact input field at column 1, row 1 (below its label)

        // ── Row 2: Age and Sex side by side ───────────────────
        GridPane grid2 = new GridPane(); // another grid for the second row of fields
        grid2.setHgap(12); // 12px horizontal gap between columns
        grid2.setVgap(8);  // 8px vertical gap between label and field rows
        grid2.getColumnConstraints().addAll(c1, c2); // reuse same 50/50 column constraints as grid1

        ageField = UIFactory.paperField("e.g., 25"); // styled text field for age input

        sexCombo = UIFactory.paperCombo();            // styled dropdown (ComboBox) for sex selection
        sexCombo.getItems().addAll("Male", "Female", "Other", "Prefer not to say"); // add all options
        sexCombo.setPromptText("Select sex");         // grey placeholder text shown before selection

        grid2.add(UIFactory.formLabel("Age"), 0, 0); // "AGE" label at column 0, row 0
        grid2.add(UIFactory.formLabel("Sex"), 1, 0); // "SEX" label at column 1, row 0
        grid2.add(ageField,  0, 1); // age text field at column 0, row 1
        grid2.add(sexCombo,  1, 1); // sex dropdown at column 1, row 1

        // ── Row 3: Phone Number and Password side by side ─────
        GridPane grid3 = new GridPane(); // third grid for phone and password
        grid3.setHgap(12); // 12px horizontal gap between columns
        grid3.setVgap(8);  // 8px vertical gap between label and field rows
        grid3.getColumnConstraints().addAll(c1, c2); // reuse the same 50/50 column layout

        phoneField    = UIFactory.paperField("e.g., 9800000000");  // styled text field for phone number
        passwordField = UIFactory.paperPassword("Min. 6 characters"); // styled password field (hides text)

        grid3.add(UIFactory.formLabel("Phone Number"), 0, 0); // "PHONE NUMBER" label at column 0, row 0
        grid3.add(UIFactory.formLabel("Password"),     1, 0); // "PASSWORD" label at column 1, row 0
        grid3.add(phoneField,    0, 1); // phone field at column 0, row 1
        grid3.add(passwordField, 1, 1); // password field at column 1, row 1

        // ── Role dropdown: passenger or driver ────────────────
        VBox roleBox = new VBox(6); // small VBox with 6px gap to hold the label and dropdown
        roleCombo = UIFactory.paperCombo();          // styled dropdown for role selection
        roleCombo.getItems().addAll("passenger", "driver"); // admins cannot self-register
        roleCombo.setPromptText("Select role");      // grey placeholder text before selection
        roleBox.getChildren().addAll(UIFactory.formLabel("Account Role"), roleCombo); // label on top, dropdown below

        // ── Terms and Conditions checkbox ─────────────────────
        termsCheck = UIFactory.paperCheck("I agree to the Terms and Conditions"); // must be ticked to register

        // ── Error label (hidden until something goes wrong) ───
        errorLabel = UIFactory.errorLabel(""); // create a red label with no text yet
        errorLabel.setVisible(false);          // hide it — only shown when there is a validation error

        // ── Register button ───────────────────────────────────
        Button registerBtn = UIFactory.primaryBtn("REGISTER HERE"); // big dark blue button
        registerBtn.setOnAction(e -> handleRegister()); // when clicked, call handleRegister()

        // ── "Already have an account? Login here" row ─────────
        HBox loginRow = new HBox(4);              // HBox puts text and link side by side with 4px gap
        loginRow.setAlignment(Pos.CENTER);         // center the row horizontally

        Text hasAccount = new Text("Already have an account? "); // plain grey text on the left
        hasAccount.setStyle("-fx-font-size: 12px; -fx-fill: #555555;"); // small grey font

        Label loginLink = UIFactory.linkLabel("Login here"); // clickable blue underlined link
        loginLink.setOnMouseClicked(e -> goToLogin());        // when clicked, go back to login screen

        loginRow.getChildren().addAll(hasAccount, loginLink); // add the text and link into the row

        // ── Assemble the card — add all sections in order ─────
        card.getChildren().addAll(
            headerBox,    // REGISTER title + CREATE YOUR ACCOUNT subtitle
            div,          // thin horizontal divider line
            grid1,        // Full Name + Contact Detail row
            grid2,        // Age + Sex row
            grid3,        // Phone Number + Password row
            roleBox,      // Account Role dropdown
            termsCheck,   // I agree to Terms checkbox
            errorLabel,   // red error message (hidden by default)
            registerBtn,  // REGISTER HERE button
            loginRow      // "Already have an account? Login here"
        );

        // ── Wrap the card in a ScrollPane so user can scroll on small screens ──
        ScrollPane scroll = UIFactory.wrappedScroll(card); // ScrollPane makes content scrollable
        scroll.setStyle("-fx-background-color: #f0f0f0;"); // match the outer background colour

        root.getChildren().add(scroll); // add the scroll pane into the root container
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  handleRegister() — called when user clicks REGISTER HERE button
    //  validates every field then saves the new user directly into MySQL
    // ─────────────────────────────────────────────────────────────────────────
    private void handleRegister() {
        errorLabel.setVisible(false); // hide any error from a previous attempt

        // read every field the user filled in and remove extra spaces with trim()
        String name     = nameField.getText().trim();     // user's full name
        String contact  = contactField.getText().trim();  // email or address
        String ageStr   = ageField.getText().trim();      // age as a raw string — convert to int below
        String sex      = sexCombo.getValue();            // selected option from the sex dropdown
        String phone    = phoneField.getText().trim();    // phone number — used as login username
        String password = passwordField.getText().trim(); // password the user chose
        String role     = roleCombo.getValue();           // selected role: "passenger" or "driver"

        // ── Validation 1: make sure no required field is empty ──
        if (name.isEmpty() || phone.isEmpty() || password.isEmpty() || sex == null || role == null) {
            showError("✕  All fields are required."); // show red error message on screen
            return; // stop here — do not continue to database
        }

        // ── Validation 2: user must tick the terms checkbox ────
        if (!termsCheck.isSelected()) {
            showError("✕  You must agree to the Terms and Conditions."); // show error
            return; // stop here
        }

        // ── Validation 3: convert age string to integer and check valid range ──
        int age = 0; // will hold the converted integer age
        try {
            age = Integer.parseInt(ageStr);              // try to convert e.g. "25" → 25
            if (age < 1 || age > 120) throw new NumberFormatException(); // reject invalid ages
        } catch (NumberFormatException e) {
            showError("✕  Please enter a valid age."); // shown if age is not a number or out of range
            return; // stop here
        }

        // ── Validation 4: password must be at least 6 characters ──
        if (password.length() < 6) {
            showError("✕  Password must be at least 6 characters."); // show error
            return; // stop here
        }

        // ── Validation 5: check MySQL to see if phone is already taken ──
        if (phoneExistsInDB(phone)) {
            showError("✕  This phone number is already registered."); // show error
            return; // stop here — cannot have two accounts with same phone
        }

        // ── Generate a new unique user ID by counting existing rows in MySQL ──
        String userId = generateUserIdFromDB(); // e.g. "U005" if 4 users already exist

        // ── INSERT the new user row into the MySQL users table ──
        String sql = "INSERT INTO users (id, full_name, phone_number, password, role, age, sex, contact_detail) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)"; // ? placeholders are filled in safely below

        try {
            Connection conn = Database.getConnection(); // get our live MySQL connection
            PreparedStatement stmt = conn.prepareStatement(sql); // prepare the INSERT query safely

            // fill in each ? placeholder in the correct order
            stmt.setString(1, userId);   // 1st ? = the new user ID e.g. "U005"
            stmt.setString(2, name);     // 2nd ? = full name
            stmt.setString(3, phone);    // 3rd ? = phone number
            stmt.setString(4, password); // 4th ? = password
            stmt.setString(5, role);     // 5th ? = role: "passenger" or "driver"
            stmt.setInt   (6, age);      // 6th ? = age as an integer
            stmt.setString(7, sex);      // 7th ? = sex e.g. "Male"
            stmt.setString(8, contact);  // 8th ? = contact detail e.g. email

            stmt.executeUpdate(); // execute the INSERT — this is what actually writes to MySQL
            stmt.close();         // close the statement to free database resources

            // print success message to Eclipse Console so we can confirm it worked
            System.out.println("✅ New user saved to MySQL: " + userId + " - " + name);

            showSuccess("Account created! You can now log in."); // show popup to user
            goToLogin(); // navigate back to the login screen

        } catch (SQLException e) {
            // something went wrong with the database — print full details to Console
            System.err.println("❌ Failed to save user to database!");
            e.printStackTrace(); // print the full error stack trace in Console
            showError("✕  Database error. Could not create account."); // show error to user
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  phoneExistsInDB() — queries MySQL to check if a phone number is taken
    //  returns true if the phone already exists, false if it is free to use
    // ─────────────────────────────────────────────────────────────────────────
    private boolean phoneExistsInDB(String phone) {
        try {
            Connection conn = Database.getConnection(); // get MySQL connection
            PreparedStatement stmt = conn.prepareStatement(
                "SELECT id FROM users WHERE phone_number = ?" // look for a matching phone number
            );
            stmt.setString(1, phone); // safely fill in the ? with the phone number to search

            ResultSet rs = stmt.executeQuery(); // run the SELECT and get the results
            boolean exists = rs.next();         // rs.next() returns true if at least one row was found
            rs.close();   // close the result set to free resources
            stmt.close(); // close the statement to free resources
            return exists; // true = phone taken, false = phone is available

        } catch (SQLException e) {
            e.printStackTrace(); // print error to Console if query fails
            return false;        // if we can't check, assume phone is free (safe fallback)
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  generateUserIdFromDB() — creates the next user ID by counting rows in MySQL
    //  e.g. if there are 4 users already, next ID will be "U005"
    // ─────────────────────────────────────────────────────────────────────────
    private String generateUserIdFromDB() {
        try {
            Connection conn = Database.getConnection(); // get MySQL connection
            PreparedStatement stmt = conn.prepareStatement(
                "SELECT COUNT(*) AS total FROM users" // count how many user rows exist in the table
            );
            ResultSet rs = stmt.executeQuery(); // run the COUNT query

            int count = 0;                            // default to 0 if something goes wrong
            if (rs.next()) count = rs.getInt("total"); // read the count value from the result

            rs.close();   // close result set
            stmt.close(); // close statement

            return "U" + String.format("%03d", count + 1); // format as "U005" — always 3 digits

        } catch (SQLException e) {
            e.printStackTrace(); // print error to Console
            return "U" + System.currentTimeMillis(); // fallback: use timestamp to guarantee uniqueness
        }
    }

    // showError() — sets the error message text and makes the red label visible
    private void showError(String msg) {
        errorLabel.setText(msg);     // put the error message into the label
        errorLabel.setVisible(true); // make the label appear on screen
    }

    // showSuccess() — pops up an information dialog with a success message
    private void showSuccess(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION); // create a standard info popup
        alert.setTitle("Success");      // title shown in the popup window's title bar
        alert.setHeaderText(null);      // no bold header needed inside the popup
        alert.setContentText(msg);      // the main message shown inside the popup
        alert.showAndWait();            // display the popup and wait until user clicks OK
    }

    // goToLogin() — navigates from this register screen back to the login screen
    private void goToLogin() {
        LoginView lv = new LoginView();                                        // create the login screen
        Scene scene = new Scene(lv.getRoot(), 520, 760);                      // wrap it in a scene of size 520x760
        scene.getStylesheets().add(yatraa.util.StyleManager.getStylesheet()); // apply the app's CSS styles
        Main.getPrimaryStage().setScene(scene);                                // switch the window to show login
    }

    // getRoot() — returns the root VBox so it can be placed inside a Scene
    public VBox getRoot() { return root; }
}