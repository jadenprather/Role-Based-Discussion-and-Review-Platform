package guiAdminHome;

import database.Database;

/*******
 * <p> Title: GUIAdminHomePage Class. </p>
 * 
 * <p> Description: The Java/FX-based Admin Home Page.  This class provides the controller actions
 * basic on the user's use of the JavaFX GUI widgets defined by the View class.
 * 
 * This page contains a number of buttons that have not yet been implemented.  WHen those buttons
 * are pressed, an alert pops up to tell the user that the function associated with the button has
 * not been implemented. Also, be aware that What has been implemented may not work the way the
 * final product requires and there maybe defects in this code.
 * 
 * The class has been written assuming that the View or the Model are the only class methods that
 * can invoke these methods.  This is why each has been declared at "protected".  Do not change any
 * of these methods to public.</p>
 * 
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 * 
 * @author Lynn Robert Carter
 * 
 * @version 1.00		2025-08-17 Initial version
 *  
 */

public class ControllerAdminHome {
	
	/*-*******************************************************************************************

	User Interface Actions for this page
	
	This controller is not a class that gets instantiated.  Rather, it is a collection of protected
	static methods that can be called by the View (which is a singleton instantiated object) and 
	the Model is often just a stub, or will be a singleton instantiated object.
	
	*/

	// Reference for the in-memory database so this package has access
	private static Database theDatabase = applicationMain.FoundationsMain.database;

	/**********
	 * <p> 
	 * 
	 * Title: performInvitation () Method. </p>
	 * 
	 * <p> Description: Protected method to send an email inviting a potential user to establish
	 * an account and a specific role. </p>
	 */
	protected static void performInvitation () {
		// Verify that the email address is valid - If not alert the user and return
		String emailAddress = ViewAdminHome.text_InvitationEmailAddress.getText();
		if (invalidEmailAddress(emailAddress)) {
			return;
		}
		
		// Check to ensure that we are not sending a second message with a new invitation code to
		// the same email address.  
		if (theDatabase.emailaddressHasBeenUsed(emailAddress)) {
			ViewAdminHome.alertEmailError.setContentText(
					"An invitation has already been sent to this email address.");
			ViewAdminHome.alertEmailError.showAndWait();
			return;
		}
		
		// Inform the user that the invitation has been sent and display the invitation code
		String theSelectedRole = (String) ViewAdminHome.combobox_SelectRole.getValue();
		String invitationCode = theDatabase.generateInvitationCode(emailAddress,
				theSelectedRole);
		String msg = "Code: " + invitationCode + " for role " + theSelectedRole + 
				" was sent to: " + emailAddress;
		System.out.println(msg);
		ViewAdminHome.alertEmailSent.setContentText(msg);
		ViewAdminHome.alertEmailSent.showAndWait();
		
		// Update the Admin Home pages status
		ViewAdminHome.text_InvitationEmailAddress.setText("");
		ViewAdminHome.label_NumberOfInvitations.setText("Number of outstanding invitations: " + 
				theDatabase.getNumberOfInvitations());
	}
	
	/**********
	 * <p> 
	 * 
	 * Title: manageInvitations () Method. </p>
	 * 
	 * <p> Description: Protected method that is currently a stub informing the user that
	 * this function has not yet been implemented. </p>
	 */
	protected static void manageInvitations () {
		System.out.println("\n*** WARNING ***: Manage Invitations Not Yet Implemented");
		ViewAdminHome.alertNotImplemented.setTitle("*** WARNING ***");
		ViewAdminHome.alertNotImplemented.setHeaderText("Manage Invitations Issue");
		ViewAdminHome.alertNotImplemented.setContentText("Manage Invitations Not Yet Implemented");
		ViewAdminHome.alertNotImplemented.showAndWait();
	}
	
