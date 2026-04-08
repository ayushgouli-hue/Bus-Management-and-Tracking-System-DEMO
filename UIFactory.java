package yatraa.ui; // ui package holds reusable UI building helper methods

import javafx.geometry.Insets; // used to set padding around elements
import javafx.geometry.Pos;    // used to align elements (CENTER, LEFT, etc.)
import javafx.scene.control.*; // imports Button, Label, TextField, PasswordField, etc.
import javafx.scene.layout.*;  // imports VBox, HBox, Pane, GridPane, etc.

// UIFactory class - a helper class with static methods that create styled UI elements
// instead of writing the same style code over and over, we call these methods
// example: UIFactory.primaryBtn("LOGIN") gives us a ready-made styled button
public class UIFactory {

    // pageTitle() - creates a large bold title label for the top of a page
    // example: UIFactory.pageTitle("Manage Buses")
    public static Label pageTitle(String text) {
        Label lbl = new Label(text);           // create a new label with the given text
        lbl.getStyleClass().add("page-title"); // apply the CSS class for page title style
        return lbl;                            // return the styled label
    }

    // sectionTitle() - creates a smaller bold heading within a page
    // text is automatically made uppercase
    public static Label sectionTitle(String text) {
        Label lbl = new Label(text.toUpperCase());       // create label with uppercase text
        lbl.getStyleClass().add("page-section-title");   // apply CSS class for section title
        return lbl;                                      // return the styled label
    }

    // formLabel() - creates a tiny label above a form field like "PHONE NUMBER"
    // text is automatically made uppercase
    public static Label formLabel(String text) {
        Label lbl = new Label(text.toUpperCase()); // create label with uppercase text
        lbl.getStyleClass().add("form-label");     // apply CSS class for form label
        return lbl;                                // return the styled label
    }

    // paperField() - creates a plain text input field with a placeholder hint
    // prompt is the grey hint text shown when the field is empty
    public static TextField paperField(String prompt) {
        TextField tf = new TextField();         // create a new text input field
        tf.setPromptText(prompt);               // set the grey placeholder hint text
        tf.getStyleClass().add("paper-field");  // apply the CSS class for styling
        tf.setMaxWidth(Double.MAX_VALUE);       // allow the field to stretch full width
        return tf;                              // return the styled text field
    }

    // paperPassword() - creates a password field that hides what you type
    // prompt is the grey hint text shown when the field is empty
    public static PasswordField paperPassword(String prompt) {
        PasswordField pf = new PasswordField();     // create a new password field
        pf.setPromptText(prompt);                   // set the grey placeholder hint text
        pf.getStyleClass().add("paper-password");   // apply the CSS class for styling
        pf.setMaxWidth(Double.MAX_VALUE);            // allow the field to stretch full width
        return pf;                                   // return the styled password field
    }

    // primaryBtn() - creates the main dark action button used for Login, Save, etc.
    public static Button primaryBtn(String text) {
        Button btn = new Button(text);             // create a button with the given text
        btn.getStyleClass().add("primary-btn");    // apply the CSS class for styling
        btn.setMaxWidth(Double.MAX_VALUE);         // make button stretch to full width
        return btn;                                // return the styled button
    }

    // secondaryBtn() - creates a lighter button used for secondary actions like Edit
    public static Button secondaryBtn(String text) {
        Button btn = new Button(text);              // create a button with the given text
        btn.getStyleClass().add("secondary-btn");   // apply the CSS class for styling
        return btn;                                 // return the styled button
    }

    // dangerBtn() - creates a button used for destructive actions like Delete
    public static Button dangerBtn(String text) {
        Button btn = new Button(text);           // create a button with the given text
        btn.getStyleClass().add("danger-btn");   // apply the CSS class for styling
        return btn;                              // return the styled button
    }

    // actionBtn() - creates a wide button used for quick action items in menus
    // adds an arrow symbol at the end to indicate navigation
    public static Button actionBtn(String text) {
        Button btn = new Button("  + " + text + "  →"); // add + prefix and → suffix
        btn.getStyleClass().add("action-btn");           // apply the CSS class for styling
        btn.setMaxWidth(Double.MAX_VALUE);               // make button stretch to full width
        return btn;                                      // return the styled button
    }

