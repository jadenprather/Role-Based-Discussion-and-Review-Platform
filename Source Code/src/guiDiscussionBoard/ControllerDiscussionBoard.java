package guiDiscussionBoard;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import applicationMain.FoundationsMain;
import guiUserLogin.ViewUserLogin; // if you need it later
import javafx.collections.FXCollections;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ListView;
import javafx.scene.control.TextInputDialog;

// ✅ add this import
import guiStudentBoard.controllerStudentBoard;

/**
 * Controller for the staff Discussion Board page.
 * Button handlers here talk to the shared ThreadService and Discussion service,
 * then update the View via its public helpers.
 * @author Qnq1q
 */
public class ControllerDiscussionBoard {

    // --- simple popup ---
    private static void info(String title, String msg){
        Alert a = new Alert(AlertType.INFORMATION);
        a.setTitle(title);
        a.setHeaderText(title);
        a.setContentText(msg);
        a.showAndWait();
    }

    // --- Open the selected thread and show its posts ---
    protected static void openThread(String threadName){
        if (threadName == null) {
            info("Open Thread", "Please select a thread to open.");
            return;
        }

        // ✅ Build a controller using the shared services (no global controller)
        controllerStudentBoard svc =
            new controllerStudentBoard(FoundationsMain.threadService, FoundationsMain.postRepo);

        List<String> items = svc.searchPosts("", threadName).stream()
            .map(p -> "#" + p.getId() + " • " + p.getAuthor() + " — " +
                       (p.getContent().length() > 60 ? p.getContent().substring(0, 60) + "…" : p.getContent()))
            .collect(Collectors.toList());

        // Update the view
        ViewDiscussionBoard.showThread(threadName);
        ViewDiscussionBoard.updatePosts(items);
    }

    // --- Create a new thread via ThreadService; refresh the list view ---
    protected static void newThread(ListView<String> listView){
        TextInputDialog dialog = new TextInputDialog("");
        dialog.setTitle("New Thread");
        dialog.setHeaderText("Please enter Thread Name");
        dialog.setContentText("Thread Name:");

        Optional<String> res = dialog.showAndWait();
        if (res.isEmpty()) return;

        String name = res.get().trim();
        if (name.isEmpty()) {
            info("New Thread", "Thread name cannot be empty.");
            return;
        }

        if (name.length() > 15) {
            info("Thread Length", "Thread Name Cannot be longer than 15 characters");
            return;
        }

        for (int i = 0; i < name.length(); i++) {
            char currentChar = name.charAt(i);
            if ("`\\\"%^&*()_-|\\\\;/^".indexOf(currentChar) >= 0) {
                info("Invalid", "This post contains an invalid Character");
                return;
            }
        }

        boolean ok = FoundationsMain.threadService.addThread(name);
        if (!ok) {
            info("New Thread", "Thread already exists or invalid name.");
            return;
        }

        // Reload from the canonical source
        listView.setItems(FXCollections.observableArrayList(
                FoundationsMain.threadService.listThreads()
        ));
        listView.getSelectionModel().select(name);

        // Clear posts panel since nothing is opened yet
        ViewDiscussionBoard.hideThread();
    }

    // --- Delete the selected thread if empty; refresh the list view ---
    protected static void deleteThread(ListView<String> listView, String threadName){
        if (threadName == null) {
            info("Delete Thread", "Please select a thread to delete.");
            return;
        }

        // ✅ Use a fresh controller (shared repo underneath)
        controllerStudentBoard svc =
            new controllerStudentBoard(FoundationsMain.threadService, FoundationsMain.postRepo);

        var postsInThread = svc.searchPosts("", threadName);
        if (!postsInThread.isEmpty()) {
            info("Delete Thread", "Cannot delete a thread that still has posts.");
            return;
        }

        boolean ok = FoundationsMain.threadService.deleteThread(threadName);
        if (!ok) {
            info("Delete Thread", "Delete failed (thread not found or protected).");
            return;
        }

        // refresh the list from the service
        listView.setItems(FXCollections.observableArrayList(
                FoundationsMain.threadService.listThreads()
        ));
        ViewDiscussionBoard.hideThread();
    }
}