	/**********
	 * <p> 
	 * 
	 * Title: setOnetimePassword () Method. </p>
	 * 
	 * <p> Description: Protected method that is currently a stub informing the user that
	 * this function has not yet been implemented. </p>
	 */
	protected static void setOnetimePassword () {
	    // Ask for username
	    var userDialog = new javafx.scene.control.TextInputDialog();
	    userDialog.setTitle("One-Time Password");
	    userDialog.setHeaderText("Generate a one-time password for a user");
	    userDialog.setContentText("Username:");
	    String uname = userDialog.showAndWait().orElse("");
	    if (uname == null || uname.isBlank()) return;

	    // Generate a short temp password (visible demo)
	    String temp = java.util.UUID.randomUUID().toString().replace("-", "").substring(0, 8);

	    boolean ok = theDatabase.updatePassword(uname, temp);
	    if (ok) {
	        var msg = "Set one-time password for '" + uname + "': " + temp + "\n"
	                + "(Tell the user to log in and change it immediately.)";
	        ViewAdminHome.alertNotImplemented.setTitle("One-Time Password");
	        ViewAdminHome.alertNotImplemented.setHeaderText("Success");
	        ViewAdminHome.alertNotImplemented.setContentText(msg);
	        ViewAdminHome.alertNotImplemented.showAndWait();
	    } else {
	        ViewAdminHome.alertNotImplemented.setTitle("One-Time Password");
	        ViewAdminHome.alertNotImplemented.setHeaderText("Failed");
	        ViewAdminHome.alertNotImplemented.setContentText("User not found or update failed.");
	        ViewAdminHome.alertNotImplemented.showAndWait();
	    }
	}
	
	/**********
	 * <p> 
	 * 
	 * Title: deleteUser () Method. </p>
	 * 
	 * <p> Description: Protected method that is currently a stub informing the user that
	 * this function has not yet been implemented. </p>
	 */
	protected static void deleteUser() {
	    // Ask username
	    var td = new javafx.scene.control.TextInputDialog();
	    td.setTitle("Delete User");
	    td.setHeaderText("Delete a user account");
	    td.setContentText("Username to delete:");
	    String uname = td.showAndWait().orElse("");
	    if (uname == null || uname.isBlank()) return;

	    // Confirm
	    var confirm = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.CONFIRMATION,
	            "ARE YOU SURE you want to delete '" + uname + "'?");
	    confirm.getButtonTypes().setAll(javafx.scene.control.ButtonType.YES, javafx.scene.control.ButtonType.NO);
	    boolean yes = confirm.showAndWait().orElse(javafx.scene.control.ButtonType.NO)
	            == javafx.scene.control.ButtonType.YES;
	    if (!yes) return;

