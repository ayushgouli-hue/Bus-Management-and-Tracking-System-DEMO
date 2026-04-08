package yatraa.auth; // auth package holds login and register screens
import javafx.geometry.Insets; // used to add padding/spacing around elements
import javafx.geometry.Pos; // used to align elements (CENTER, LEFT, etc.)
import javafx.scene.control.*; // imports Button, Label, TextField, PasswordField, Alert
import javafx.scene.layout.*; // imports VBox, HBox, Pane, etc.
import javafx.scene.text.Text; // used for plain text inside layouts
import yatraa.Main; // to switch screens using showLogin() or getPrimaryStage()
import yatraa.admin.AdminDashboard; // the admin dashboard screen
import yatraa.driver.DriverDashboard; // the driver dashboard screen
import yatraa.passenger.PassengerDashboard; // the passenger dashboard screen
import yatraa.model.User; // the User data class
import yatraa.ui.UIFactory; // our helper class for creating styled UI elements
import yatraa.util.DataStore; // our data storage class
import javafx.scene.Scene; // Scene wraps the screen content

// LoginView class - builds and shows the Login screen
// This is the first screen users see when they open the app
public class LoginView {
    private VBox root; // root is the main container for this screen
    private TextField phoneField; // input field for phone number
    private PasswordField passwordField; // input field for password (hides text)
    private Label errorLabel; // label to show error messages in red

    // constructor - called when we create a new LoginView object
    // it immediately calls buildUI() to set up the screen
    public LoginView() {
        buildUI(); // build all the UI elements
    }

    // buildUI() - creates all the labels, fields, and buttons on the login screen
    private void buildUI() {
        // --- outer background container ---
        root = new VBox(); // VBox stacks children vertically
        root.setStyle("-fx-background-color: #f0f0f0;"); // set plain grey background
        root.setAlignment(Pos.CENTER); // center everything on screen
        root.setPadding(new Insets(40)); // add 40px padding around edges

        // --- white card in the middle that holds the form ---
        VBox card = new VBox(20); // VBox with 20px space between children
        card.getStyleClass().add("main-card"); // apply CSS card style (white border box)
        card.setMaxWidth(340); // limit card width to 340 pixels
        card.setAlignment(Pos.TOP_CENTER); // align items to top center

        // --- logo area at the top of the card ---
        VBox logoBox = new VBox(4); // small VBox for logo items with 4px gap
        logoBox.setAlignment(Pos.CENTER); // center the logo items
        Label logoEmoji = new Label("🚌"); // bus emoji as the app icon
        logoEmoji.setStyle("-fx-font-size: 40px;"); // make the emoji large
        Label logoLabel = new Label("YAATRI"); // app name text
        logoLabel.getStyleClass().add("logo-label"); // apply large bold style
        Label logoSub = new Label("BUS TRACKING SYSTEM"); // subtitle below app name
        logoSub.getStyleClass().add("logo-sub"); // apply small grey style
        logoBox.getChildren().addAll(logoEmoji, logoLabel, logoSub); // add all to logo box

        // --- "how to use" clickable link ---
        Label howTo = new Label("[ HOW TO USE ]"); // clickable hint text
        howTo.setStyle("-fx-text-fill: #555555; -fx-font-size: 11px; -fx-underline: true; -fx-cursor: hand;");
        howTo.setOnMouseClicked(e -> showHowTo()); // when clicked, call showHowTo()

        // --- thin horizontal divider line ---
        Pane div = UIFactory.divider(); // creates a thin grey line

        // --- phone number field ---
        VBox phoneBox = new VBox(6); // small VBox to hold the label + field together
        phoneBox.getChildren().add(UIFactory.formLabel("Phone Number")); // "PHONE NUMBER" label above
        phoneField = UIFactory.paperField("Enter your phone number"); // the actual input field
        phoneBox.getChildren().add(phoneField); // add field below label

        // --- password field ---
        VBox passBox = new VBox(6); // small VBox to hold the label + field together
        passBox.getChildren().add(UIFactory.formLabel("Password")); // "PASSWORD" label above
        passwordField = UIFactory.paperPassword("Enter your password"); // the password input field
        passBox.getChildren().add(passwordField); // add field below label

        // --- error message label (hidden until login fails) ---
        errorLabel = UIFactory.errorLabel(""); // create empty error label
        errorLabel.setVisible(false); // hide it by default

        // --- login button ---
        Button loginBtn = UIFactory.primaryBtn("LOGIN"); // create the LOGIN button
        loginBtn.setOnAction(e -> handleLogin()); // when clicked, call handleLogin()

        // --- register link at the bottom ---
        HBox registerRow = new HBox(4); // horizontal row with 4px gap
        registerRow.setAlignment(Pos.CENTER); // center the row
        Text noAccount = new Text("Don't have an account? "); // plain text before link
        noAccount.setStyle("-fx-font-size: 12px; -fx-fill: #555555;"); // grey small text
        Label registerLink = UIFactory.linkLabel("Register here"); // clickable underlined link
        registerLink.setOnMouseClicked(e -> goToRegister()); // when clicked, go to register
        registerRow.getChildren().addAll(noAccount, registerLink); // add text and link to row

        // --- add all elements to the card in order from top to bottom ---
        card.getChildren().addAll(
            logoBox, // bus emoji + YAATRI + BUS TRACKING SYSTEM
            howTo, // [ HOW TO USE ] link
            div, // horizontal line
            phoneBox, // PHONE NUMBER label + input field
            passBox, // PASSWORD label + input field
            errorLabel, // error message (hidden by default)
            loginBtn, // LOGIN button
            registerRow // "Don't have an account? Register here"
        );

        // --- add the card to the outer root container ---
        root.getChildren().add(card);
    }

