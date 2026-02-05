package guiRequests;

import java.util.stream.Collectors;

import applicationMain.FoundationsMain;
import database.Database;
import entityClasses.User;
import entityClasses.Request;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class ViewRequests {
	
	// Get size of the widow
	// These are the application values required by the user interface
    private static double width  = FoundationsMain.WINDOW_WIDTH;
    private static double height = FoundationsMain.WINDOW_HEIGHT;
    
    // Reference for the in-memory database so this package has access
    private static Database db = FoundationsMain.database;

    // The users's 
    private static ViewRequests theView; 
    private static Stage theStage;			// The Stage that JavaFX has established for us
    private static Scene theScene;			// The shared Scene each invocation populates
    private static Pane  root;				// The Pane that holds all the GUI widgets

    private static User theUser;			// The current logged in User

    // header Just like all thre rest
    private static Label  title = new Label("Requests");
    private static Label  userL = new Label();
    private static Button acct  = new Button("Account Update");
    private static Line   sep1  = new Line(20,95,width-20,95);

    // lists that show the data
    private static Label openL  = new Label("Open Requests");		// Label
    private static ListView<Request> openList = new ListView<>();	// The actual List
    
    private static Label closedL= new Label("Closed Requests");		
    private static ListView<Request> closedList = new ListView<>();
    
    // Showing the details that people left
    private static Label detailsL = new Label("Details");
    private static TextArea detailsTA = new TextArea();				
    

    // actions buttons
    private static Button newBtn   = new Button("New");
    private static Button noteCloseBtn = new Button("Add Note & Close (Admin)");
    private static Button reopenBtn = new Button("Reopen");

    // Everything below the last line.
    // Everything that was in the discussion board.
    private static Line sep4 = new Line(20,525,width-20,525);
    private static Button back = new Button("Return");
    private static Button logout = new Button("Logout");
    private static Button quit = new Button("Quit");

    // just make new page if not made yet
    public static void display(Stage ps, User user){
        theStage = ps;
        theUser  = user;
        if (theView == null) theView = new ViewRequests();

        userL.setText("User: " + user.getUserName());
        
        // NEW: load role flags for this user
        db.getUserAccountDetails(user.getUserName());
        
        theStage.setTitle("CSE 360 Foundations: Requests");
        refreshLists();
        theStage.setScene(theScene);
        theStage.show();
    }
    
    // AKA setting up the actually meat of the page
    private ViewRequests(){
    	
        root = new Pane();
        theScene = new Scene(root, width, height);
        
        // GUI area 1:
        setupLabel(title, "Arial", 28, width, Pos.CENTER, 0, 5);
        setupLabel(userL,  "Arial", 20, width, Pos.BASELINE_LEFT, 20, 55);
        setupButton(acct,  "Dialog", 18, 170, Pos.CENTER, 610, 45);
        acct.setOnAction(e -> guiUserUpdate.ViewUserUpdate.displayUserUpdate(theStage, theUser));

        // GUI area 2:
        // lists of threads
        setupLabel(openL, "Arial", 18, 300, Pos.BASELINE_LEFT, 20, 110);
        openList.setLayoutX(20); openList.setLayoutY(140); openList.setPrefSize(210, 320);

        setupLabel(closedL, "Arial", 18, 300, Pos.BASELINE_LEFT, 250, 110);
        closedList.setLayoutX(250); closedList.setLayoutY(140); closedList.setPrefSize(210, 320);
        
     // details (spans the width under both lists) when needed
        setupLabel(detailsL, "Arial", 18, 300, Pos.BASELINE_LEFT, 500, 110);
        detailsTA.setLayoutX(500);
        detailsTA.setLayoutY(140);
        detailsTA.setPrefSize(270, 320);
        detailsTA.setEditable(false);
        detailsTA.setWrapText(true);
        
     // selection listeners to update details
        openList.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldReq, newReq) -> showDetails(newReq)
        );
        closedList.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldReq, newReq) -> showDetails(newReq)
        );

        // button & button actions
        setupButton(newBtn, "Dialog", 16, 150, Pos.CENTER, 50, 485);
        newBtn.setOnAction(e -> ControllerRequests.newRequest(theStage, theUser));

        setupButton(noteCloseBtn, "Dialog", 16, 230, Pos.CENTER, 280, 485);
        noteCloseBtn.setOnAction(e -> {
            Request sel = openList.getSelectionModel().getSelectedItem();
            ControllerRequests.addNoteAndClose(theStage, sel, theUser);
            refreshLists();
        });

        setupButton(reopenBtn, "Dialog", 16, 150, Pos.CENTER, 580, 485);
        reopenBtn.setOnAction(e -> {
            Request sel = closedList.getSelectionModel().getSelectedItem();
            ControllerRequests.reopenRequest(theStage, sel, theUser);
            refreshLists();
        });

        // GUI area 3:
        setupButton(back, "Dialog", 18, 220, Pos.CENTER, 20, 540);
        back.setOnAction(e -> guiRole2.ViewRole2Home.displayRole2Home(theStage, theUser));
        
        setupButton(logout, "Dialog", 18, 220, Pos.CENTER, 290, 540);
        logout.setOnAction(e -> guiUserLogin.ViewUserLogin.displayUserLogin(theStage));
        
        setupButton(quit, "Dialog", 18, 220, Pos.CENTER, 550, 540);
        quit.setOnAction(e -> System.exit(0));

        // Adding it all to the pane.
        root.getChildren().addAll(
        	    title, userL, acct, sep1,
        	    openL, openList, closedL, closedList,
        	    sep4, newBtn, noteCloseBtn, reopenBtn,
        	    back, logout, quit
        	);
    }

    // Just refresh the page.
    static void refreshLists(){
        openList.setItems(FXCollections.observableArrayList(db.getOpenRequests()));
        closedList.setItems(FXCollections.observableArrayList(db.getClosedRequests()));

        // Only admins can see the close button
        noteCloseBtn.setDisable(!db.getCurrentAdminRole());
        
        detailsTA.clear();  // reset details when refreshing
    }

    // helpers
    private static void setupLabel(Label l, String ff, double f, double w, Pos p, double x, double y){
        l.setFont(Font.font(ff, f)); l.setMinWidth(w); l.setAlignment(p); l.setLayoutX(x); l.setLayoutY(y);
    }
    private static void setupButton(Button b, String ff, double f, double w, Pos p, double x, double y){
        b.setFont(Font.font(ff, f)); b.setMinWidth(w); b.setAlignment(p); b.setLayoutX(x); b.setLayoutY(y);
    }
    
    static void showDetails(entityClasses.Request req) {
        if (req == null) {
            detailsTA.clear();
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("#").append(req.getId())
          .append(" • ").append(req.getTitle())
          .append(" [").append(req.getStatus()).append("]\n");

        if (req.getCreatedBy() != null || req.getCreatedAt() != null) {
            sb.append("Created by: ")
              .append(nullToEmpty(req.getCreatedBy()))
              .append("  at: ").append(req.getCreatedAt())
              .append("\n");
        }

        if (req.getParentId() != null) {
            sb.append("Reopened from request #").append(req.getParentId()).append("\n");
        }

        sb.append("\nDescription:\n")
          .append(nullToEmpty(req.getDescription()).isBlank() ? "(none)" : req.getDescription())
          .append("\n\nAdmin notes:\n");

        String notes = req.getAdminNotes();
        sb.append((notes == null || notes.isBlank()) ? "(none)" : notes.trim());

        detailsTA.setText(sb.toString());
    }

    private static String nullToEmpty(String s) { return s == null ? "" : s; }
}
