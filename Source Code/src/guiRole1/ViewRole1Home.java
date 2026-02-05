package guiRole1;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import database.Database;
import entityClasses.User;
import guiUserUpdate.ViewUserUpdate;

public class ViewRole1Home {

    /*-*******************************************************************************************
     * Attributes
     */

    // App window dimensions
    private static double width = applicationMain.FoundationsMain.WINDOW_WIDTH;
    private static double height = applicationMain.FoundationsMain.WINDOW_HEIGHT;

    // GUI Area 1 (header): page title, current user, buttons
    protected static Label label_PageTitle = new Label();
    protected static Label label_UserDetails = new Label();
    protected static Button button_UpdateThisUser = new Button("Account Update");

    // NEW: Student Board navigation button (you asked for this)
    protected static Button button_StudentBoard = new Button("Student Board");

    // Separator
    protected static Line line_Separator1 = new Line(20, 95, width - 20, 95);

    // (GUI Area 2 is a stub in this role page)

    // Separator above footer
    protected static Line line_Separator4 = new Line(20, 525, width - 20, 525);

    // GUI Area 3 (footer): logout / quit
    protected static Button button_Logout = new Button("Logout");
    protected static Button button_Quit = new Button("Quit");

    // Singleton pattern for this view
    private static ViewRole1Home theView;

    // References shared across Role1 pages
    private static Database theDatabase = applicationMain.FoundationsMain.database;
    protected static Stage theStage;
    protected static Pane theRootPane;
    protected static User theUser;

    private static Scene theViewRole1HomeScene;
    protected static final int theRole = 2; // Admin:1; Role1:2; Role2:3

    /*-*******************************************************************************************
     * Public entry point
     */

    /**
     * Display Role1 Home page.
     */
    public static void displayRole1Home(Stage ps, User user) {
        // Establish the references to the GUI and the current user
        theStage = ps;
        theUser = user;

        // Instantiate singleton if needed
        if (theView == null) theView = new ViewRole1Home();

        // Populate dynamic aspects of the GUI
        theDatabase.getUserAccountDetails(user.getUserName());
        applicationMain.FoundationsMain.activeHomePage = theRole;

        label_UserDetails.setText("User: " + theUser.getUserName());

        // Show page
        theStage.setTitle("CSE 360 Foundations: Role1 Home Page");
        theStage.setScene(theViewRole1HomeScene);
        theStage.show();
    }

    /*-*******************************************************************************************
     * Constructor (singleton)
     */

    private ViewRole1Home() {
        // Root pane + scene
        theRootPane = new Pane();
        theViewRole1HomeScene = new Scene(theRootPane, width, height);

        // ===== GUI Area 1: Header =====
        label_PageTitle.setText("Role1 Home Page");
        setupLabelUI(label_PageTitle, "Arial", 28, width, Pos.CENTER, 0, 5);

        label_UserDetails.setText("User: " + (theUser == null ? "" : theUser.getUserName()));
        setupLabelUI(label_UserDetails, "Arial", 20, width, Pos.BASELINE_LEFT, 20, 55);

        // Existing Account Update button (right side)
        setupButtonUI(button_UpdateThisUser, "Dialog", 18, 170, Pos.CENTER, 610, 45);
        button_UpdateThisUser.setOnAction((event) ->
                ViewUserUpdate.displayUserUpdate(theStage, theUser));

        // NEW: Student Board button — placed to the LEFT of Account Update
        // Adjust X if you want different spacing.
        setupButtonUI(button_StudentBoard, "Dialog", 18, 170, Pos.CENTER, 430, 45);
        button_StudentBoard.setOnAction(e -> ControllerRole1Home.performOpenStudentBoard());

        // NOTE: Do NOT set an onAction here. The controller will wire it:
        // this.view.getBtnStudentBoard().setOnAction(e -> openStudentBoard());

        // ===== GUI Area 3: Footer =====
        setupButtonUI(button_Logout, "Dialog", 18, 250, Pos.CENTER, 20, 540);
        button_Logout.setOnAction((event) -> { ControllerRole1Home.performLogout(); });

        setupButtonUI(button_Quit, "Dialog", 18, 250, Pos.CENTER, 300, 540);
        button_Quit.setOnAction((event) -> { ControllerRole1Home.performQuit(); });

        // Add everything to root
        theRootPane.getChildren().addAll(
                label_PageTitle, label_UserDetails,
                button_StudentBoard,           // <-- NEW button added to scene graph
                button_UpdateThisUser,
                line_Separator1,
                line_Separator4,
                button_Logout, button_Quit
        );
    }

    /*-*******************************************************************************************
     * Public getter so ControllerRole1Home can attach an onAction handler
     */
    public Button getBtnStudentBoard() {
        return button_StudentBoard;
    }

    /*-*******************************************************************************************
     * Helper methods
     */

    private static void setupLabelUI(Label l, String ff, double f, double w, Pos p, double x, double y) {
        l.setFont(Font.font(ff, f));
        l.setMinWidth(w);
        l.setAlignment(p);
        l.setLayoutX(x);
        l.setLayoutY(y);
    }

    private static void setupButtonUI(Button b, String ff, double f, double w, Pos p, double x, double y) {
        b.setFont(Font.font(ff, f));
        b.setMinWidth(w);
        b.setAlignment(p);
        b.setLayoutX(x);
        b.setLayoutY(y);
    }
}
