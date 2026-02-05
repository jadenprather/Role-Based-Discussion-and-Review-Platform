package studentPostsTest;

/** Dependency-free search spec for Post repository. */
public class SearchSpecTests {
    public String query;             // substring over author/thread/raw content (case-insensitive)
    public String thread;            // exact thread match (case-insensitive)
    public boolean includeDeleted;   // default false

    public SearchSpecTests query(String q){ this.query=q; return this; }
    public SearchSpecTests thread(String t){ this.thread=t; return this; }
    public SearchSpecTests includeDeleted(boolean b){ this.includeDeleted=b; return this; }
}

