package guiStudentBoard;

import entityClasses.Post;
import entityClasses.Post.Moderation;
import review.ReviewService.Parameter;
import services.ThreadService;
import studentPosts.InMemoryPostRepository;
import studentPosts.SearchSpec;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * FINAL MERGED CONTROLLER – Supports ALL Student, Staff, and Dashboard features.
 * Combines logic from both versions of controllerStudentBoard.
 *
 * Uses:
 *   ✓ Immutable entityClasses.Post + InMemoryPostRepository
 *   ✓ Replies stored separately (correct model)
 *   ✓ Reasonable answer checking
 *   ✓ Help-peers dashboard logic
 *   ✓ Grading CSV export
 *   ✓ Moderation + flagReason map
 *   ✓ Role-based access (Student/Grader)
 *   ✓ Read/unread tracking
 */
public class controllerStudentBoard {

    // =====================================================================
    // THREAD & REPO
    // =====================================================================

    private static final String DEFAULT_THREAD = "General";

    private final ThreadService threads;
    private final InMemoryPostRepository repo;

    // ID sources
    private int nextPostId = 1;
    private int nextReplyId = 1;

    // =====================================================================
    // ROLE-BASED ACCESS
    // =====================================================================

    public enum Role { STUDENT, GRADER }
    private final Map<String, Role> roles = new HashMap<>();

    public void setUserRole(String username, Role role) {
        roles.put(username, role);
    }

    public Role getUserRole(String username) {
        return roles.getOrDefault(username, Role.STUDENT);
    }

    // =====================================================================
    // FLAG REASON MAP (Option 2)
    // =====================================================================

    private final Map<Integer, String> flagReasons = new HashMap<>();

    // =====================================================================
    // REPLIES
    // =====================================================================

    public static class Reply {
        private final int id;
        private final String author;
        private String content;
        private final LocalDateTime createdAt;
        private final int parentPostId;
        private final Set<String> readers = new HashSet<>();

        public Reply(int id, String author, String content, int parentPostId) {
            this.id = id;
            this.author = author;
            this.content = content;
            this.parentPostId = parentPostId;
            this.createdAt = LocalDateTime.now();
        }

        public int getId() { return id; }
        public String getAuthor() { return author; }
        public String getContent() { return content; }
        public LocalDateTime getCreatedAt() { return createdAt; }
        public int getParentPostId() { return parentPostId; }

        public void setContent(String newContent) { this.content = newContent; }

        public void markRead(String username) { readers.add(username); }
        public boolean isReadBy(String username) { return readers.contains(username); }
    }

    private final Map<Integer, Reply> replies = new LinkedHashMap<>();
    private final Map<Integer, Set<String>> postReaders = new HashMap<>();

    // =====================================================================
    // CONSTRUCTOR
    // =====================================================================

    public controllerStudentBoard(ThreadService threads, InMemoryPostRepository repo) {
        this.threads = threads;
        this.repo = repo;

        this.nextPostId = repo.findAll().stream()
                .mapToInt(Post::getId)
                .max()
                .orElse(0) + 1;
    }

    // =====================================================================
    // THREAD LISTING
    // =====================================================================

    public Set<String> getAllowedThreads() {
        return new LinkedHashSet<>(threads.listThreads());
    }

    public List<String> listThreads() {
        return threads.listThreads();
    }

    // =====================================================================
    // POST CREATION / UPDATE / DELETE
    // =====================================================================

    public Post createPost(String author, String content, String thread) {
        String t = (thread == null || thread.isBlank()) ? DEFAULT_THREAD : thread.trim();
        if (!threads.hasThread(t)) t = DEFAULT_THREAD;

        return repo.create(nextPostId++, author, t, content);
    }

    public boolean updatePost(int postId, String newContent) {
        Optional<Post> opt = repo.findById(postId);
        if (opt.isEmpty()) return false;

        Post updated = opt.get().withContent(newContent);
        repo.update(updated);
        return true;
    }

    public boolean deletePost(int postId) {
        Optional<Post> opt = repo.findById(postId);
        if (opt.isEmpty()) return false;

        Post updated = opt.get().softDeleted();
        repo.update(updated);
        return true;
    }

    // =====================================================================
    // FLAGGING (Moderation + flagReason map)
    // =====================================================================

    public void flagPost(int postId, String reason) {
        repo.findById(postId).ifPresent(post -> {
            Post updated = post.withModeration(Moderation.FLAGGED);
            repo.update(updated);
            flagReasons.put(postId, reason == null ? "" : reason);
        });
    }

    public void unflagPost(int postId) {
        repo.findById(postId).ifPresent(post -> {
            Post updated = post.withModeration(Moderation.NORMAL);
            repo.update(updated);
            flagReasons.remove(postId);
        });
    }

    public List<Post> listFlaggedPosts() {
        return repo.findAll().stream()
                .filter(p -> p.getModeration() == Moderation.FLAGGED)
                .collect(Collectors.toList());
    }

    public String getFlagReason(int postId) {
        return flagReasons.getOrDefault(postId, "");
    }

    // =====================================================================
    // REPLIES
    // =====================================================================

    public Reply addReply(int postId, String author, String content) {
        if (repo.findById(postId).isEmpty()) return null;

        Reply r = new Reply(nextReplyId++, author, content, postId);
        replies.put(r.getId(), r);
        return r;
    }

    public boolean updateReply(int replyId, String newContent) {
        Reply r = replies.get(replyId);
        if (r == null) return false;
        r.setContent(newContent);
        return true;
    }

    public boolean deleteReply(int replyId) {
        return replies.remove(replyId) != null;
    }

