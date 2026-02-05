package guiStaffReview;

import javafx.collections.FXCollections;
import javafx.scene.control.*;
import review.ReviewService;
import review.ReviewService.Feedback;
import review.ReviewService.Feedback.Scope;
import review.ReviewService.Feedback.TargetType;
import entityClasses.User;
import guiStudentBoard.controllerStudentBoard;
import guiStudentBoard.controllerStudentBoard.Reply;
import entityClasses.Post;

import java.util.List;
import java.util.stream.Collectors;

/**
 * This class provides helper methods used by the Staff Review interface.
 * Staff can look through posts, view replies, and leave feedback on either.
 * All methods are static because the staff review screen treats this as a
 * simple utility controller rather than an object that stores its own state.
 */
class ControllerStaffReview {

    /**
     * Adds feedback for the post or reply that the staff member selected.
     * The method determines whether a post or reply was chosen and attaches
     * the feedback to the correct target.
     */
    static void doAddFeedback(
            User staff,
            ReviewService rv,
            ListView<Post> postsLV,
            ListView<Reply> repliesLV,
            ListView<Feedback> fbLV
    ) {
        Reply selectedReply = repliesLV.getSelectionModel().getSelectedItem();
        Post selectedPost = postsLV.getSelectionModel().getSelectedItem();

        // Staff must choose something to review first.
        if (selectedReply == null && selectedPost == null) {
            info("Feedback", "Select a post or a reply first.");
            return;
        }

        // Ask who should be able to see this feedback.
        var scopeChoice = new ChoiceDialog<>(
                Scope.PRIVATE_TO_STUDENT,
                Scope.PRIVATE_TO_STUDENT,
                Scope.STAFF_ONLY
        );
        scopeChoice.setTitle("Feedback Scope");
        scopeChoice.setHeaderText("Who should see this feedback?");
        Scope scope = scopeChoice.showAndWait().orElse(null);
        if (scope == null) return;

        // Ask the staff member to enter the feedback text.
        var dlg = new TextInputDialog("");
        dlg.setTitle("Feedback");
        dlg.setHeaderText("Enter feedback text");
        dlg.setContentText("Text:");
        String text = dlg.showAndWait().orElse(null);
        if (text == null || text.isBlank()) return;

        TargetType targetType;
        int targetId;
        String recipient;

        // If a reply was selected, attach the feedback to the reply.
        if (selectedReply != null) {
            targetType = TargetType.REPLY;
            targetId = selectedReply.getId();
            recipient = selectedReply.getAuthor();
        } else {
            // Otherwise, attach it to a post.
            targetType = TargetType.POST;
            targetId = selectedPost.getId();
            recipient = selectedPost.getAuthor();
        }

        // Store the feedback using the service.
        rv.addFeedback(targetType, targetId, staff.getUserName(), recipient, text.trim(), scope);

        // Refresh the feedback list to include the new entry.
        loadFeedback(rv, targetType, targetId, fbLV);
    }

    /**
     * Refreshes the thread list used by the staff review view.
     * Threads here simply represent the distinct thread names found in posts.
     */
    static void refreshThreads(
            controllerStudentBoard sr,
            ListView<String> threadsLV,
            ListView<Post> postsLV,
            ListView<Reply> repliesLV,
            ListView<Feedback> fbLV,
            TextArea detailsTA
    ) {
        // Build a list of thread names based on the posts available.
        var threads = sr.listAllPosts()
                .stream()
                
                .distinct()
                .sorted()
                .collect(Collectors.toList());

        

        // If at least one thread exists, show its posts.
        if (!threads.isEmpty()) {
            if (threadsLV.getSelectionModel().getSelectedItem() == null) {
                threadsLV.getSelectionModel().selectFirst();
            }
            loadPostsForThread(
                    sr,
                    threadsLV.getSelectionModel().getSelectedItem(),
                    postsLV, repliesLV, fbLV, detailsTA
            );
        }
    }

