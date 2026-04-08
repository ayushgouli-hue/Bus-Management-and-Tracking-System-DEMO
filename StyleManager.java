package yatraa.util; // util package holds helper classes

import java.io.File;        // File is used to create a temporary file on disk
import java.io.FileWriter;  // FileWriter is used to write text into a file
import java.io.IOException; // IOException handles errors when reading/writing files

// StyleManager class - generates and returns the CSS file for the whole app
// CSS is what makes the app look nice with fonts, colors, borders etc.
// This class writes CSS into a temporary file and returns the file path
public class StyleManager {

    // cssPath saves the path of the CSS file once it is created
    // we use this to avoid creating the file more than once
    private static String cssPath = null;

    // getStylesheet() - returns the path to the CSS file
    // if the file already exists, it just returns the saved path
    // if not, it creates the file first, then returns the path
    public static String getStylesheet() {
        if (cssPath != null) return cssPath; // file already created, just return it

        try {
            File tmpFile = File.createTempFile("yaatri_style", ".css"); // create a temp CSS file
            tmpFile.deleteOnExit();                                       // delete the file when app closes

            FileWriter fw = new FileWriter(tmpFile); // open the file for writing
            fw.write(getCSS());                       // write all the CSS into the file
            fw.close();                               // close the file when done

            cssPath = tmpFile.toURI().toString(); // save the file path as a URI string
            return cssPath;                        // return the path

        } catch (IOException e) {
            e.printStackTrace(); // print the error to the console if something goes wrong
            return "";           // return empty string so app doesn't crash completely
        }
    }

