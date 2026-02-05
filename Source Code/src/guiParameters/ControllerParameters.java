package guiParameters;

import javafx.collections.FXCollections;
import javafx.scene.control.*;
import review.ReviewService;
import review.ReviewService.Parameter;
import guiStudentBoard.controllerStudentBoard;

class ControllerParameter {

    /** Reload parameter list into the ListView */
    static void refresh(ReviewService rv, ListView<Parameter> paramsLV){
        paramsLV.setItems(FXCollections.observableArrayList(rv.listParameters()));
    }

    /** Add a new rubric parameter (name, description, max points, weight) */
    static void doAddParam(ReviewService rv, ListView<Parameter> paramsLV){
    	//Just getting info from users
        TextInputDialog n = new TextInputDialog("");
        n.setTitle("New Parameter"); n.setHeaderText("Name"); n.setContentText("Name:");
        var name = n.showAndWait().orElse(null); if (name==null || name.isBlank()) return;

        TextInputDialog d = new TextInputDialog("");
        d.setTitle("New Parameter"); d.setHeaderText("Description"); d.setContentText("Description:");
        var desc = d.showAndWait().orElse("");

        TextInputDialog m = new TextInputDialog("10");
        m.setTitle("New Parameter"); m.setHeaderText("Max Points"); m.setContentText("Max:");
        int max = m.showAndWait().map(s->Integer.parseInt(s.trim())).orElse(10);

        TextInputDialog w = new TextInputDialog("1.0");
        w.setTitle("New Parameter"); w.setHeaderText("Weight (0..1)"); w.setContentText("Weight:");
        double wt = w.showAndWait().map(s->Double.parseDouble(s.trim())).orElse(1.0);

        // adding it to the list
        rv.createParameter(name.trim(), desc.trim(), max, wt);
        refresh(rv, paramsLV);
    }

    /** Delete the selected parameter */
    static void doDelParam(ReviewService rv, ListView<Parameter> paramsLV){
        var sel = paramsLV.getSelectionModel().getSelectedItem();
        if (sel == null) return;
        rv.deleteParameter(sel.getId());
        refresh(rv, paramsLV);
    }

    /**
     * Compute a quick demo score for a student using the selected parameters.
     * Replace the scoring rule with your real rubric later.
     */
    static void doCompute(controllerStudentBoard sr, TextField studentTF, ListView<Parameter> paramsLV, TextArea resultTA)
    {
        var student = studentTF.getText()==null ? "" : studentTF.getText().trim();
        if (student.isBlank()){ info("Grade","Enter a student username."); return; }

        var chosen = paramsLV.getSelectionModel().getSelectedItems();
        if (chosen.isEmpty()){ info("Grade","Select 1+ parameters (Ctrl+Click)."); return; }

        // demo rule: full credit if the student has at least one post
        boolean hasPost = sr.listAllPosts().stream().anyMatch(p -> p.getAuthor().equals(student));

        int totalMax = chosen.stream().mapToInt(Parameter::getMaxPoints).sum();
        int earned   = hasPost ? totalMax : 0;

        resultTA.setText(
            "Student: " + student +
            "\nSelected params: " + chosen.size() +
            "\nMax points: " + totalMax +
            "\nEarned (demo rule): " + earned +
            "\n\nNOTE: Replace this with your rubric " +
            "(e.g., count posts/replies, quality, timeliness, participation)."
        );
    }

    // small helper
    private static void info(String t, String m){
        var a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle(t); a.setHeaderText(t); a.setContentText(m); a.showAndWait();
    }
}