    /**
     * Loads all posts that belong to a specific thread.
     * This updates the post list and clears the reply and feedback sections.
     */
    static void loadPostsForThread(
            controllerStudentBoard sr,
            String thread,
            ListView<Post> postsLV,
            ListView<Reply> repliesLV,
            ListView<Feedback> fbLV,
            TextArea detailsTA
    ) {
        // If there is no thread selected, clear everything.
        if (thread == null) {
            postsLV.setItems(FXCollections.observableArrayList());
            repliesLV.setItems(FXCollections.observableArrayList());
            fbLV.setItems(FXCollections.observableArrayList());
            detailsTA.clear();
            return;
        }

        // Load posts in this thread. Empty keyword means "show all".
        var posts = FXCollections.observableArrayList(sr.searchPosts("", thread));
        postsLV.setItems(posts);

        // Clear the other panels until a post is selected.
        repliesLV.setItems(FXCollections.observableArrayList());
        fbLV.setItems(FXCollections.observableArrayList());
        detailsTA.clear();
    }

    /**
     * Loads all replies for a selected post so staff can read the full thread.
     */
    static void loadRepliesForPost(
            controllerStudentBoard sr,
            Post p,
            String currentUser,
            ListView<Reply> repliesLV,
            ListView<Feedback> fbLV
    ) {
        // If nothing is selected, clear everything.
        if (p == null) {
            repliesLV.setItems(FXCollections.observableArrayList());
            fbLV.setItems(FXCollections.observableArrayList());
            return;
        }

        // Staff always sees the full set of replies.
        var replies = sr.listReplies(p.getId(), currentUser, false);
        repliesLV.setItems(FXCollections.observableArrayList(replies));
        fbLV.setItems(FXCollections.observableArrayList());
    }

    /**
     * Loads feedback entries for either a post or a reply.
     */
    static void loadFeedback(
            ReviewService rv,
            TargetType tt,
            int targetId,
            ListView<Feedback> fbLV
    ) {
        // If nothing is selected, clear the feedback list.
        if (targetId < 0) {
            fbLV.setItems(FXCollections.observableArrayList());
            return;
        }

        // Fetch feedback from the service.
        var list = rv.listFeedbackForTarget(tt, targetId);
        fbLV.setItems(FXCollections.observableArrayList(list));
    }

    /**
     * Shows information about a selected post in the details area.
     */
    static void showPostDetails(Post p, TextArea detailsTA) {
        if (p == null) {
            detailsTA.clear();
            return;
        }

        detailsTA.setText(
                "POST #" + p.getId()
                        + " (" + p.getThread() + ") by " + p.getAuthor()
                        + " @ " + p.getCreatedAt()
                        + "\n\n" + p.getContent()
        );
    }

    /**
     * Shows information about a selected reply in the details area.
     */
    static void showReplyDetails(Reply r, TextArea detailsTA) {
        if (r == null) return;

        detailsTA.setText(
                "REPLY #" + r.getId()
                        + " to Post " + r.getParentPostId()
                        + " by " + r.getAuthor()
                        + " @ " + r.getCreatedAt()
                        + "\n\n" + r.getContent()
        );
    }

    /**
     * Sets up how posts and replies appear inside their ListView components.
     * Each cell shows a small, readable snippet of the content.
     */
    static void formatters(ListView<Post> postsLV, ListView<Reply> repliesLV) {

        postsLV.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Post p, boolean empty) {
                super.updateItem(p, empty);

                if (empty || p == null) {
                    setText(null);
                } else {
                    setText(
                            "#" + p.getId()
                                    + " • " + p.getAuthor()
                                    + " — " + snippet(p.getContent(), 60)
                    );
                }
            }
        });

        repliesLV.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Reply r, boolean empty) {
                super.updateItem(r, empty);

                if (empty || r == null) {
                    setText(null);
                } else {
                    setText(
                            r.getAuthor()
                                    + ": "
                                    + snippet(r.getContent(), 50)
                    );
                }
            }
        });
    }

    /**
     * Returns a shortened version of a long string so it fits nicely in list cells.
     */
    private static String snippet(String s, int max) {
        if (s == null) return "";
        return s.length() > max ? s.substring(0, max) + "…" : s;
    }

    /**
     * Opens a simple information dialog with a title and message.
     */
    private static void info(String title, String msg) {
        var a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle(title);
        a.setHeaderText(title);
        a.setContentText(msg);
        a.showAndWait();
    }
}