    // handleLogin() — called when the user clicks the LOGIN button
    // reads the phone and password fields, checks them against MySQL, then navigates to the right dashboard
    private void handleLogin() {
        String phone = phoneField.getText().trim(); // get the phone number the user typed, remove extra spaces
        String password = passwordField.getText().trim(); // get the password the user typed, remove extra spaces
        errorLabel.setVisible(false); // hide any error message left over from a previous login attempt

        // ── Validation: make sure neither field is empty ──────────
        if (phone.isEmpty() || password.isEmpty()) {
            errorLabel.setText("✕ Both fields are required."); // set the error message text
            errorLabel.setVisible(true); // make the red error label appear
            return; // stop here — do not try to log in
        }

        // ── Query MySQL to find a user with matching phone AND password ──
        try {
            java.sql.Connection conn = yatraa.util.Database.getConnection(); // get our live MySQL connection
            java.sql.PreparedStatement stmt = conn.prepareStatement(
                "SELECT * FROM users WHERE phone_number = ? AND password = ?" // find exact match in users table
            );
            stmt.setString(1, phone); // safely replace first ? with the phone number the user typed
            stmt.setString(2, password); // safely replace second ? with the password the user typed
            java.sql.ResultSet rs = stmt.executeQuery(); // run the SELECT query and store the results

            // ── Check if any matching user row was found ──────────
            if (!rs.next()) {
                // rs.next() returns false when no rows match — means wrong phone or password
                errorLabel.setText("✕ Invalid phone number or password."); // set error message
                errorLabel.setVisible(true); // show the red error label
                rs.close(); // close result set to free database resources
                stmt.close(); // close statement to free database resources
                return; // stop here — do not log in
            }

            // ── A matching row was found — build a User object from the MySQL data ──
            User user = new User(
                rs.getString("id"), // read the user's ID from the database row e.g. "U001"
                rs.getString("full_name"), // read the user's full name e.g. "Ram Shrestha"
                rs.getString("phone_number"), // read the phone number e.g. "9800000002"
                rs.getString("password"), // read the password e.g. "driver123"
                rs.getString("role"), // read the role e.g. "admin", "driver", or "passenger"
                rs.getInt("age"), // read the age as an integer e.g. 30
                rs.getString("sex"), // read the sex e.g. "Male"
                rs.getString("contact_detail") // read the contact detail e.g. email address
            );

            rs.close(); // close result set — we have everything we need now
            stmt.close(); // close statement — always clean up after database queries

            // print a confirmation to the Eclipse Console so we know login worked
            System.out.println("✅ Logged in from MySQL: " + user.getFullName() + " (" + user.getRole() + ")");

            // ── Save the logged-in user so other screens can access it ──
            yatraa.util.DataStore.setCurrentUser(user); // store the user in DataStore as the active session

            // ── Navigate to the correct dashboard based on the user's role ──
            navigateByRole(user); // admin → AdminDashboard, driver → DriverDashboard, else → PassengerDashboard
        } catch (java.sql.SQLException e) {
            // something went wrong with the database connection or query
            System.err.println("❌ Login DB error!"); // print short error label to Console
            e.printStackTrace(); // print full error details to Console for debugging
            errorLabel.setText("✕ Database error. Please try again."); // show friendly error to user
            errorLabel.setVisible(true); // make the red error label appear on screen
        }
    }

    // navigateByRole() - switches to the right dashboard for each role
    // admin → AdminDashboard, driver → DriverDashboard, passenger → PassengerDashboard
    private void navigateByRole(User user) {
        javafx.scene.Parent view; // this will hold whichever dashboard we pick
        // switch statement checks the user's role
        switch (user.getRole()) {
            case "admin" -> view = new AdminDashboard().getRoot(); // admin goes to admin screen
            case "driver" -> view = new DriverDashboard().getRoot(); // driver goes to driver screen
            default -> view = new PassengerDashboard().getRoot(); // everyone else goes to passenger
        }
        Scene scene = new Scene(view, 520, 760); // create scene with the chosen view
        scene.getStylesheets().add(yatraa.util.StyleManager.getStylesheet()); // apply CSS styles
        Main.getPrimaryStage().setScene(scene); // switch the window to this scene
    }

    // goToRegister() - switches the screen to the Register page
    private void goToRegister() {
        RegisterView reg = new RegisterView(); // create the register screen
        Scene scene = new Scene(reg.getRoot(), 560, 760); // wrap in a scene
        scene.getStylesheets().add(yatraa.util.StyleManager.getStylesheet()); // apply CSS styles
        Main.getPrimaryStage().setScene(scene); // switch to register screen
    }

    // showHowTo() - shows a popup with login instructions for testing
    // this helps testers know which phone/password to use for each role
    private void showHowTo() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION); // create an info popup
        alert.setTitle("How to Use — Yaatri"); // popup title
        alert.setHeaderText("YAATRI BUS TRACKING"); // bold header in popup
        alert.setContentText(
            "ADMIN — Phone: 9800000001 | Pass: admin123\n" +
            " → Manage buses, routes, schedules\n\n" +
            "DRIVER — Phone: 9800000002 | Pass: driver123\n" +
            " → Update bus status in real time\n\n" +
            "PASSENGER — Phone: 9800000004 | Pass: pass123\n" +
            " → View live bus information"
        );
        alert.showAndWait(); // show the popup and wait for user to close it
    }

    // getRoot() - returns the root VBox so it can be put inside a Scene
    public VBox getRoot() { return root; }
}
