package studentPosts;

import entityClasses.Post;
import entityClasses.Post.Moderation;

import java.util.*;
import java.util.stream.Collectors;

/**
 * A simple in-memory repository used for storing and retrieving Post objects.
 * This class has no external dependencies and is mainly used for testing,
 * prototypes, and assignments. All posts are kept in a HashMap while the
 * program is running.
 */
public class InMemoryPostRepository {

    /** Stores all posts by their ID. */
    private final Map<Integer, Post> store = new HashMap<>();

    // ============================================================
    // CREATE
    // ============================================================

    /**
     * Creates a new Post and adds it to the repository.
     * The Post class handles all validation.
     */
    public Post create(int id, String author, String thread, String content) {
        Post p = Post.createNew(id, author, thread, content);
        store.put(id, p);
        return p;
    }

    // ============================================================
    // READ
    // ============================================================

    /** Returns the post with the given ID, if it exists. */
    public Optional<Post> findById(int id) {
        return Optional.ofNullable(store.get(id));
    }

    /** Returns a list of all posts currently stored. */
    public List<Post> findAll() {
        return new ArrayList<>(store.values());
    }

    /**
     * Finds all posts that belong to the given thread name.
     * Thread matching is case-insensitive.
     */
    public List<Post> findByThread(String thread) {
        if (thread == null) return List.of();
        String t = thread.toLowerCase();

        return store.values().stream()
                .filter(p -> p.getThread().toLowerCase().equals(t))
                .collect(Collectors.toList());
    }

    // ============================================================
    // UPDATE
    // ============================================================

    /**
     * Updates the content of a post by creating a new edited copy.
     * The original post is replaced in the repository.
     */
    public Post updateContent(int id, String newContent) {
        Post cur = require(id);
        Post edited = cur.withContent(newContent);
        store.put(id, edited);
        return edited;
    }

    /**
     * Updates the moderation status (NORMAL, FLAGGED, HIDDEN).
     */
    public Post moderate(int id, Moderation m) {
        Post cur = require(id);
        Post mod = cur.withModeration(m);
        store.put(id, mod);
        return mod;
    }

    // ============================================================
    // DELETE (SOFT DELETE)
    // ============================================================

    /**
     * Soft-deletes a post. The post remains in the repository,
     * but is marked as deleted so its content is hidden.
     */
    public Post softDelete(int id) {
        Post cur = require(id);
        Post tomb = cur.softDeleted();
        store.put(id, tomb);
        return tomb;
    }

    // ============================================================
    // SEARCH
    // ============================================================

    /**
     * Searches through the repository using a SearchSpec.
     * You can filter by keyword, thread, and whether deleted posts
     * should be included. Results are sorted newest first.
     */
    public List<Post> search(SearchSpec spec) {

        String q = (spec == null || spec.query == null)
                ? null : spec.query.toLowerCase();

        String t = (spec == null || spec.thread == null)
                ? null : spec.thread.toLowerCase();

        boolean includeDeleted = (spec != null && spec.includeDeleted);

        return store.values().stream()
                .filter(p -> includeDeleted || !p.isDeleted())
                .filter(p -> t == null || p.getThread().toLowerCase().equals(t))
                .filter(p -> {
                    if (q == null) return true;
                    return p.getAuthor().toLowerCase().contains(q)
                        || p.getThread().toLowerCase().contains(q)
                        || p.getRawContent().toLowerCase().contains(q);
                })
                .sorted(
                        Comparator.comparing(Post::getCreatedAt, Comparator.reverseOrder())
                                  .thenComparingInt(Post::getId).reversed()
                )
                .collect(Collectors.toList());
    }

    // ============================================================
    // INTERNAL HELPERS
    // ============================================================

    /**
     * Returns the post if it exists, otherwise throws an exception.
     * This makes certain update operations easier to implement.
     */
    private Post require(int id) {
        Post p = store.get(id);
        if (p == null) {
            throw new NoSuchElementException("post " + id + " not found");
        }
        return p;
    }

    /**
     * Placeholder for assignments that may need a full update method.
     * Currently unused.
     */
    public void update(Post updated) {
        // Method intentionally left blank.
        // Some assignments require the signature, but not the implementation.
    }
}
