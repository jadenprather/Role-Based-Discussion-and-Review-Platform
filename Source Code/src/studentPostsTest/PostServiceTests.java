package studentPostsTest;

import entityClasses.Post;

import java.util.NoSuchElementException;

public class PostServiceTests {
    private int passed=0, failed=0;
    public static void main(String[] args) throws Exception { new PostServiceTests().run(); }

    private void run() throws Exception {
        crud_flow_create_read_update_delete();
        search_thread_query_deleted_flag();
        notFound_errors();

        System.out.printf("%nPostServiceTest %s (%d/%d)%n",
                failed==0?"PASSED":"DONE WITH FAILURES", passed, passed+failed);
        if (failed>0) System.exit(1);
    }

    private void crud_flow_create_read_update_delete() throws InterruptedException {
        InMemoryPostRepositoryTests repo = new InMemoryPostRepositoryTests();

        Post p = repo.create(1,"alice","general","Hello");
        assertTrue(repo.findById(1).isPresent(),"created visible");

        Thread.sleep(5);
        Post edited = repo.updateContent(1,"Hello world");
        assertTrue(edited.getEditedAt()!=null,"editedAt set");
        assertEq("Hello world", edited.getContent(),"content changed");

        Post deleted = repo.softDelete(1);
        assertTrue(deleted.isDeleted(),"soft deleted");
        assertEq("[deleted]", deleted.getContent(),"masked");
        assertEq("Hello world", deleted.getRawContent(),"raw preserved");
        pass("crud_flow_create_read_update_delete");
    }

    private void search_thread_query_deleted_flag(){
        InMemoryPostRepositoryTests repo = new InMemoryPostRepositoryTests();
        repo.create(10,"ann","math","limits");
        repo.create(11,"bob","math","derivatives");
        repo.create(12,"cal","cs","arrays and lists");
        repo.softDelete(12);

        var math = repo.search(new SearchSpecTests().thread("math"));
        assertEq(2, math.size(), "thread filter");

        var arraysDefault = repo.search(new SearchSpecTests().query("arrays"));
        assertEq(0, arraysDefault.size(), "deleted excluded");

        var arraysIncl = repo.search(new SearchSpecTests().query("arrays").includeDeleted(true));
        assertEq(1, arraysIncl.size(), "include deleted");
        pass("search_thread_query_deleted_flag");
    }

    private void notFound_errors(){
        InMemoryPostRepositoryTests repo = new InMemoryPostRepositoryTests();
        try { repo.updateContent(99,"x"); fail("expected NoSuchElement"); }
        catch (NoSuchElementException ok) { pass("notFound:update"); }
        try { repo.softDelete(99); fail("expected NoSuchElement"); }
        catch (NoSuchElementException ok) { pass("notFound:delete"); }
    }

    // tiny asserts
    private void assertTrue(boolean c,String m){ if(!c) fail("assertTrue: "+m); }
    private void assertEq(Object e,Object a,String m){ if(e==null? a!=null:!e.equals(a)) fail("assertEq: "+m+" exp="+e+" act="+a); }
    private void pass(String n){ passed++; System.out.println("✅ "+n); }
    private void fail(String m){ failed++; System.err.println("❌ "+m); }
}
