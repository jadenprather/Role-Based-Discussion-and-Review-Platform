package guiStaffReview;

import applicationMain.FoundationsMain;
import database.Database;
import entityClasses.User;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import review.ReviewService;
import review.ReviewService.Feedback;
import guiStudentBoard.controllerStudentBoard;
import guiStudentBoard.controllerStudentBoard.Reply;
import entityClasses.Post;

/**
 * This view is used by staff members to review posts, replies,
 * and any feedback attached to them. Staff can browse threads,
 * read discussions, and add feedback for students.
 */
public class ViewStaffReview {

    private static final double width  = FoundationsMain.WINDOW_WIDTH;
    private static final double height = FoundationsMain.WINDOW_HEIGHT;

    /** Shared student board controller (created once). */
    public static controllerStudentBoard SR;

    /** Shared services used throughout the screen. */
    public static final ReviewService RV = FoundationsMain.reviewService;
    public static final Database DB      = FoundationsMain.database;

    /** Singleton view wiring. */
    private static ViewStaffReview theView;
    public static Stage theStage;
    public static Scene theScene;
    public static Pane  root;
    public static User  theUser;

    /** Header controls. */
    public static final Label  title = new Label("Staff Review");
    public static final Label  who   = new Label();
    public static final Button acct  = new Button("Account Update");
    public static final Line   sep1  = new Line(20,95,width-20,95);

    /** Thread list. */
    public static final Label threadsL = new Label("Threads");
    public static final ListView<String> threadsLV = new ListView<>();

    /** Posts list. */
    public static final Label postsL = new Label("Posts");
    public static final ListView<Post> postsLV = new ListView<>();

    /** Replies list. */
    public static final Label repliesL = new Label("Replies");
    public static final ListView<Reply> repliesLV = new ListView<>();

    /** Feedback + details section. */
    public static final Label    detailsL = new Label("Details");
    public static final TextArea detailsTA = new TextArea();

    public static final Label fbL = new Label("Feedback (for selected Post/Reply)");
    public static final ListView<Feedback> fbLV = new ListView<>();
    public static final Button addFbBtn = new Button("Add Feedback");

    /** Footer buttons. */
    public static final Line   sep4   = new Line(20,525,width-20,525);
    public static final Button back   = new Button("Return");
    public static final Button logout = new Button("Logout");
    public static final Button quit   = new Button("Quit");

    /**
     * Displays the staff review window and initializes all controls.
     * This method is called when a staff user enters the review screen.
     */
    public static void display(Stage ps, User staff) {
        theStage = ps;
        theUser = staff;

        // Create the controller only once. This ensures posts and replies
        // stay consistent while staff navigates the review system.
        if (SR == null) {
            SR = new controllerStudentBoard(
                    FoundationsMain.threadService,
                    FoundationsMain.postRepo
            );
        }

        if (theView == null) {
            theView = new ViewStaffReview();
        }

        // Load staff account info for display.
        DB.getUserAccountDetails(staff.getUserName());

        who.setText("Staff: " + staff.getUserName());
        theStage.setTitle("CSE 360 Foundations: Staff Review");

        // Initial thread list.
        threadsLV.getItems().setAll(FoundationsMain.threadService.listThreads());

        // Set up how posts and replies appear visually.
        ControllerStaffReview.formatters(postsLV, repliesLV);

        // Display the scene.
        theStage.setScene(theScene);
        theStage.show();

        // Automatically load the first thread when the screen opens.
        if (!threadsLV.getItems().isEmpty()) {
            threadsLV.getSelectionModel().select(0);
            ControllerStaffReview.loadPostsForThread(
                    SR,
                    threadsLV.getSelectionModel().getSelectedItem(),
                    postsLV, repliesLV, fbLV, detailsTA
            );
        }
    }

