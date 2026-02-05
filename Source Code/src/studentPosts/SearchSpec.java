package studentPosts;

/** Dependency-free search spec for Post repository. */
public class SearchSpec {
    public String query;             // substring over author/thread/raw content (case-insensitive)
    public String thread;            // exact thread match (case-insensitive)
    public boolean includeDeleted;   // default false

    public SearchSpec query(String q){ this.query=q; return this; }
    public SearchSpec thread(String t){ this.thread=t; return this; }
    public SearchSpec includeDeleted(boolean b){ this.includeDeleted=b; return this; }
}

