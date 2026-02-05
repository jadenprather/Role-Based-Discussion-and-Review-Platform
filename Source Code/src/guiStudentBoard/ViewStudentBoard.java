package guiStudentBoard;

import entityClasses.Post;
import entityClasses.Post.Moderation;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.util.Pair;

import java.util.*;
import java.util.stream.Collectors;

/**
 * This class creates the main student discussion board interface.
 * Students can read posts, write new ones, reply to others, and
 * keep track of unread activity. The board updates automatically
 * as the student interacts with different controls.
 */
public class ViewStudentBoard extends BorderPane {

    private final controllerStudentBoard controller;
    private final String currentUser;

    private final TextField searchField = new TextField();
    private final ComboBox<String> threadFilter = new ComboBox<>();
    private final Button btnRefresh = new Button("Refresh");
    private final Button btnShowUnread = new Button("Show Unread");

    private final TableView<Map<String, Object>> postsTable = new TableView<>();
    private final ObservableList<Map<String, Object>> postsData = FXCollections.observableArrayList();

    private final Button btnNewPost = new Button("New Post");
    private final Button btnReply = new Button("Reply");
    private final Button btnMarkRead = new Button("Mark Read");
    private final Button btnDelete = new Button("Delete");

    private final Button btnExportGrading = new Button("Export Grading");
    private final Button btnShowHelpers = new Button("Show Peer Helpers ≥ 3");

    private final ListView<String> repliesList = new ListView<>();
    private final CheckBox chkUnreadOnly = new CheckBox("Unread only");

    /**
     * Sets up the student board with all controls and loads
     * the student's first set of posts.
     */
    public ViewStudentBoard(controllerStudentBoard controller, String currentUser) {
        this.controller = controller;
        this.currentUser = currentUser;

        setPadding(new Insets(12));
        setTop(buildTopBar());
        setCenter(buildCenter());
        setRight(buildRight());

        configurePostsTable();
        wireActions();

        refreshPosts(false);
    }

    // ============================================================
    // TOP BAR — Search and Filters
    // ============================================================