    public void markReplyRead(int replyId, String username) {
        Reply r = replies.get(replyId);
        if (r != null) r.markRead(username);
    }

    public List<Reply> listReplies(int postId, String username, boolean unreadOnly) {
        return replies.values().stream()
                .filter(r -> r.getParentPostId() == postId)
                .filter(r -> !unreadOnly || !r.isReadBy(username))
                .sorted(Comparator.comparing(Reply::getCreatedAt))
                .collect(Collectors.toList());
    }

    // =====================================================================
    // READ TRACKING
    // =====================================================================

    public void markPostRead(int postId, String username) {
        postReaders.computeIfAbsent(postId, k -> new HashSet<>()).add(username);
    }

    private boolean isPostReadBy(int postId, String username) {
        return postReaders.getOrDefault(postId, Set.of()).contains(username);
    }

    public List<Post> listUnreadPosts(String username) {
        return repo.findAll().stream()
                .filter(p -> !isPostReadBy(p.getId(), username))
                .sorted(Comparator.comparing(Post::getCreatedAt).reversed())
                .collect(Collectors.toList());
    }

    // =====================================================================
    // SEARCH
    // =====================================================================

    public List<Post> searchPosts(String keyword, String thread) {
        return repo.search(
                new SearchSpec()
                        .query(keyword)
                        .thread(thread)
                        .includeDeleted(false)
        );
    }

    // =====================================================================
    // REASONABLE ANSWERS
    // =====================================================================

    public boolean isAnswerReasonable(Reply reply) {
        if (reply == null) return false;

        String content = reply.getContent().trim().toLowerCase();
        if (content.length() < 20) return false;
        if (content.split("\\s+").length < 5) return false;

        List<String> notReasonable = List.of("yes", "thanks", "ok", "no", "maybe", "i don't know");
        return !notReasonable.contains(content);
    }

    // =====================================================================
    // HELP-PEERS DASHBOARD
    // =====================================================================

    public Map<String, Set<String>> calculateStudentHelpedPeers() {
        Map<String, Set<String>> helped = new HashMap<>();

        for (Reply r : replies.values()) {
            repo.findById(r.getParentPostId()).ifPresent(parent -> {
                if (!r.getAuthor().equals(parent.getAuthor())) {
                    helped.computeIfAbsent(r.getAuthor(), k -> new HashSet<>())
                            .add(parent.getAuthor());
                }
            });
        }
        return helped;
    }

    public Set<String> getStudentsWhoHelpedAtLeast(int minPeers) {
        return calculateStudentHelpedPeers().entrySet().stream()
                .filter(e -> e.getValue().size() >= minPeers)
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());
    }

    // =====================================================================
    // GRADING CSV EXPORT
    // =====================================================================

    public String exportGradingSummaryCSV() {
        StringBuilder sb = new StringBuilder();
        sb.append("Student,Answers Posted,Peers Helped,Flagged/Unreasonable Answers\n");

        Map<String, Integer> postedAnswers = new HashMap<>();
        Map<String, Integer> unreasonable = new HashMap<>();
        Map<String, Set<String>> helped = calculateStudentHelpedPeers();

        for (Reply r : replies.values()) {
            postedAnswers.merge(r.getAuthor(), 1, Integer::sum);
            if (!isAnswerReasonable(r)) {
                unreasonable.merge(r.getAuthor(), 1, Integer::sum);
            }
        }

        Set<String> all = new HashSet<>(postedAnswers.keySet());
        all.addAll(helped.keySet());

        for (String s : all) {
            sb.append(String.format("%s,%d,%d,%d\n",
                    s,
                    postedAnswers.getOrDefault(s, 0),
                    helped.getOrDefault(s, Set.of()).size(),
                    unreasonable.getOrDefault(s, 0)
            ));
        }
        return sb.toString();
    }

    // =====================================================================
    // DISPLAY HELPERS
    // =====================================================================

    public String getReplyDisplayContent(int replyId) {
        Reply r = replies.get(replyId);
        if (r == null) return "";

        boolean parentDeleted = repo.findById(r.getParentPostId())
                .map(Post::isDeleted).orElse(false);

        return r.getContent() + (parentDeleted ? " (original post deleted)" : "");
    }

    // =====================================================================
    // POST SUMMARIES
    // =====================================================================

    public Map<String, Object> getPostSummary(Post post, String username) {
        long replyCount = replies.values().stream()
                .filter(r -> r.getParentPostId() == post.getId()).count();

        long unreadReplies = replies.values().stream()
                .filter(r -> r.getParentPostId() == post.getId())
                .filter(r -> !r.isReadBy(username)).count();

        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", post.getId());
        map.put("author", post.getAuthor());
        map.put("thread", post.getThread());
        map.put("content", post.getContent());
        map.put("createdAt", post.getCreatedAt());
        map.put("deleted", post.isDeleted());
        map.put("moderation", post.getModeration());
        map.put("flagReason", getFlagReason(post.getId()));
        map.put("replies", replyCount);
        map.put("unreadReplies", unreadReplies);
        map.put("read", isPostReadBy(post.getId(), username));

        return map;
    }

    public List<Map<String, Object>> listPostSummaries(String username, boolean othersOnly) {
        return repo.findAll().stream()
                .filter(p -> !othersOnly || !p.getAuthor().equals(username))
                .sorted(Comparator.comparing(Post::getCreatedAt).reversed())
                .map(p -> getPostSummary(p, username))
                .collect(Collectors.toList());
    }

	public Collection<Parameter> listAllPosts() {
		// TODO Auto-generated method stub
		return null;
	}

	public List<Map<String, Object>> getRepliesForPostSummary(int postId) {
		// TODO Auto-generated method stub
		return null;
	}
}
