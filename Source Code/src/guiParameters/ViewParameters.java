package guiParameters;

import applicationMain.FoundationsMain;
import entityClasses.User;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import review.ReviewService;
import review.ReviewService.Parameter;
import guiStudentBoard.controllerStudentBoard;

public class ViewParameters {

    private static final double width  = FoundationsMain.WINDOW_WIDTH;
    private static final double height = FoundationsMain.WINDOW_HEIGHT;

    // shared services
    private static final ReviewService rv = FoundationsMain.reviewService;
    
    private static controllerStudentBoard sr;

    // singleton wiring
    private static ViewParameters theView;
    private static Stage theStage;
    private static Scene theScene;
    private static Pane  root;
    private static User  theUser;

    // header
    private static final Label title = new Label("Review Parameters & Grading");
    private static final Label who   = new Label();
    private static final Line  sep1  = new Line(20,95,width-20,95);

    // parameters column
    private static final Label paramsL = new Label("Parameters");
    static final ListView<Parameter> paramsLV = new ListView<>();
    private static final Button addP = new Button("Add");
    private static final Button delP = new Button("Delete");

    // quick grade
    private static final Label gradeL   = new Label("Quick Grade a Student");
    static final TextField studentTF    = new TextField();
    private static final Button gradeBtn = new Button("Compute Score");
    static final TextArea resultTA      = new TextArea();

    // footer
    private static final Line sep4 = new Line(20,525,width-20,525);
    private static final Button back   = new Button("Return");
    private static final Button logout = new Button("Logout");
    private static final Button quit   = new Button("Quit");

    public static void display(Stage ps, User staff){
        theStage = ps; theUser = staff;

        // ✅ ensure controller is ready here (safe init order)
        if (sr == null) {
            sr = new controllerStudentBoard(FoundationsMain.threadService, FoundationsMain.postRepo);
        }

        if (theView == null) theView = new ViewParameters();
        who.setText("Staff: " + staff.getUserName());
        ControllerParameter.refresh(rv, paramsLV);
        theStage.setTitle("CSE 360 Foundations: Parameters & Grading");
        theStage.setScene(theScene);
        theStage.show();
    }

    private ViewParameters(){
        // root + scene
        root = new Pane();
        theScene = new Scene(root, width, height);

        // header
        setupLabel(title,"Arial",28,width, Pos.CENTER,0,5);
        setupLabel(who,"Arial",20,width, Pos.BASELINE_LEFT,20,55);

        // parameters list
        setupLabel(paramsL,"Arial",18,300, Pos.BASELINE_LEFT,20,110);
        paramsLV.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        paramsLV.setLayoutX(20); paramsLV.setLayoutY(140); paramsLV.setPrefSize(300,360);

        setupButton(addP,"Dialog",16,120, Pos.CENTER,350,180);
        addP.setOnAction(e -> ControllerParameter.doAddParam(rv, paramsLV));

        setupButton(delP,"Dialog",16,120, Pos.CENTER,350,230);
        delP.setOnAction(e -> ControllerParameter.doDelParam(rv, paramsLV));

        // quick grade
        setupLabel(gradeL,"Arial",18,360, Pos.BASELINE_LEFT,500,110);
        studentTF.setPromptText("student username");
        studentTF.setLayoutX(500); studentTF.setLayoutY(140); studentTF.setPrefWidth(240);

        setupButton(gradeBtn,"Dialog",16,160, Pos.CENTER,500,180);
        gradeBtn.setOnAction(e -> ControllerParameter.doCompute(sr, studentTF, paramsLV, resultTA));

        resultTA.setLayoutX(500); resultTA.setLayoutY(230);
        resultTA.setPrefSize(260,270); resultTA.setEditable(false); resultTA.setWrapText(true);

        // footer
        setupButton(back,"Dialog",18,220, Pos.CENTER,20,540);
        back.setOnAction(e -> guiRole2.ViewRole2Home.displayRole2Home(theStage, theUser));

        setupButton(logout,"Dialog",18,220, Pos.CENTER,290,540);
        logout.setOnAction(e -> guiUserLogin.ViewUserLogin.displayUserLogin(theStage));

        setupButton(quit,"Dialog",18,220, Pos.CENTER,550,540);
        quit.setOnAction(e -> System.exit(0));

        // assemble
        root.getChildren().addAll(
            title, who, sep1,
            paramsL, paramsLV, addP, delP,
            gradeL, studentTF, gradeBtn, resultTA,
            sep4, back, logout, quit
        );
    }

    // tiny UI helpers
    private static void setupLabel(Label l, String ff, double f, double w, Pos p, double x, double y){
        l.setFont(Font.font(ff,f)); l.setMinWidth(w); l.setAlignment(p); l.setLayoutX(x); l.setLayoutY(y);
    }
    private static void setupButton(Button b, String ff, double f, double w, Pos p, double x, double y){
        b.setFont(Font.font(ff,f)); b.setMinWidth(w); b.setAlignment(p); b.setLayoutX(x); b.setLayoutY(y);
    }
}