    // badge() - creates a small label that shows a bus status like "Active" or "Delayed"
    // status determines which CSS class is applied (different background color)
    public static Label badge(String text, String status) {
        Label lbl = new Label(text); // create a label with the status text
        switch (status.toLowerCase()) { // check status in lowercase
            case "delayed"    -> lbl.getStyleClass().add("badge-delayed"); // delayed style
            case "in transit" -> lbl.getStyleClass().add("badge-transit"); // in transit style
            default           -> lbl.getStyleClass().add("badge-active");  // active style (default)
        }
        return lbl; // return the styled badge label
    }

    // divider() - creates a thin horizontal line to separate sections
    public static Pane divider() {
        Pane div = new Pane();             // create an empty pane
        div.getStyleClass().add("divider"); // apply the CSS class that makes it a thin line
        div.setMaxWidth(Double.MAX_VALUE);  // stretch the line to full width
        return div;                         // return the divider
    }

    // errorLabel() - creates a red label used to display error messages
    // starts with the given message text (usually empty "")
    public static Label errorLabel(String msg) {
        Label lbl = new Label(msg);             // create label with the message text
        lbl.getStyleClass().add("error-label"); // apply CSS class for red error text
        return lbl;                             // return the styled error label
    }

    // formCard() - creates a white box with a dashed border used to wrap forms
    public static VBox formCard() {
        VBox box = new VBox(12);             // vertical layout with 12px gap between items
        box.getStyleClass().add("form-card"); // apply the CSS class for form card style
        box.setMaxWidth(Double.MAX_VALUE);    // stretch to full width
        return box;                           // return the styled form card
    }

    // infoCard() - creates a white box used to show information like bus details
    public static VBox infoCard() {
        VBox box = new VBox(8);              // vertical layout with 8px gap between items
        box.getStyleClass().add("info-card"); // apply the CSS class for info card style
        return box;                           // return the styled info card
    }

    // metaRow() - creates a row with a label on the left and a value on the right
    // used to show details like "CAPACITY   45 seats"
    public static HBox metaRow(String label, String value) {
        Label lbl = new Label(label);          // create the label (e.g. "CAPACITY")
        lbl.getStyleClass().add("meta-label"); // apply grey small text style
        lbl.setMinWidth(90);                   // set minimum width so all labels line up

        Label val = new Label(value);           // create the value (e.g. "45 seats")
        val.getStyleClass().add("value-label"); // apply bold dark text style

        HBox row = new HBox(10, lbl, val);       // put both in a horizontal row with 10px gap
        row.setAlignment(Pos.CENTER_LEFT);        // align them to the left
        return row;                               // return the finished row
    }

    // linkLabel() - creates a clickable underlined text link
    // example: "Register here" or "Login here"
    public static Label linkLabel(String text) {
        Label lbl = new Label(text);              // create label with the given text
        lbl.getStyleClass().add("link-label");    // apply CSS class for blue underlined link
        return lbl;                               // return the styled link label
    }

    // paperCheck() - creates a styled checkbox
    // text is the label shown next to the checkbox
    public static CheckBox paperCheck(String text) {
        CheckBox cb = new CheckBox(text);       // create checkbox with the given label
        cb.getStyleClass().add("paper-check");  // apply the CSS class for styling
        return cb;                              // return the styled checkbox
    }

    // paperCombo() - creates a styled dropdown (ComboBox)
    // items are added separately after calling this method
    public static ComboBox<String> paperCombo() {
        ComboBox<String> cb = new ComboBox<>();  // create a new dropdown
        cb.getStyleClass().add("paper-combo");   // apply the CSS class for styling
        cb.setMaxWidth(Double.MAX_VALUE);         // stretch to full width
        return cb;                                // return the styled dropdown
    }

    // wrappedScroll() - wraps any content inside a scroll pane
    // the scroll pane allows the user to scroll if the content is too tall
    public static ScrollPane wrappedScroll(javafx.scene.Node content) {
        ScrollPane sp = new ScrollPane(content);              // put the content inside a scroll pane
        sp.setFitToWidth(true);                               // make content stretch to the full width
        sp.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);   // hide horizontal scrollbar
        sp.getStyleClass().add("scroll-pane");                // apply CSS class for scroll pane
        sp.setStyle("-fx-background-color: #f0f0f0;");        // set background color directly
        return sp;                                             // return the scroll pane
    }
}