	    boolean ok = theDatabase.deleteUserByUsername(uname);
	    if (ok) {
	        var a = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION,
	                "User '" + uname + "' deleted.");
	        a.showAndWait();
	    } else {
	        var e = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR,
	                "Delete failed. (User may not exist.)");
	        e.showAndWait();
	    }
	}

	
	/**********
	 * <p> 
	 * 
	 * Title: listUsers () Method. </p>
	 * 
	 * <p> Description: Protected method that is currently a stub informing the user that
	 * this function has not yet been implemented. </p>
	 */
	protected static void listUsers() {
	    try {
	        var names = theDatabase.getUserList();
	        if (names == null || names.isEmpty()) {
	            ViewAdminHome.alertNotImplemented.setTitle("All Users");
	            ViewAdminHome.alertNotImplemented.setHeaderText("No users found");
	            ViewAdminHome.alertNotImplemented.setContentText("The user table appears to be empty.");
	            ViewAdminHome.alertNotImplemented.showAndWait();
	            return;
	        }

	        // Build a readable table
	        StringBuilder sb = new StringBuilder();
	        sb.append(String.format("%-16s  %-22s  %-28s  %-6s %-8s %-6s%n",
	                "Username","Name","Email","Admin","Student","Staff"));
	        sb.append("------------------------------------------------------------------------------------------\n");

	        // Skip the first "<Select a User>" entry if present
	        for (String uname : names) {
	            if (uname == null || uname.startsWith("<")) continue;

	            boolean ok = theDatabase.getUserAccountDetails(uname);
	            if (!ok) continue;

	            String first = theDatabase.getCurrentFirstName();
	            String last  = theDatabase.getCurrentLastName();
	            String email = theDatabase.getCurrentEmailAddress();
	            boolean a = theDatabase.getCurrentAdminRole();
	            boolean r1 = theDatabase.getCurrentNewRole1(); // Student
	            boolean r2 = theDatabase.getCurrentNewRole2(); // Staff

	            String full = ((first == null ? "" : first) + " " + (last == null ? "" : last)).trim();
	            sb.append(String.format("%-16s  %-22s  %-28s  %-6s %-8s %-6s%n",
	                    uname, full, (email == null ? "" : email),
	                    a ? "Y" : " ", r1 ? "Y" : " ", r2 ? "Y" : " "));
	        }

	        var a = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
	        a.setTitle("All Users");
	        a.setHeaderText("Username / Name / Email / Roles (Admin, Student, Staff)");
	        a.setContentText(sb.toString());
	        var ta = new javafx.scene.control.TextArea(sb.toString());
	        ta.setEditable(false);
	        a.getDialogPane().setExpandableContent(ta);
	        a.getDialogPane().setExpanded(true);
	        a.showAndWait();

	    } catch (Exception ex) {
	        ViewAdminHome.alertNotImplemented.setTitle("List Users");
	        ViewAdminHome.alertNotImplemented.setHeaderText("Error");
	        ViewAdminHome.alertNotImplemented.setContentText(ex.getMessage());
	        ViewAdminHome.alertNotImplemented.showAndWait();
	    }
	}

	
	
	/**********
	 * <p> 
	 * 
	 * Title: addRemoveRoles () Method. </p>
	 * 
	 * <p> Description: Protected method that allows an admin to add and remove roles for any of
	 * the users currently in the system.  This is done by invoking the AddRemoveRoles Page. There
	 * is no need to specify the home page for the return as this can only be initiated by and
	 * Admin.</p>
	 */
	protected static void addRemoveRoles() {
		guiAddRemoveRoles.ViewAddRemoveRoles.displayAddRemoveRoles(ViewAdminHome.theStage, 
				ViewAdminHome.theUser);
	}

	/**********
	 * <p> 
	 * 
	 * Title: invalidEmailAddress () Method. </p>
	 * 
	 * <p> Description: Protected method that is intended to check an email address before it is
	 * used to reduce errors.  The code currently only checks to see that the email address is not
	 * empty.  In the future, a syntactic check must be performed and maybe there is a way to check
	 * if a properly email address is active.</p>
	 * 
	 * @param emailAddress	This String holds what is expected to be an email address
	 */
	protected static boolean invalidEmailAddress(String emailAddress) {
	    if (!guiTools.EmailValidator.isValid(emailAddress)) {
	        String msg = guiTools.EmailValidator.error(emailAddress);
	        if (msg == null || msg.isBlank()) msg = "Correct the email address and try again.";
	        ViewAdminHome.alertEmailError.setContentText(msg);
	        ViewAdminHome.alertEmailError.showAndWait();
	        return true;
	    }
	    return false;
	}
	
	/**********
	 * <p> 
	 * 
	 * Title: performLogout () Method. </p>
	 * 
	 * <p> Description: Protected method that logs this user out of the system and returns to the
	 * login page for future use.</p>
	 */
	protected static void performLogout() {
		guiUserLogin.ViewUserLogin.displayUserLogin(ViewAdminHome.theStage);
	}
	
	/**********
	 * <p> 
	 * 
	 * Title: performQuit () Method. </p>
	 * 
	 * <p> Description: Protected method that gracefully terminates the execution of the program.
	 * </p>
	 */
	protected static void performQuit() {
		System.exit(0);
	}
}
