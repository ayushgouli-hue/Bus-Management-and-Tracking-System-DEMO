package yatraa; // this is the main package where the app starts

import javafx.application.Application; // this class is needed to start any JavaFX app
import javafx.scene.Scene;             // Scene is what you see inside the window
import javafx.stage.Stage;            // Stage is the main window of the app
import yatraa.auth.LoginView;          // we import our login screen
import yatraa.util.DataStore;          // we import our data storage class
import yatraa.util.StyleManager;       // we import our CSS style class

// Main class - this is where the whole app begins running
// It extends Application which is required for all JavaFX apps
public class Main extends Application {

    // primaryStage holds the main window so other screens can use it later
    private static Stage primaryStage;

    // start() is called automatically by JavaFX when the app launches
    // stage is the main window that JavaFX gives us
    @Override
    public void start(Stage stage) {
        primaryStage = stage;          // save the window so other classes can use it
        DataStore.initialize();        // fill in all the sample bus/user/route data

        stage.setTitle("Yaatri — Bus Tracking System"); // text shown in the title bar

        stage.setMinWidth(480);  // minimum width the user can resize the window to
        stage.setMinHeight(700); // minimum height the user can resize the window to
        stage.setWidth(520);     // starting width of the window
        stage.setHeight(760);    // starting height of the window

        showLogin();   // show the login screen first when app opens
        stage.show();  // make the window visible on screen
    }

    // showLogin() switches the screen to the Login page
    // we call this when the app starts or when the user logs out
    public static void showLogin() {
        LoginView loginView = new LoginView();                          // create a new login screen object
        Scene scene = new Scene(loginView.getRoot(), 520, 760);        // wrap it in a scene of size 520x760
        scene.getStylesheets().add(StyleManager.getStylesheet());      // apply our CSS styles to the scene
        primaryStage.setScene(scene);                                   // set this scene on the main window
    }

    // getPrimaryStage() returns the main window
    // other screens call this method to switch scenes on the window
    public static Stage getPrimaryStage() {
        return primaryStage; // return the saved window reference
    }

    // main() is where Java starts running the program
    // launch(args) starts the JavaFX application lifecycle
    public static void main(String[] args) {
        launch(args); // this will call start() automatically
    }
}
