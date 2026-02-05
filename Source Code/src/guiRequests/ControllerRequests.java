package guiRequests;

import applicationMain.FoundationsMain;
import database.Database;
import entityClasses.Request;
import entityClasses.User;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextInputDialog;

public class ControllerRequests {

	// Its the shared DATABASE
    private static Database db = FoundationsMain.database;

    //Requires the userId and the who is making it
    static void newRequest(javafx.stage.Stage stage, User user){
    	// When making a request ask for a title
        TextInputDialog t = new TextInputDialog("");
        t.setTitle("New Request");
        t.setHeaderText("Enter a short title");
        t.setContentText("Title:");
        var titleOpt = t.showAndWait();
        if (titleOpt.isEmpty()) return;

        // Making the Description
        TextArea ta = new TextArea();
        ta.setPrefRowCount(6);
        var d = new javafx.scene.control.Dialog<String>();
        d.setTitle("Request Description");
        d.setHeaderText("Describe the action admins should take");
        var ok = javafx.scene.control.ButtonType.OK;
        d.getDialogPane().getButtonTypes().addAll(ok, javafx.scene.control.ButtonType.CANCEL);
        d.getDialogPane().setContent(ta);
        d.setResultConverter(bt -> bt == ok ? ta.getText() : null);
        var desc = d.showAndWait().orElse(null);
        if (desc == null) return;
        
        //We are saving it to the DATABASE
        int id = db.createRequest(titleOpt.get().trim(), desc, user.getUserName());
        if (id < 0) info("New Request", "Unable to create request.");
        ViewRequests.refreshLists();
    }

    // Make sure the User is an admin
    static void addNoteAndClose(javafx.stage.Stage stage, Request req, User admin){
        if (req == null) { info("Close", "Select an OPEN request first."); return; }
        if (!db.getCurrentAdminRole()) { info("Close", "Only admins can close requests."); return; }

        //Making the note
        TextInputDialog t = new TextInputDialog("");
        t.setTitle("Admin Note");
        t.setHeaderText("Add an admin note before closing");
        t.setContentText("Note:");
        var v = t.showAndWait();
        if (v.isEmpty()) return;

        // SAVE THAT NOTE to the DATABASE
        // Tells the DB to attach the note and mark the request closed.
        if (!db.addAdminNoteAndClose(req.getId(), admin.getUserName(), v.get().trim()))
            info("Close", "Failed to close request.");
    }

    
    static void reopenRequest(javafx.stage.Stage stage, Request req, User user){
    	// Check if selected request is closed
        if (req == null) { info("Reopen", "Select a CLOSED request first."); return; }

        // Telling why they reopened it
        TextInputDialog t = new TextInputDialog("");
        t.setTitle("Reopen Request");
        t.setHeaderText("Explain why this is being reopened");
        t.setContentText("Description:");
        var v = t.showAndWait();
        if (v.isEmpty()) return;

        //Tell Database to reopen it with the reason why.
        if (!db.reopenRequest(req.getId(), user.getUserName(), v.get().trim()))
            info("Reopen", "Failed to reopen request.");
    }

    //Convenience function to show an info popup.
    private static void info(String title, String msg){
        var a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle(title); a.setHeaderText(title); a.setContentText(msg); a.showAndWait();
    }
}