    /**
     * Builds the staff review UI layout. This constructor is only called once
     * because the view is treated like a singleton.
     */
    private ViewStaffReview() {
        root = new Pane();
        theScene = new Scene(root, width, height);

        // Header area.
        setupLabel(title, "Arial", 28, width, Pos.CENTER, 0, 5);
        setupLabel(who, "Arial", 20, width, Pos.BASELINE_LEFT, 20, 55);

        setupButton(acct, "Dialog", 18, 170, Pos.CENTER, 610, 45);
        acct.setOnAction(e ->
                guiUserUpdate.ViewUserUpdate.displayUserUpdate(theStage, theUser)
        );

        // Thread column.
        setupLabel(threadsL, "Arial", 18, 200, Pos.BASELINE_LEFT, 20, 110);
        threadsLV.setLayoutX(20);
        threadsLV.setLayoutY(140);
        threadsLV.setPrefSize(110, 360);

        // Posts column.
        setupLabel(postsL, "Arial", 18, 200, Pos.BASELINE_LEFT, 150, 110);
        postsLV.setLayoutX(150);
        postsLV.setLayoutY(140);
        postsLV.setPrefSize(330, 360);

        // Replies column.
        setupLabel(repliesL, "Arial", 18, 200, Pos.BASELINE_LEFT, 500, 110);
        repliesLV.setLayoutX(500);
        repliesLV.setLayoutY(140);
        repliesLV.setPrefSize(240, 180);

        // Feedback section.
        setupLabel(fbL, "Arial", 16, 300, Pos.BASELINE_LEFT, 500, 330);
        fbLV.setLayoutX(500);
        fbLV.setLayoutY(360);
        fbLV.setPrefSize(240, 140);

        setupButton(addFbBtn, "Dialog", 14, 200, Pos.CENTER, 520, 495);
        addFbBtn.setOnAction(e ->
                ControllerStaffReview.doAddFeedback(theUser, RV, postsLV, repliesLV, fbLV)
        );

        // Details area.
        setupLabel(detailsL, "Arial", 18, 300, Pos.BASELINE_LEFT, 20, 510);
        detailsTA.setEditable(false);
        detailsTA.setWrapText(true);
        detailsTA.setLayoutX(20);
        detailsTA.setLayoutY(540);
        detailsTA.setPrefSize(860, 70);

        // When a thread is selected, load its posts.
        threadsLV.getSelectionModel().selectedItemProperty().addListener((o, oldV, newV) ->
                ControllerStaffReview.loadPostsForThread(SR, newV, postsLV, repliesLV, fbLV, detailsTA)
        );

        // When a post is selected, show its replies and details.
        postsLV.getSelectionModel().selectedItemProperty().addListener((o, oldV, newPost) -> {
            ControllerStaffReview.loadRepliesForPost(SR, newPost, theUser.getUserName(), repliesLV, fbLV);
            ControllerStaffReview.showPostDetails(newPost, detailsTA);
        });

        // When a reply is selected, show its details and feedback.
        repliesLV.getSelectionModel().selectedItemProperty().addListener((o, oldV, newReply) -> {
            ControllerStaffReview.showReplyDetails(newReply, detailsTA);
            ControllerStaffReview.loadFeedback(
                    RV,
                    ReviewService.Feedback.TargetType.REPLY,
                    (newReply == null ? -1 : newReply.getId()),
                    fbLV
            );
        });

        // Footer buttons.
        setupButton(back, "Dialog", 18, 220, Pos.CENTER, 20, 540);
        back.setOnAction(e ->
                guiRole2.ViewRole2Home.displayRole2Home(theStage, theUser)
        );

        setupButton(logout, "Dialog", 18, 220, Pos.CENTER, 290, 540);
        logout.setOnAction(e ->
                guiUserLogin.ViewUserLogin.displayUserLogin(theStage)
        );

        setupButton(quit, "Dialog", 18, 220, Pos.CENTER, 550, 540);
        quit.setOnAction(e -> System.exit(0));

        // Add all controls to the screen.
        root.getChildren().addAll(
                title, who, acct, sep1,
                threadsL, threadsLV,
                postsL, postsLV,
                repliesL, repliesLV,
                fbL, fbLV, addFbBtn,
                sep4, back, logout, quit
        );
    }

    /**
     * Helper for setting up labels with consistent styling.
     */
    private static void setupLabel(Label l, String ff, double f, double w, Pos p, double x, double y) {
        l.setFont(Font.font(ff, f));
        l.setMinWidth(w);
        l.setAlignment(p);
        l.setLayoutX(x);
        l.setLayoutY(y);
    }

    /**
     * Helper for setting up buttons with consistent styling.
     */
    private static void setupButton(Button b, String ff, double f, double w, Pos p, double x, double y) {
        b.setFont(Font.font(ff, f));
        b.setMinWidth(w);
        b.setAlignment(p);
        b.setLayoutX(x);
        b.setLayoutY(y);
    }
}
