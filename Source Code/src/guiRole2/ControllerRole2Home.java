package guiRole2;


public class ControllerRole2Home {
	
	/*-*******************************************************************************************

	User Interface Actions for this page
	
	**********************************************************************************************/
	
	protected static void performUpdate () {
		guiUserUpdate.ViewUserUpdate.displayUserUpdate(ViewRole2Home.theStage, ViewRole2Home.theUser);
	}	

	
	protected static void performLogout() {
		guiUserLogin.ViewUserLogin.displayUserLogin(ViewRole2Home.theStage);
	}
	
	protected static void performQuit() {
		System.exit(0);
	}
	
	// My code
	protected static void reviewStudent() {
		guiStaffReview.ViewStaffReview.display(
		        guiRole2.ViewRole2Home.theStage, 
		        guiRole2.ViewRole2Home.theUser
		    );
		return;
	}

	// go to guiDiscussionBoard -> ViewDiscussionBoard and set up the GUI
	// Establish the references to the GUI and the current user
	protected static void discussionThread() {
	    guiDiscussionBoard.ViewDiscussionBoard.display(
	        guiRole2.ViewRole2Home.theStage,
	        guiRole2.ViewRole2Home.theUser
	    );
	}
	
	protected static void parameter() {
		// Pass in the info so you can display the gui
		guiParameters.ViewParameters.display(guiRole2.ViewRole2Home.theStage, 
				guiRole2.ViewRole2Home.theUser);
		return;
	}
	
	protected static void makeRequest() {
	    guiRequests.ViewRequests.display(
	            ViewRole2Home.theStage,
	            ViewRole2Home.theUser
	        );
	}





	

}