    // getCSS() - returns all the CSS styling rules as one big String
    // this gives the app a simple, plain, beginner-friendly look
    // similar to the friend's UI style in the screenshots
    private static String getCSS() {
        return """
/* ===================================================
   YAATRI — Simple Plain UI Style
   =================================================== */

/* base font and background for the whole app */
.root {
    -fx-font-family: 'Arial', sans-serif;
    -fx-background-color: #f0f0f0;
    -fx-font-size: 13px;
}

/* light grey background used on all screens */
.bg {
    -fx-background-color: #f0f0f0;
}

/* white card panel used to group form elements */
.main-card {
    -fx-background-color: #ffffff;
    -fx-border-color: #cccccc;
    -fx-border-width: 1px;
    -fx-padding: 20px;
}

/* scroll pane background stays grey like the rest */
.scroll-pane {
    -fx-background-color: #f0f0f0;
    -fx-border-color: transparent;
}
.scroll-pane > .viewport {
    -fx-background-color: #f0f0f0;
}

/* top header bar (black bar at top) */
.header-bar {
    -fx-background-color: #333333;
    -fx-padding: 10px 16px;
}

/* app name text in header e.g. "YAATRI" */
.header-title {
    -fx-text-fill: #ffffff;
    -fx-font-size: 18px;
    -fx-font-weight: bold;
}

/* role label in header e.g. "Admin" or "Driver" */
.header-subtitle {
    -fx-text-fill: #bbbbbb;
    -fx-font-size: 11px;
}

/* small circular button in header to show profile or logout */
.header-avatar {
    -fx-background-color: #ffffff;
    -fx-text-fill: #333333;
    -fx-font-weight: bold;
    -fx-font-size: 13px;
    -fx-min-width: 32px;
    -fx-min-height: 32px;
    -fx-max-width: 32px;
    -fx-max-height: 32px;
    -fx-cursor: hand;
    -fx-border-color: #ffffff;
    -fx-background-radius: 50%;
    -fx-border-radius: 50%;
}

/* large title text at the top of each page */
.page-title {
    -fx-font-size: 20px;
    -fx-font-weight: bold;
    -fx-text-fill: #222222;
}

/* smaller section headings inside a page */
.page-section-title {
    -fx-font-size: 13px;
    -fx-font-weight: bold;
    -fx-text-fill: #333333;
}

/* logo text on login screen */
.logo-label {
    -fx-font-size: 28px;
    -fx-font-weight: bold;
    -fx-text-fill: #222222;
}

/* subtitle below logo e.g. "BUS TRACKING SYSTEM" */
.logo-sub {
    -fx-font-size: 11px;
    -fx-text-fill: #666666;
}

/* small label above a form field e.g. "Phone Number" */
.form-label {
    -fx-font-size: 11px;
    -fx-font-weight: bold;
    -fx-text-fill: #444444;
}

/* plain text input field */
.paper-field {
    -fx-background-color: #ffffff;
    -fx-border-color: #aaaaaa;
    -fx-border-width: 1px;
    -fx-padding: 7px 10px;
    -fx-font-size: 13px;
    -fx-prompt-text-fill: #aaaaaa;
}
.paper-field:focused {
    -fx-border-color: #4a90e2;
    -fx-border-width: 1.5px;
}

/* password field looks same as text field but hides characters */
.paper-password {
    -fx-background-color: #ffffff;
    -fx-border-color: #aaaaaa;
    -fx-border-width: 1px;
    -fx-padding: 7px 10px;
    -fx-font-size: 13px;
    -fx-prompt-text-fill: #aaaaaa;
}
.paper-password:focused {
    -fx-border-color: #4a90e2;
    -fx-border-width: 1.5px;
}

/* dropdown selector (ComboBox) */
.paper-combo {
    -fx-background-color: #ffffff;
    -fx-border-color: #aaaaaa;
    -fx-border-width: 1px;
    -fx-font-size: 13px;
}

/* main action button (dark grey) */
.primary-btn {
    -fx-background-color: #dddddd;
    -fx-text-fill: #222222;
    -fx-font-size: 13px;
    -fx-border-color: #999999;
    -fx-border-width: 1px;
    -fx-padding: 7px 18px;
    -fx-cursor: hand;
}
.primary-btn:hover {
    -fx-background-color: #cccccc;
}

/* secondary button used for Edit etc. */
.secondary-btn {
    -fx-background-color: #eeeeee;
    -fx-text-fill: #333333;
    -fx-font-size: 12px;
    -fx-border-color: #aaaaaa;
    -fx-border-width: 1px;
    -fx-padding: 5px 14px;
    -fx-cursor: hand;
}
.secondary-btn:hover {
    -fx-background-color: #e0e0e0;
}

/* danger button used for Delete */
.danger-btn {
    -fx-background-color: #eeeeee;
    -fx-text-fill: #cc0000;
    -fx-font-size: 12px;
    -fx-border-color: #cc0000;
    -fx-border-width: 1px;
    -fx-padding: 5px 14px;
    -fx-cursor: hand;
}
.danger-btn:hover {
    -fx-background-color: #ffe0e0;
}

/* action button used for menu items like Add Bus */
.action-btn {
    -fx-background-color: #dddddd;
    -fx-text-fill: #222222;
    -fx-font-size: 13px;
    -fx-border-color: #aaaaaa;
    -fx-border-width: 1px;
    -fx-padding: 10px 16px;
    -fx-cursor: hand;
    -fx-alignment: center-left;
}
.action-btn:hover {
    -fx-background-color: #cccccc;
}

/* status buttons used on the driver screen */
.status-on-time-btn {
    -fx-background-color: #eeeeee;
    -fx-text-fill: #222222;
    -fx-border-color: #aaaaaa;
    -fx-border-width: 1px;
    -fx-padding: 10px;
    -fx-cursor: hand;
}
.status-delayed-btn {
    -fx-background-color: #dddddd;
    -fx-text-fill: #222222;
    -fx-border-color: #aaaaaa;
    -fx-border-width: 1px;
    -fx-padding: 10px;
    -fx-cursor: hand;
}
.status-departed-btn {
    -fx-background-color: #dddddd;
    -fx-text-fill: #222222;
    -fx-border-color: #aaaaaa;
    -fx-border-width: 1px;
    -fx-padding: 10px;
    -fx-cursor: hand;
}
.status-arrived-btn {
    -fx-background-color: #cccccc;
    -fx-text-fill: #222222;
    -fx-border-color: #aaaaaa;
    -fx-border-width: 1px;
    -fx-padding: 10px;
    -fx-cursor: hand;
}

/* clickable link text */
.link-label {
    -fx-text-fill: #3366cc;
    -fx-font-size: 12px;
    -fx-underline: true;
    -fx-cursor: hand;
}

/* red error message label */
.error-label {
    -fx-text-fill: #cc0000;
    -fx-font-size: 12px;
}

/* checkbox used in forms */
.paper-check .box {
    -fx-background-color: #ffffff;
    -fx-border-color: #aaaaaa;
    -fx-border-width: 1px;
}
.paper-check:selected .box {
    -fx-background-color: #555555;
}
.paper-check:selected .mark {
    -fx-background-color: #ffffff;
}

/* white card used to display bus info */
.bus-card {
    -fx-background-color: #ffffff;
    -fx-border-color: #cccccc;
    -fx-border-width: 1px;
    -fx-padding: 12px;
}

/* white card used to show info details */
.info-card {
    -fx-background-color: #ffffff;
    -fx-border-color: #cccccc;
    -fx-border-width: 1px;
    -fx-padding: 12px;
}

/* card used around forms with dashed border */
.form-card {
    -fx-background-color: #ffffff;
    -fx-border-color: #aaaaaa;
    -fx-border-width: 1px;
    -fx-border-style: dashed;
    -fx-padding: 14px;
}

/* card used for schedule display */
.schedule-card {
    -fx-background-color: #ffffff;
    -fx-border-color: #cccccc;
    -fx-border-width: 1px;
    -fx-padding: 12px;
}

/* badge showing "Active" status */
.badge-active {
    -fx-background-color: #e8e8e8;
    -fx-border-color: #aaaaaa;
    -fx-border-width: 1px;
    -fx-font-size: 11px;
    -fx-font-weight: bold;
    -fx-text-fill: #333333;
    -fx-padding: 2px 8px;
}

/* badge showing "Delayed" status */
.badge-delayed {
    -fx-background-color: #cccccc;
    -fx-border-color: #999999;
    -fx-border-width: 1px;
    -fx-font-size: 11px;
    -fx-font-weight: bold;
    -fx-text-fill: #222222;
    -fx-padding: 2px 8px;
}

/* badge showing "In Transit" status */
.badge-transit {
    -fx-background-color: #d0d0d0;
    -fx-border-color: #999999;
    -fx-border-width: 1px;
    -fx-font-size: 11px;
    -fx-font-weight: bold;
    -fx-text-fill: #222222;
    -fx-padding: 2px 8px;
}

/* thin horizontal line used as a divider between sections */
.divider {
    -fx-background-color: #cccccc;
    -fx-min-height: 1px;
    -fx-max-height: 1px;
    -fx-pref-height: 1px;
}

/* bottom navigation bar */
.nav-bar {
    -fx-background-color: #ffffff;
    -fx-border-color: #cccccc;
    -fx-border-width: 1px 0 0 0;
    -fx-padding: 6px 0;
}

/* inactive nav button */
.nav-btn {
    -fx-background-color: transparent;
    -fx-text-fill: #888888;
    -fx-font-size: 11px;
    -fx-font-weight: bold;
    -fx-border-color: transparent;
    -fx-padding: 6px 8px;
    -fx-cursor: hand;
}

/* active/selected nav button */
.nav-btn-active {
    -fx-background-color: #dddddd;
    -fx-text-fill: #222222;
    -fx-font-size: 11px;
    -fx-font-weight: bold;
    -fx-border-color: transparent;
    -fx-padding: 6px 8px;
    -fx-cursor: hand;
}
.nav-btn:hover {
    -fx-background-color: #eeeeee;
    -fx-text-fill: #333333;
}

/* large stat number on admin dashboard */
.stat-number {
    -fx-font-size: 28px;
    -fx-font-weight: bold;
    -fx-text-fill: #222222;
}

/* small label under the stat number */
.stat-label {
    -fx-font-size: 11px;
    -fx-text-fill: #666666;
}

/* bold bus ID text like "BUS-101" */
.bus-id-label {
    -fx-font-size: 14px;
    -fx-font-weight: bold;
    -fx-text-fill: #222222;
}

/* smaller grey model name under bus ID */
.bus-model-label {
    -fx-font-size: 12px;
    -fx-text-fill: #666666;
}

/* tiny uppercase label above a value e.g. "CAPACITY" */
.meta-label {
    -fx-font-size: 10px;
    -fx-text-fill: #888888;
}

/* value text shown below a meta label */
.value-label {
    -fx-font-size: 13px;
    -fx-font-weight: bold;
    -fx-text-fill: #222222;
}

/* stops text like "Ratna Park → Koteshwor → Bhaktapur" */
.stops-label {
    -fx-font-size: 11px;
    -fx-text-fill: #666666;
}

/* table view used in admin screens */
.table-view {
    -fx-background-color: #ffffff;
    -fx-border-color: #cccccc;
    -fx-border-width: 1px;
}
.table-view .column-header-background {
    -fx-background-color: #dddddd;
}
.table-view .column-header .label {
    -fx-text-fill: #333333;
    -fx-font-size: 11px;
    -fx-font-weight: bold;
}
.table-row-cell {
    -fx-background-color: #ffffff;
    -fx-border-color: #eeeeee;
    -fx-border-width: 0 0 1px 0;
}
.table-row-cell:selected {
    -fx-background-color: #e8e8e8;
}
.table-cell {
    -fx-font-size: 12px;
    -fx-text-fill: #333333;
}
""";
    }
}
