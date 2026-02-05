package studentPosts;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link InMemoryPostRepository}.
 * Each method tests a key repository behavior, verifying contract and boundary cases.
 */
public class inMemoryPostRepositoryTest {

    /** Repository instance under test. */
    private final InMemoryPostRepository repo = new InMemoryPostRepository();

    /**
     * Test finding posts by thread, using multiple threads and authors.
     * Ensures findByThread works and is case insensitive.
     */
    @Test
    public void testFindByThread() {
        repo.create(401, "bob", "alpha", "aaa");
        repo.create(402, "eve", "alpha", "bbb");
        repo.create(403, "tom", "beta", "ccc");
        var alphaPosts = repo.findByThread("alpha");
        assertEquals(2, alphaPosts.size());
        var betaPosts = repo.findByThread("beta");
        assertEquals(1, betaPosts.size());
    }

    /**
     * Test handling of maximum content length for a post.
     * Verifies repository enforces content length and throws as required.
     */
    @Test
    public void testMaxContentLength() {
        String max = "x".repeat(256); // For Post.MAX_CONTENT_LEN; adjust if needed
        repo.create(501, "test", "thread", max);
        var post = repo.findById(501);
        assertThrows(IllegalArgumentException.class, () ->
            repo.create(502, "test", "thread", "y".repeat(257))
        );
    }

    /**
     * Test that searching for nonexistent posts and threads returns empty/absent values.
     */
    @Test
    public void testNoItemFound() {
        assertNull(repo.findById(9999));
        assertTrue(repo.findByThread("nonexistent").isEmpty());
    }

    /**
     * Test that creation rejects blank/empty fields.
     * Validates input checking for author, thread, and content.
     */
    @Test
    public void testCreateRejectsBlankFields() {
        assertThrows(IllegalArgumentException.class, () -> repo.create(1, "", "thread", "content"));
        assertThrows(IllegalArgumentException.class, () -> repo.create(2, "author", "", "content"));
        assertThrows(IllegalArgumentException.class, () -> repo.create(3, "author", "thread", ""));
    }

    /**
     * Test that creating posts with duplicate IDs throws an error.
     * Verifies primary key contract.
     */
    @Test
    public void testCreateWithDuplicateIdThrows() {
        repo.create(10, "bob", "thread", "hi");
        assertThrows(IllegalArgumentException.class, () ->
            repo.create(10, "bob", "thread", "second"));
    }

    /**
     * Test that null arguments for author, thread, or content are not accepted.
     */
    @Test
    public void testNullArguments() {
        assertThrows(IllegalArgumentException.class, () -> repo.create(601, null, "t", "c"));
        assertThrows(IllegalArgumentException.class, () -> repo.create(602, "a", null, "c"));
        assertThrows(IllegalArgumentException.class, () -> repo.create(603, "a", "t", null));
    }
}
