package yatraa.admin; // admin package holds all admin screens

import javafx.geometry.Insets;   // used for padding
import javafx.geometry.Pos;      // used for alignment
import javafx.scene.Scene;       // Scene wraps a screen
import javafx.scene.control.*;   // Button, Label, ContextMenu, MenuItem, etc.
import javafx.scene.layout.*;    // VBox, HBox, BorderPane, StackPane, Region, etc.
import yatraa.Main;              // to switch screens
import yatraa.auth.LoginView;    // login screen (for logout)
import yatraa.model.User;        // User data class
import yatraa.ui.UIFactory;      // helper class for creating styled UI elements
import yatraa.util.DataStore;    // our data storage class

// AdminDashboard class - the main screen for the admin user
// It has a header bar at the top, content in the middle, and a nav bar at the bottom
// The admin can switch between HOME, BUSES, ROUTES, and SCHEDULE tabs
public class AdminDashboard {

    private BorderPane root;        // BorderPane divides the screen into top/center/bottom
    private StackPane contentArea;  // the middle area where tab content is shown
    private Button[] navBtns;       // array holding all 4 navigation buttons

    // sub-views for each tab (created once and reused)
    private AdminBusesView busesView;       // the Manage Buses tab content
    private AdminRoutesView routesView;     // the Manage Routes tab content
    private AdminScheduleView scheduleView; // the Schedule tab content

    // constructor - creates all the sub-views and builds the main layout
    public AdminDashboard() {
        busesView    = new AdminBusesView();    // create buses management view
        routesView   = new AdminRoutesView();   // create routes management view
        scheduleView = new AdminScheduleView(); // create schedule management view
        buildUI();                              // build the main dashboard layout
    }

    // buildUI() - builds the main layout with header, content area, and nav bar
    private void buildUI() {
        root = new BorderPane();                              // create the main layout container
        root.setStyle("-fx-background-color: #f0f0f0;");     // set grey background

        root.setTop(buildHeader());     // put the header bar at the top
        root.setBottom(buildNavBar());  // put the nav bar at the bottom

        contentArea = new StackPane();                            // content area sits in the middle
        contentArea.setStyle("-fx-background-color: #f0f0f0;");  // grey background for content
        showTab(0);                     // show the HOME tab by default
        root.setCenter(contentArea);    // put the content area in the center
    }

    // buildHeader() - creates the top black bar with app name, role, and avatar button
    private HBox buildHeader() {
        User user = DataStore.getCurrentUser(); // get the logged-in user

        // get the first letter of the admin's name for the avatar button
        String initials = user != null && user.getFullName().length() > 0
            ? String.valueOf(user.getFullName().charAt(0)).toUpperCase() : "A";

        HBox header = new HBox();                         // horizontal row for header
        header.getStyleClass().add("header-bar");         // apply dark header CSS style
        header.setAlignment(Pos.CENTER_LEFT);             // align items to the left
        header.setSpacing(10);                            // 10px gap between items

        VBox titleBox = new VBox(2);                      // VBox for app name + role label
        Label appLabel = new Label("YAATRI");             // app name shown in header
        appLabel.getStyleClass().add("header-title");     // apply white bold title style
        Label roleLabel = new Label("Admin");             // role label below the app name
        roleLabel.getStyleClass().add("header-subtitle"); // apply grey small style
        titleBox.getChildren().addAll(appLabel, roleLabel); // add both to titleBox

        Region spacer = new Region();                     // spacer pushes avatar to the right
        HBox.setHgrow(spacer, Priority.ALWAYS);           // spacer grows to fill available space

        Button avatarBtn = new Button(initials);          // circle button showing first letter of name
        avatarBtn.getStyleClass().add("header-avatar");   // apply circular button style
        avatarBtn.setOnAction(e -> showProfileMenu(avatarBtn)); // click to show profile/logout menu

        header.getChildren().addAll(titleBox, spacer, avatarBtn); // add all items to header
        return header; // return the finished header
    }

    // buildNavBar() - creates the bottom navigation bar with 4 tab buttons
    private HBox buildNavBar() {
        HBox nav = new HBox();                      // horizontal row for nav bar
        nav.getStyleClass().add("nav-bar");          // apply nav bar CSS style
        nav.setAlignment(Pos.CENTER);               // center the buttons
        nav.setSpacing(0);                          // no gap between buttons

        String[] labels = {"HOME", "BUSES", "ROUTES", "SCHEDULE"}; // tab names
        navBtns = new Button[labels.length];        // create array to hold the buttons

        for (int i = 0; i < labels.length; i++) {  // loop through each tab name
            final int idx = i;                     // save index as final for use in lambda
            navBtns[i] = new Button(labels[i]);    // create a button with this tab's name
            navBtns[i].getStyleClass().add(i == 0 ? "nav-btn-active" : "nav-btn"); // first tab is active
            navBtns[i].setMinWidth(110);            // set minimum width so all buttons are equal size
            navBtns[i].setOnAction(e -> {
                showTab(idx);          // when clicked, show the tab for this index
                updateNavActive(idx);  // and highlight this button as active
            });
            nav.getChildren().add(navBtns[i]); // add the button to the nav bar
        }
        return nav; // return the finished nav bar
    }

    // updateNavActive() - updates which nav button looks "active" (highlighted)
    // activeIdx is the index of the tab that is currently selected
    private void updateNavActive(int activeIdx) {
        for (int i = 0; i < navBtns.length; i++) {         // loop through all nav buttons
            navBtns[i].getStyleClass().clear();             // remove all CSS classes
            navBtns[i].getStyleClass().add(                 // add the correct CSS class
                i == activeIdx ? "nav-btn-active" : "nav-btn" // active or inactive style
            );
        }
    }

