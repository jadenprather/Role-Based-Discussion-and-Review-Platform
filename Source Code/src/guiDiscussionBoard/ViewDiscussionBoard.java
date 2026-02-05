package guiDiscussionBoard;

import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import database.Database;
import entityClasses.User;
import guiRole2.ControllerRole2Home;
import applicationMain.FoundationsMain;
import services.ThreadService;


public class ViewDiscussionBoard {
	/*-*******************************************************************************************

	Attributes
	
	*/

	// These are the application values required by the user interface
	private static double width = applicationMain.FoundationsMain.WINDOW_WIDTH;
	private static double height = applicationMain.FoundationsMain.WINDOW_HEIGHT;

	
	// These are the widget attributes for the GUI. There are 3 areas for this GUI.
	
	// GUI Area 1: It informs the user about the purpose of this page, whose account is being used,
	// and a button to allow this user to update the account settings.
	protected static Label label_PageTitle = new Label("Discussion Board");
	protected static Label label_UserDetails = new Label();
	protected static Button button_UpdateThisUser = new Button("Account Update");
	
	// This is a separator and it is used to partition the GUI for various tasks
	protected static Line line_Separator1 = new Line(20, 95, width-20, 95);
	
	
	// GUI Area 2: Where the actions actually happen
	// Where the Current Threads are.
    private static final Label label_Threads = new Label("Threads:");
    
    //ListView<String> is javaFX Scrollable display of strings
    // Right now its just examples 
    private static final ListView<String> list_Threads  = new ListView<>();
    
    //The showing of the thread & keep hidden for now
    private static final Label label_Posts = new Label("Posts:");
    private static final ListView<String> list_Posts = new ListView<>();
    static {
        label_Posts.setVisible(false);
        list_Posts.setVisible(false);
    }
    
    // The buttons for this
    private static final Button button_AddThread 	= new Button("Add Thread");
    private static final Button button_OpenThread   = new Button("Open Thread");
    private static final Button button_NewThread    = new Button("New Thread");
    private static final Button button_DeleteThread = new Button("Delete");
	
		
	// This is a separator and it is used to partition the GUI for various tasks
	protected static Line line_Separator4 = new Line(20, 525, width-20,525);
	
	// GUI Area 3: This is last of the GUI areas.  It is used for quitting the application, logging
	// out, and on other pages a return is provided so the user can return to a previous page when
	// the actions on that page are complete.  Be advised that in most cases in this code, the 
	// return is to a fixed page as opposed to the actual page that invoked the pages.
	protected static Button button_Return = new Button("Return");
	protected static Button button_Logout = new Button("Logout");
	protected static Button button_Quit = new Button("Quit");

	// This is the end of the GUI objects for the page.
	
	// These attributes are used to configure the page and populate it with this user's information
	private static ViewDiscussionBoard theView;	// Used to determine if instantiation of the class
												// is needed
	// Reference for the in-memory database so this package has access
	private static Database theDatabase = applicationMain.FoundationsMain.database;	

	protected static Stage theStage;			// The Stage that JavaFX has established for us
	protected static Pane theRootPane;			// The Pane that holds all the GUI widgets 
	protected static User theUser;				// The current user of the application
	
	private static Scene theDiscussionScene;

	
	/*-*******************************************************************************************

	Constructors
	
	*/

	/**********
	 * <p> Method: display(Stage ps, User user) </p>
	 * 
	 * <p> Description: This method is the single entry point from outside this package to cause
	 * the AddRevove page to be displayed.
	 * 
	 * It first sets up very shared attributes so we don't have to pass parameters.
	 * 
	 * It then checks to see if the page has been setup.  If not, it instantiates the class, 
	 * initializes all the static aspects of the GUI widgets (e.g., location on the page, font,
	 * size, and any methods to be performed).
	 * 
	 * After the instantiation, the code then populates the elements that change based on the user
	 * and the system's current state.  It then sets the Scene onto the stage, and makes it visible
	 * to the user.
	 * 
	 * @param ps specifies the JavaFX Stage to be used for this GUI and it's methods
	 * 
	 * @param user specifies the User whose roles will be updated
	 *
	 */
	 public static void display(Stage ps, User user) {
		 
		// Establish the references to the GUI and the current user
	        theStage = ps;
	        theUser  = user;

	     // If not yet established, populate the static aspects of the GUI
	        if (theView == null) theView = new ViewDiscussionBoard();  // build once

			// Populate the dynamic aspects of the GUI with the data from the user and the current
			// state of the system.
	        theDatabase.getUserAccountDetails(user.getUserName());
	        applicationMain.FoundationsMain.activeHomePage = 3; // Role2

	        label_UserDetails.setText("User: " + theUser.getUserName());
	        list_Threads.setItems(
	        	    FXCollections.observableArrayList(FoundationsMain.threadService.listThreads())
	        	);

	     // Set the title for the window, display the page, and wait for the Staff to do something
	        theStage.setTitle("CSE 360 Foundations: Discussion Board");
	        theStage.setScene(theDiscussionScene);
	        theStage.show();
	    }

