/**
 * <h1>StudentPostTests - ManualTestRunner</h1>
 * 
 * Purpose: Demonstrates semi-automated verification of Student User Stories
 * (R-P1 through R-P4) using print-based validation instead of JUnit.
 *
 * Each test prints PASS/FAIL results directly to the console:
 *  • R-P1 (Create) — verified that create() adds a valid Post  
 *  • R-P2 (Read) — verified that findById() retrieves the Post  
 *  • R-P3 (Update) — verified that updateContent() changes the content  
 *  • R-P4 (Delete) — verified that softDelete() hides or flags the Post
 *
 * The screenshot in the appendix shows the console output confirming
 * “4/4 passed,” proving the implementation satisfies all CRUD requirements.
 */


package studentPostsTest;

import studentPosts.InMemoryPostRepository;
import entityClasses.Post;

public class MannualTestRunner {
    public static void main(String[] args) {
        InMemoryPostRepository repo = new InMemoryPostRepository();
        int passed = 0, total = 0;

        // R-P1: Create
        total++;
        try {
            Post p = repo.create(1, "alice", "thread-1", "hello");
            if (p != null && "alice".equals(p.getAuthor())) {
                System.out.println("R-P1 (Create): PASS");
                passed++;
            } else {
                System.out.println("R-P1 (Create): FAIL");
            }
        } catch (Throwable t) {
            System.out.println("R-P1 (Create): FAIL " + t);
        }

        // R-P2: Read
        total++;
        try {
            boolean ok = repo.findById(1).isPresent();
            System.out.println("R-P2 (Read): " + (ok ? "PASS" : "FAIL"));
            if (ok) passed++;
        } catch (Throwable t) {
            System.out.println("R-P2 (Read): FAIL " + t);
        }

        // R-P3: Update
        total++;
        try {
            repo.updateContent(1, "new");
            boolean ok = repo.findById(1).get().getContent().equals("new");
            System.out.println("R-P3 (Update): " + (ok ? "PASS" : "FAIL"));
            if (ok) passed++;
        } catch (Throwable t) {
            System.out.println("R-P3 (Update): FAIL " + t);
        }

        // R-P4: Delete (soft)
        total++;
        try {
            repo.softDelete(1);
            boolean ok = repo.findById(1).isEmpty() ||
                         (repo.findById(1).isPresent() && repo.findById(1).get().isDeleted());
            System.out.println("R-P4 (Delete): " + (ok ? "PASS" : "FAIL"));
            if (ok) passed++;
        } catch (Throwable t) {
            System.out.println("R-P4 (Delete): FAIL " + t);
        }

        System.out.printf("StudentPostTests Summary: %d/%d passed%n", passed, total);
    }
}