    // showTab() - switches the content area to show the selected tab's content
    // idx 0=HOME, 1=BUSES, 2=ROUTES, 3=SCHEDULE
    private void showTab(int idx) {
        contentArea.getChildren().clear(); // clear whatever was showing before

        switch (idx) {
            case 0 -> contentArea.getChildren().add(buildQuickActionsView()); // show home/quick actions
            case 1 -> {
                busesView.refresh();                              // refresh the buses list
                contentArea.getChildren().add(busesView.getRoot()); // show buses view
            }
            case 2 -> {
                routesView.refresh();                               // refresh the routes list
                contentArea.getChildren().add(routesView.getRoot()); // show routes view
            }
            case 3 -> {
                scheduleView.refresh();                                // refresh the schedule list
                contentArea.getChildren().add(scheduleView.getRoot()); // show schedule view
            }
        }
    }

    // buildQuickActionsView() - builds the HOME tab content
    // shows quick action buttons and recent activity
    private javafx.scene.Node buildQuickActionsView() {
        VBox outer = new VBox(20);                               // vertical layout with 20px gap
        outer.setPadding(new Insets(24));                        // 24px padding around edges
        outer.setStyle("-fx-background-color: #f0f0f0;");       // grey background

        Label title = UIFactory.pageTitle("Quick Actions");     // large page title
        Pane div = UIFactory.divider();                          // thin divider line

        Label actionsLabel = UIFactory.sectionTitle("Admin Actions"); // section heading

        // add bus quick action button
        Button addBusBtn = UIFactory.actionBtn("Add New Bus"); // button to go to buses tab
        addBusBtn.setOnAction(e -> {
            showTab(1);           // switch to buses tab
            updateNavActive(1);   // highlight buses nav button
        });

        // add route quick action button
        Button addRouteBtn = UIFactory.actionBtn("Add New Route"); // button to go to routes tab
        addRouteBtn.setOnAction(e -> {
            showTab(2);           // switch to routes tab
            updateNavActive(2);   // highlight routes nav button
        });

        // create schedule quick action button
        Button addSchedBtn = UIFactory.actionBtn("Create Schedule"); // button to go to schedule tab
        addSchedBtn.setOnAction(e -> {
            showTab(3);           // switch to schedule tab
            updateNavActive(3);   // highlight schedule nav button
        });

        Label recentLabel = UIFactory.sectionTitle("Recent Activity"); // section heading
        VBox activityBox = buildActivity(); // build the activity list

        // add everything to the outer layout in order
        outer.getChildren().addAll(
            title, div,
            actionsLabel, addBusBtn, addRouteBtn, addSchedBtn,
            UIFactory.divider(),
            recentLabel, activityBox
        );

        return UIFactory.wrappedScroll(outer); // wrap in scroll pane so user can scroll
    }

    // buildActivity() - creates the Recent Activity section with sample activity items
    private VBox buildActivity() {
        VBox box = new VBox(10); // vertical layout with 10px gap

        // sample activity data: each row has a dot, a message, and a time
        String[][] activities = {
            {"●", "Bus BUS-101 departed from Ratna Park", "2 min ago"},
            {"●", "Route R-002 updated by Admin",         "15 min ago"},
            {"●", "New schedule SCH-003 created",         "1 hour ago"}
        };

        for (String[] act : activities) { // loop through each activity item
            HBox row = new HBox(10);               // horizontal row with 10px gap
            row.setAlignment(Pos.CENTER_LEFT);     // align to left

            Label dot = new Label(act[0]);          // the dot "●" symbol
            dot.setStyle("-fx-text-fill: #555555; -fx-font-size: 12px;");

            VBox details = new VBox(2); // VBox to stack message and time
            Label msg = new Label(act[1]); // the activity message
            msg.setStyle("-fx-font-size: 12px; -fx-text-fill: #333333;");
            Label time = new Label(act[2]); // the time ago text
            time.getStyleClass().add("meta-label"); // small grey text
            details.getChildren().addAll(msg, time); // add message and time

            row.getChildren().addAll(dot, details); // add dot and details to row
            box.getChildren().add(row);             // add row to the activity box
        }
        return box; // return the finished activity list
    }

    // showProfileMenu() - shows a small context menu with user info and logout option
    // anchor is the button that was clicked, the menu appears below it
    private void showProfileMenu(Button anchor) {
        ContextMenu menu = new ContextMenu(); // create the popup menu
        User user = DataStore.getCurrentUser(); // get the logged-in user

        // menu item showing who is logged in (disabled, just for display)
        MenuItem info = new MenuItem("Logged in as: " + (user != null ? user.getFullName() : "Unknown"));
        info.setDisable(true); // make it unclickable, just for info

        // logout menu item
        MenuItem logout = new MenuItem("[ LOGOUT ]");
        logout.setOnAction(e -> {
            DataStore.setCurrentUser(null); // clear the logged-in user
            Main.showLogin();               // go back to the login screen
        });

        menu.getItems().addAll(info, new SeparatorMenuItem(), logout); // add items with a separator
        menu.show(anchor, javafx.geometry.Side.BOTTOM, 0, 0); // show menu below the button
    }

    // getRoot() - returns the root BorderPane so it can be placed in a Scene
    public BorderPane getRoot() { return root; }
}