	     //Create the Pane for the list of widgets and the Scene for the window
	    private ViewDiscussionBoard() {
	        theRootPane = new Pane();
	        theDiscussionScene = new Scene(theRootPane, width, height);

	        // GUI: Area 1
	        setupLabel(label_PageTitle, "Arial", 28, width, Pos.CENTER, 0, 5);
	        setupLabel(label_UserDetails, "Arial", 20, width, Pos.BASELINE_LEFT, 20, 55);

	        setupButton(button_UpdateThisUser, "Dialog", 18, 170, Pos.CENTER, 610, 45);
	        button_UpdateThisUser.setOnAction(e ->
	            guiUserUpdate.ViewUserUpdate.displayUserUpdate(theStage, theUser)
	        );

	        // GUI: Area 2
		        setupLabel(label_Threads, "Arial", 18, 200, Pos.BASELINE_LEFT, 20, 110);
		        
		       //Text Box existing Threads 
		        list_Threads.setLayoutX(20);
		        list_Threads.setLayoutY(140);
		        list_Threads.setPrefSize(180, 350);

		        // Text box showing what's inside of that Discussion Thread
		        setupLabel(label_Posts, "Arial", 18, 400, Pos.BASELINE_LEFT, 220, 110);
		        list_Posts.setLayoutX(220);
		        list_Posts.setLayoutY(140);
		        list_Posts.setPrefSize(530, 330);
		        
		        
	        // The buttons
		    // on click go to the controller and right method
	        setupButton(button_OpenThread, "Dialog", 16, 130, Pos.CENTER, 220, 480);
	        
	        // Get the item selected and pass it
	        button_OpenThread.setOnAction((event) -> 
	        {ControllerDiscussionBoard.openThread(list_Threads.getSelectionModel().getSelectedItem()); });
	        
	        setupButton(button_NewThread,  "Dialog", 16, 130, Pos.CENTER, 410, 480);
	        
	        // Pass it the thread
	        button_NewThread.setOnAction((event) -> {ControllerDiscussionBoard.newThread(list_Threads); });
	        
	        button_AddThread.setOnAction(e -> {
	            TextInputDialog dlg = new TextInputDialog();
	            dlg.setHeaderText("Enter new thread name:");
	            dlg.showAndWait().ifPresent(name -> {
	                if (FoundationsMain.threadService.addThread(name.trim())) {
	                    refreshThreadsList();
	                } else {
	                    new Alert(Alert.AlertType.ERROR, "Thread already exists or invalid name.").showAndWait();
	                }
	            });
	        });
	        
	        //setupButton(button_AddThread, "Dialog", 16, 130, Pos.CENTER, 30, 480);
	        
	        setupButton(button_DeleteThread,"Dialog",16, 130, Pos.CENTER, 600, 480);
	        
	        // Delete selected item in the thread
	        button_DeleteThread.setOnAction((event) -> 
	        {ControllerDiscussionBoard.deleteThread(list_Threads, list_Threads.getSelectionModel().getSelectedItem() ); });
	        

	        // --- Area 3
	        setupButton(button_Return, "Dialog", 18, 220, Pos.CENTER, 20, 540);
	        button_Return.setOnAction(e ->
	            guiRole2.ViewRole2Home.displayRole2Home(theStage, theUser)
	        );

	        setupButton(button_Logout, "Dialog", 18, 220, Pos.CENTER, 290, 540);
	        button_Logout.setOnAction(e ->
	            guiUserLogin.ViewUserLogin.displayUserLogin(theStage)
	        );

	        setupButton(button_Quit, "Dialog", 18, 220, Pos.CENTER, 550, 540);
	        button_Quit.setOnAction(e -> System.exit(0));

	        // add to root Pane to show
	        theRootPane.getChildren().addAll(
	        	    label_PageTitle, label_UserDetails, button_UpdateThisUser,
	        	    line_Separator1,
	        	    label_Threads, list_Threads,
	        	    label_Posts, list_Posts,
	        	    button_OpenThread, button_NewThread, button_DeleteThread, //button_AddThread,
	        	    line_Separator4,
	        	    button_Return, button_Logout, button_Quit
	        	);
	    }

	    // --- tiny UI helpers (match house style) ---
	    private static void setupLabel(Label l, String ff, double f, double w, Pos p, double x, double y){
	        l.setFont(Font.font(ff, f));
	        l.setMinWidth(w);
	        l.setAlignment(p);
	        l.setLayoutX(x);
	        l.setLayoutY(y);
	    }

	    private static void setupButton(Button b, String ff, double f, double w, Pos p, double x, double y){
	        b.setFont(Font.font(ff, f));
	        b.setMinWidth(w);
	        b.setAlignment(p);
	        b.setLayoutX(x);
	        b.setLayoutY(y);
	    }
	    
	    protected static void showThread(String threadName) {
	        if (threadName == null) return;

	        label_Posts.setText("Posts in: " + threadName);
	       // list_Posts.setItems(demoPosts);

	        // reveal the posts panel
	        label_Posts.setVisible(true);
	        list_Posts.setVisible(true);
	    }
	    
	    protected static void hideThread() {
	        label_Posts.setVisible(false);
	        list_Posts.setVisible(false);
	        list_Posts.getItems().clear();
	    }
	    
	    private void refreshThreadsList() {
	        list_Threads.setItems(
	            FXCollections.observableArrayList(FoundationsMain.threadService.listThreads())
	        );
	    }
	    protected static void updatePosts(java.util.List<String> items) {
	        list_Posts.getItems().setAll(items);
	    }
	
}