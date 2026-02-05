package entityClasses;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * # Post
 * Domain entity representing a student discussion post.
 *
 * <h2>Purpose &amp; Rationale</h2>
 * <p>
 * Implements TP2 Task 3 requirements: a Post class that supports CRUD and input
 * validation needed by the Students User Stories:
 * </p>
 * <ul>
 *   <li><b>Create</b>: create a post in a thread (required fields + validation).</li>
 *   <li><b>Read</b>: getters expose display data; {@link #getContent()} masks deleted posts.</li>
 *   <li><b>Update</b>: edit content ({@link #withContent(String)}) with audit, and moderation state
 *       ({@link #withModeration(Moderation)}).</li>
 *   <li><b>Delete</b>: soft delete ({@link #softDeleted()}) so replies remain; UI shows "[deleted]".</li>
 * </ul>
 *
 * <h3>Attributes &amp; Sources</h3>
 * <ul>
 *   <li><b>id</b>: unique identifier (repository/service assigned). Needed to locate/edit/delete.</li>
 *   <li><b>author</b>: username of poster (required). From authenticated session.</li>
 *   <li><b>thread</b>: discussion thread name (required). Must correspond to a thread created by staff.</li>
 *   <li><b>content</b>: original post text (required; length-bounded). Used for display &amp; search.</li>
 *   <li><b>createdAt</b>: creation timestamp (immutable).</li>
 *   <li><b>editedAt</b>: last edit timestamp (null until edited).</li>
 *   <li><b>deleted</b>: soft delete flag (per student story: replies persist; viewers see "[deleted]").</li>
 *   <li><b>moderation</b>: status to support staff epics (FLAGGED/HIDDEN) in TP3.</li>
 * </ul>
 *
 * <h3>Validation Policy</h3>
 * <ul>
 *   <li><b>author</b>, <b>thread</b>, <b>content</b> are required and trimmed.</li>
 *   <li><b>content</b> length &le; {@link #MAX_CONTENT_LEN} (constant for testability).</li>
 *   <li>No control characters in content except tab/newline/carriage return.</li>
 *   <li>Thread existence/whitelist should be enforced by the calling service (e.g., ThreadService).
 *       This class enforces only non-blank thread names to remain storage-agnostic.</li>
 * </ul>
 *
 * <h3>Design Notes</h3>
 * <ul>
 *   <li>Immutability: all state is final; "updates" return new instances to simplify reasoning {@literal &}
 *       support auditability.</li>
 *   <li>Soft delete: {@link #getContent()} returns "[deleted]" when {@code deleted==true},
 *       while {@link #getRawContent()} exposes the original text for staff/search/testing.</li>
 *   <li>Moderation: {@link Moderation} is a forward-looking hook for TP3 staff workflows.</li>
 * </ul>
 *
 * <p><b>Story tags (inline in code)</b></p>
 * <ul>
 *   <li>US-STUDENT-POST-TO-THREAD – constructor/validation of required fields.</li>
 *   <li>US-STUDENT-DELETE-POST – {@link #softDeleted()}, masking in {@link #getContent()}.</li>
 *   <li>US-STUDENT-SEARCH – {@link #getRawContent()} preserved for search indexing.</li>
 *   <li>TP3-STAFF-MODERATION – {@link Moderation}, {@link #withModeration(Moderation)}.</li>
 * </ul>
 *
 * @author Christopher, Sujal, Cherry, Ruchita, Jaden
 * @version 1.2
 * @since 2025-10
 */
public final class Post {

    /** Max allowed content length (use in tests and UI validation). */
    public static final int MAX_CONTENT_LEN = 4096;

    /**
     * Moderation status for a post, used by staff workflows.
     */
    public enum Moderation {
        /** Default state: visible to all users. */
        NORMAL,
        /** Flagged for staff attention; typically still visible. */
        FLAGGED,
        /** Hidden from students; visible only to staff/admin. */
        HIDDEN
    }

    // --- Core identity & authorship ---
    private final int id;
    private final String author;
    private final String thread;

    // --- Text & audit fields ---
    private final String content;           // original text; never mutated
    private final LocalDateTime createdAt;  // immutable creation time
    private final LocalDateTime editedAt;   // null until edited
    private final boolean deleted;          // soft-delete flag
    private final Moderation moderation;    // moderation state (default NORMAL)

    // ========================================================================
    // Factory & "mutators" (return new instances) ——— CRUD
    // ========================================================================

    /**
     * Factory used by repository/service when creating a new Post.
     * <p>US-STUDENT-POST-TO-THREAD</p>
     *
     * @param id       unique id (assigned by repository/service)
     * @param author   username (required, non-blank)
     * @param thread   thread name (required, non-blank)
     * @param content  post text (required, 1..MAX_CONTENT_LEN)
     * @return new Post instance
     * @throws IllegalArgumentException if validation fails
     */
    public static Post createNew(int id, String author, String thread, String content) {
        validate(author, thread, content);
        return new Post(
                id,
                author.trim(),
                thread.trim(),
                content.trim(),
                LocalDateTime.now(),
                /* deleted */ false,
                /* editedAt */ null,
                /* moderation */ Moderation.NORMAL
        );
    }

    /**
     * Returns a copy with updated content (keeps audit fields; stamps {@code editedAt}).
     * <p>Supports "Update" in CRUD.</p>
     *
     * @param newContent replacement body text
     * @return new Post reflecting the edit
     * @throws IllegalArgumentException if the new content is invalid
     */
    public Post withContent(String newContent) {
        // US-STUDENT-POST-TO-THREAD (validation reused for edits)
        validate(author, thread, newContent);
        return new Post(
                id, author, thread, newContent.trim(),
                createdAt, deleted,
                /* editedAt */ LocalDateTime.now(),
                moderation
        );
    }

    /**
     * Returns a copy marked as deleted (soft delete; replies remain).
     * <p>US-STUDENT-DELETE-POST</p>
     *
     * @return new Post with {@code deleted==true}
     */
    public Post softDeleted() {
        return new Post(
                id, author, thread, content,
                createdAt, /* deleted */ true,
                editedAt, moderation
        );
    }

    /**
     * Returns a copy with updated moderation state (FLAGGED/HIDDEN).
     * <p>TP3-STAFF-MODERATION</p>
     *
     * @param m new moderation (null is treated as no change)
     * @return new Post with moderation applied
     */
    public Post withModeration(Moderation m) {
        return new Post(
                id, author, thread, content,
                createdAt, deleted, editedAt,
                (m == null ? moderation : m)
        );
    }

    // Validation (shared by create and update)
    private static void validate(String author, String thread, String content) {
        if (author == null || author.isBlank())
            throw new IllegalArgumentException("author is required");
        if (thread == null || thread.isBlank())
            throw new IllegalArgumentException("thread is required");
        if (content == null || content.isBlank())
            throw new IllegalArgumentException("content is required");
        if (content.length() > MAX_CONTENT_LEN)
            throw new IllegalArgumentException("content too long (max " + MAX_CONTENT_LEN + ")");
        // Disallow control chars except tab/newline/cr
        boolean hasBadControl = content.chars().anyMatch(ch ->
                Character.isISOControl(ch) && ch != '\n' && ch != '\r' && ch != '\t'
        );
        if (hasBadControl)
            throw new IllegalArgumentException("content contains disallowed control characters");
    }

    // ========================================================================
    // Constructor (private: enforce factory & immutability)
    // ========================================================================

    private Post(
            int id,
            String author,
            String thread,
            String content,
            LocalDateTime createdAt,
            boolean deleted,
            LocalDateTime editedAt,
            Moderation moderation
    ) {
        this.id = id;
        this.author = author;
        this.thread = thread;
        this.content = content;
        this.createdAt = createdAt;
        this.deleted = deleted;
        this.editedAt = editedAt;
        this.moderation = (moderation == null ? Moderation.NORMAL : moderation);
    }

    // ========================================================================
    // Read (getters) ——— CRUD
    // ========================================================================

    /** Gets the unique identifier of this post. 
     *  @return unique identifier of this post (repository-assigned). */
    public int getId() { return id; }

    /** Gets the immutable author username. 
     *  @return immutable author username. */
    public String getAuthor() { return author; }

    /** Gets the thread name this post belongs to. 
     *  @return thread name this post belongs to. */
    public String getThread() { return thread; }

    /** 
     * Gets the display content. If the post is soft-deleted, returns the tombstone string.
     * <p>US-STUDENT-DELETE-POST: UI should render this value.</p>
     * @return original content or {@code "[deleted]"} if soft-deleted.
     */
    public String getContent() { return deleted ? "[deleted]" : content; }

    /** 
     * Gets the original (unmasked) content. Useful for search, staff views, and tests.
     * <p>US-STUDENT-SEARCH</p>
     * @return the original (non-tombstoned) content string.
     */
    public String getRawContent() { return content; }

    /** Gets the immutable creation timestamp. 
     *  @return creation timestamp (immutable). */
    public LocalDateTime getCreatedAt() { return createdAt; }

    /** Gets the last edit timestamp, if any. 
     *  @return last edit timestamp, or {@code null} if never edited. */
    public LocalDateTime getEditedAt() { return editedAt; }

    /** Indicates whether this post is soft-deleted. 
     *  @return whether this post is soft-deleted. */
    public boolean isDeleted() { return deleted; }

    /** Gets the moderation status for staff workflows. 
     *  @return moderation status for staff workflows. */
    public Moderation getModeration() { return moderation; }

    // Equality/Hashing/Debugging

    /** Identity-based equality (id only). */
    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Post)) return false;
        return id == ((Post) o).id;
    }

    /** Identity-based hashing (id only). */
    @Override public int hashCode() { return Objects.hash(id); }

    /** Concise debug string for logs and screenshots. */
    @Override public String toString() {
        return "Post#" + id + "@" + thread + " by " + author + (deleted ? " [deleted]" : "");
    }
}