    private Node buildTopBar() {
        HBox bar = new HBox(8);
        bar.setPadding(new Insets(0, 0, 10, 0));

        searchField.setPromptText("Search keyword… (press Enter)");

        searchField.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                doSearch();
            }
        });

        List<String> threads = new ArrayList<>(controller.getAllowedThreads());
        Collections.sort(threads);
        threads.add(0, "All threads");

        threadFilter.setItems(FXCollections.observableArrayList(threads));
        threadFilter.getSelectionModel().selectFirst();

        bar.getChildren().addAll(
                new Label("Search:"), searchField,
                new Label("Thread:"), threadFilter,
                btnRefresh, btnShowUnread,
                btnExportGrading, btnShowHelpers
        );

        return bar;
    }

    // ============================================================
    // MAIN CENTER AREA — Posts Table + Buttons
    // ============================================================

    private Node buildCenter() {
        VBox v = new VBox(8);

        postsTable.setItems(postsData);

        HBox actions = new HBox(8, btnNewPost, btnReply, btnMarkRead, btnDelete);
        actions.setPadding(new Insets(6, 0, 0, 0));

        v.getChildren().addAll(postsTable, actions);
        VBox.setVgrow(postsTable, Priority.ALWAYS);

        return v;
    }

    // ============================================================
    // RIGHT SIDE — Replies Panel
    // ============================================================

    private Node buildRight() {
        VBox box = new VBox(8);
        box.setPadding(new Insets(0, 0, 0, 10));
        box.setPrefWidth(380);

        Label title = new Label("Replies");
        title.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        // Students can optionally show only the unread replies.
        chkUnreadOnly.setSelected(false);
        chkUnreadOnly.setOnAction(e -> loadRepliesForSelected());

        box.getChildren().addAll(title, chkUnreadOnly, repliesList);
        VBox.setVgrow(repliesList, Priority.ALWAYS);

        return box;
    }

    // ============================================================
    // POSTS TABLE CONFIGURATION
    // ============================================================

    @SuppressWarnings("unchecked")
    private void configurePostsTable() {

        TableColumn<Map<String, Object>, Object> cId      = makeCol("ID",      "id",       60);
        TableColumn<Map<String, Object>, Object> cAuthor  = makeCol("Author",  "author",   120);
        TableColumn<Map<String, Object>, Object> cThread  = makeCol("Thread",  "thread",   110);
        TableColumn<Map<String, Object>, Object> cContent = makeCol("Content", "content",  320);
        TableColumn<Map<String, Object>, Object> cReplies = makeCol("Replies", "replies",   70);
        TableColumn<Map<String, Object>, Object> cFlagged = makeCol("Flagged", "moderation", 70);

        // Show "FLAGGED" only if the post has been marked by staff.
        cFlagged.setCellValueFactory(param -> {
            Object m = param.getValue().get("moderation");
            boolean isFlagged = (m == Moderation.FLAGGED);
            return new ReadOnlyObjectWrapper<>(isFlagged ? "FLAGGED" : "");
        });

        postsTable.getColumns().setAll(
                cId, cAuthor, cThread, cContent, cReplies, cFlagged
        );

        // Update side panel whenever a post is selected.
        postsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> {
            boolean hasSelection = newV != null;
            btnReply.setDisable(!hasSelection);
            btnMarkRead.setDisable(!hasSelection);
            btnDelete.setDisable(!hasSelection);
            loadRepliesForSelected();
        });

        btnReply.setDisable(true);
        btnMarkRead.setDisable(true);
        btnDelete.setDisable(true);

        // Highlight helpful students or flagged posts.
        postsTable.setRowFactory(tv -> new TableRow<>() {
            @Override
            protected void updateItem(Map<String, Object> row, boolean empty) {
                super.updateItem(row, empty);

                if (!empty && row != null) {
                    Set<String> helpers = controller.getStudentsWhoHelpedAtLeast(3);
                    String author = String.valueOf(row.get("author"));
                    Moderation mod = (Moderation) row.get("moderation");

                    if (helpers.contains(author)) {
                        setStyle("-fx-background-color: palegreen;");
                    } else if (mod == Moderation.FLAGGED) {
                        setStyle("-fx-background-color: #ffe4e1;");
                    } else {
                        setStyle("");
                    }
                } else {
                    setStyle("");
                }
            }
        });
    }

    private TableColumn<Map<String, Object>, Object> makeCol(String title, String key, int width) {
        TableColumn<Map<String, Object>, Object> c = new TableColumn<>(title);
        c.setMinWidth(width);
        c.setPrefWidth(width);

        c.setCellValueFactory(param ->
                new ReadOnlyObjectWrapper<>(param.getValue().get(key))
        );

        return c;
    }

    // ============================================================
    // BUTTON ACTIONS
    // ============================================================

    private void wireActions() {

        btnRefresh.setOnAction(e -> refreshPosts(false));

        btnShowUnread.setOnAction(e -> showUnread());

        threadFilter.setOnAction(e -> doSearch());

        btnNewPost.setOnAction(e -> showNewPostDialog());

        btnReply.setOnAction(e -> showReplyDialog());

        btnMarkRead.setOnAction(e -> markSelectedPostRead());

        btnDelete.setOnAction(e -> deleteSelectedPost());

        btnExportGrading.setOnAction(e -> {
            String csv = controller.exportGradingSummaryCSV();
            Alert a = new Alert(Alert.AlertType.INFORMATION, csv, ButtonType.OK);
            a.setHeaderText("Grading Summary (CSV)");
            a.getDialogPane().setPrefWidth(700);
            a.showAndWait();
        });

        btnShowHelpers.setOnAction(e -> {
            Set<String> helpers = controller.getStudentsWhoHelpedAtLeast(3);

            Alert a = new Alert(Alert.AlertType.INFORMATION,
                    helpers.isEmpty()
                            ? "No one has helped ≥3 peers yet."
                            : "Students who helped ≥3 peers:\n" + String.join(", ", helpers),
                    ButtonType.OK);

            a.setHeaderText("Peer Helpers");
            a.showAndWait();
        });
    }

    // ============================================================
    // DATA LOADING METHODS
    // ============================================================

    private void refreshPosts(boolean othersOnly) {
        List<Map<String, Object>> rows =
                controller.listPostSummaries(currentUser, othersOnly);

        postsData.setAll(rows);
        loadRepliesForSelected();
    }

    private void showUnread() {
        List<Post> unread = controller.listUnreadPosts(currentUser);

        List<Map<String, Object>> rows = unread.stream()
                .map(p -> controller.getPostSummary(p, currentUser))
                .collect(Collectors.toList());

        postsData.setAll(rows);
        loadRepliesForSelected();
    }

    private void doSearch() {
        String keyword = Optional.ofNullable(searchField.getText()).orElse("").trim();

        String selectedThread = threadFilter.getSelectionModel().getSelectedItem();
        String thread = (selectedThread == null || selectedThread.equals("All threads"))
                ? null : selectedThread;

        List<Post> results = controller.searchPosts(keyword, thread);

        List<Map<String, Object>> summaries = results.stream()
                .map(p -> controller.getPostSummary(p, currentUser))
                .collect(Collectors.toList());

        postsData.setAll(summaries);
        loadRepliesForSelected();
    }

    private Map<String, Object> getSelectedRow() {
        return postsTable.getSelectionModel().getSelectedItem();
    }

    private int getSelectedPostId() {
        Map<String, Object> row = getSelectedRow();
        return (row == null) ? -1 : (int) row.get("id");
    }

    /**
     * Loads replies for the selected post and shows them in the right panel.
     * This method also adds a small label if the reply looks unreasonable.
     */
    private void loadRepliesForSelected() {
        repliesList.getItems().clear();

        int postId = getSelectedPostId();
        if (postId <= 0) return;

        List<controllerStudentBoard.Reply> rep =
                controller.listReplies(postId, currentUser, chkUnreadOnly.isSelected());

        for (controllerStudentBoard.Reply r : rep) {
            boolean reasonable = controller.isAnswerReasonable(r);
            String flag = reasonable ? "" : " [Unreasonable]";

            repliesList.getItems().add(
                    "#" + r.getId() + " by " + r.getAuthor() + ": " + r.getContent() + flag
            );
        }
    }

    // ============================================================
    // DIALOGS (New Post / Reply)
    // ============================================================

    private void showNewPostDialog() {

        Dialog<Pair<String, String>> dlg = new Dialog<>();
        dlg.setTitle("New Post");
        dlg.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        TextArea contentField = new TextArea();
        contentField.setPromptText("Enter content...");
        contentField.setPrefRowCount(5);

        ComboBox<String> threadBox = new ComboBox<>();
        threadBox.getItems().addAll(controller.getAllowedThreads());
        threadBox.getSelectionModel().selectFirst();

        // Arrange input controls.
        GridPane gp = new GridPane();
        gp.setVgap(8);
        gp.setHgap(8);
        gp.setPadding(new Insets(10));
        gp.addRow(0, new Label("Thread:"), threadBox);
        gp.addRow(1, new Label("Content:"), contentField);

        dlg.getDialogPane().setContent(gp);

        dlg.setResultConverter(bt ->
                bt == ButtonType.OK
                        ? new Pair<>("", contentField.getText().trim())
                        : null
        );

        Optional<Pair<String, String>> res = dlg.showAndWait();

        if (res.isPresent()) {
            String content = res.get().getValue();
            String thread = threadBox.getValue();

            if (!content.isEmpty()) {
                controller.createPost(currentUser, content, thread);
                refreshPosts(false);
            } else {
                showErrorPopup("Post content required.");
            }
        }
    }

    private void showReplyDialog() {

        int postId = getSelectedPostId();
        if (postId <= 0) return;

        TextInputDialog dlg = new TextInputDialog();
        dlg.setTitle("Reply");
        dlg.setHeaderText("Write a reply to post #" + postId);

        dlg.showAndWait().ifPresent(text -> {

            String msg = text.trim();

            if (!msg.isBlank()) {
                controller.addReply(postId, currentUser, msg);
                refreshPosts(false);

                postsTable.getSelectionModel().select(
                        postsData.stream()
                                .filter(m -> (int) m.get("id") == postId)
                                .findFirst()
                                .orElse(null)
                );
            }
        });
    }

    // ============================================================
    // SIMPLE ACTIONS: MARK READ / DELETE
    // ============================================================

    private void markSelectedPostRead() {
        int postId = getSelectedPostId();
        if (postId <= 0) return;

        controller.markPostRead(postId, currentUser);
        refreshPosts(false);
    }

    private void deleteSelectedPost() {
        int postId = getSelectedPostId();
        if (postId <= 0) return;

        Alert a = new Alert(Alert.AlertType.CONFIRMATION,
                "Delete post #" + postId + "? It will show as [deleted].",
                ButtonType.OK, ButtonType.CANCEL);

        if (a.showAndWait().filter(bt -> bt == ButtonType.OK).isPresent()) {
            controller.deletePost(postId);
            refreshPosts(false);
        }
    }

    // ============================================================
    // ERROR POPUP
    // ============================================================

    private void showErrorPopup(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK);
        alert.showAndWait();
    }
}
