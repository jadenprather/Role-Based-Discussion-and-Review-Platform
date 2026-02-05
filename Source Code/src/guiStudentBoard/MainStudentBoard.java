package guiStudentBoard;

import applicationMain.FoundationsMain;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Main entry point for the Student Discussion Board application (MVC pattern).
 *
 * <p>This class launches the JavaFX UI, initializes the controller with the
 * shared {@code ThreadService} from {@link applicationMain.FoundationsMain},
 * seeds a bit of demo data, and hands control to the view.</p>
 *
 * <p><b>Why we inject ThreadService here:</b> staff (in their GUI) and students
 * (in this GUI) must see the **same** set of threads. So we do **not** create a
 * controller with hardcoded threads — we reuse the app-wide service.</p>
 */
public class MainStudentBoard extends Application {

    @Override
    public void start(Stage stage) {
        // 1) build controller using the shared thread service (your version)
//        controllerStudentBoard controller =
//                new controllerStudentBoard(FoundationsMain.threadService);
    	controllerStudentBoard controller =
    		    new controllerStudentBoard(FoundationsMain.threadService, FoundationsMain.postRepo);

        // 2) optional demo seed (same as yours + your peer’s)
        controller.createPost("Alice", "How do I fix error X?", "General");
        controller.addReply(1, "Bob", "Try cleaning and rebuilding.");
        controller.addReply(1, "Charlie", "Check JDK version.");

        // 3) current user for the UI
        String currentUser = "Cherry";

        // 4) build view with controller + user
        ViewStudentBoard view = new ViewStudentBoard(controller, currentUser);

        // 5) stage stuff
        stage.setTitle("Student Board");
        stage.setScene(new Scene(view, 1100, 650));
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
