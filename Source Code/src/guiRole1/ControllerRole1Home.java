package guiRole1;

import javafx.scene.Scene;
import javafx.stage.Stage;


import guiStudentBoard.ViewStudentBoard;
import guiStudentBoard.controllerStudentBoard;
import applicationMain.FoundationsMain;

public class ControllerRole1Home {

	/*-*******************************************************************************************

	User Interface Actions for this page
	
	This controller is not a class that gets instantiated.  Rather, it is a collection of protected
	static methods that can be called by the View (which is a singleton instantiated object) and 
	the Model is often just a stub, or will be a singleton instantiated object.
	
	 */

	
 	/**********
	 * <p> Method: performLogout() </p>
	 * 
	 * <p> Description: This method logs out the current user and proceeds to the normal login
	 * page where existing users can log in or potential new users with a invitation code can
	 * start the process of setting up an account. </p>
	 * 
	 */
	protected static void performLogout() {
		guiUserLogin.ViewUserLogin.displayUserLogin(ViewRole1Home.theStage);
	}
	

	
	
	/**********
	 * <p> Method: performQuit() </p>
	 * 
	 * <p> Description: This method terminates the execution of the program.  It leaves the
	 * database in a state where the normal login page will be displayed when the application is
	 * restarted.</p>
	 * 
	 */	
	protected static void performQuit() {
		System.exit(0);
	}
	
	
	protected static void performOpenStudentBoard() {
		// Build Student Board controller (in-memory model)
	    controllerStudentBoard boardController =
	            new controllerStudentBoard(FoundationsMain.threadService, FoundationsMain.postRepo);

		// Get the current user's name from the Role1 view (fallback to "student")
		String currentUser = "student";
		try {
			if (ViewRole1Home.theUser != null && ViewRole1Home.theUser.getUserName() != null) {
				currentUser = ViewRole1Home.theUser.getUserName();
			}
		} catch (Exception ignored) { }

		// Build the Student Board view
		ViewStudentBoard boardView = new ViewStudentBoard(boardController, currentUser);

		// Show in a new top-level window
		Stage stage = new Stage();
		stage.setTitle("Student Board");
		stage.setScene(new Scene(boardView, 1100, 650));
		stage.show();
	}
	
}