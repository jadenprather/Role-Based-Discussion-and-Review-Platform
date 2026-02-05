/**
 * <h2>Requirement → Test Mapping</h2>
 * R-P1 (Create) → testCreatePost_valid(), testCreatePost_rejectsEmptyTitle()
 * R-P2 (Read)   → testFindById_valid(), testFindAll_returnsAll(), testFindByThread_filters()
 * R-P3 (Update) → testUpdateContent_persists(), testUpdateContent_rejectsTooLong()
 * R-P4 (Delete) → testSoftDelete_marksDeleted(), testDelete_idempotent()
 *
 * R-G1 (MVC wiring) → controllerCreate_callsRepoAndReturnsId()
 * R-G2 (Validation surfaced) → manual_UI_showsValidationMessage()  (MANUAL)
 * R-G3 (List/Search/Filter) → controllerSearch_filtersList()
 * R-G4 (Edit persists) → controllerUpdate_reflectedInRead()
 * R-G5 (Delete reflected) → controllerDelete_reflectedInRead()
 *
 * R-T1 (Input validation) → testInvalidInputs_various()
 * R-T2 (Store consistency) → testRepoConsistency_afterOperations()
 * R-T3 (ID uniqueness, if used) → testGeneratedIds_unique()
 */


package studentPostsTest;
import studentPosts.InMemoryPostRepository;
// …your controllers/services/models from studentPosts…


import entityClasses.Post;
import entityClasses.Post.Moderation;

import java.util.*;
import java.util.stream.Collectors;

/** Minimal, no-deps repository for Task 5. */
public class InMemoryPostRepositoryTests {
    private final Map<Integer, Post> store = new HashMap<>();

    // CREATE
    public Post create(int id, String author, String thread, String content) {
        Post p = Post.createNew(id, author, thread, content);
        store.put(id, p);
        return p;
    }

    // READ
    public Optional<Post> findById(int id){ return Optional.ofNullable(store.get(id)); }
    public List<Post> findAll(){ return new ArrayList<>(store.values()); }
    public List<Post> findByThread(String thread){
        if (thread==null) return List.of();
        String t = thread.toLowerCase();
        return store.values().stream().filter(p -> p.getThread().toLowerCase().equals(t)).collect(Collectors.toList());
    }

    // UPDATE
    public Post updateContent(int id, String newContent){
        Post cur = require(id);
        Post edited = cur.withContent(newContent);
        store.put(id, edited);
        return edited;
    }

    public Post moderate(int id, Moderation m){
        Post cur = require(id);
        Post mod = cur.withModeration(m);
        store.put(id, mod);
        return mod;
    }

    // DELETE (soft)
    public Post softDelete(int id){
        Post cur = require(id);
        Post tomb = cur.softDeleted();
        store.put(id, tomb);
        return tomb;
    }

    // SEARCH
    public List<Post> search(SearchSpecTests spec){
        String q = spec==null||spec.query==null? null : spec.query.toLowerCase();
        String t = spec==null||spec.thread==null? null : spec.thread.toLowerCase();
        boolean includeDeleted = spec!=null && spec.includeDeleted;

        return store.values().stream()
                .filter(p -> includeDeleted || !p.isDeleted())
                .filter(p -> t==null || p.getThread().toLowerCase().equals(t))
                .filter(p -> {
                    if (q==null) return true;
                    return p.getAuthor().toLowerCase().contains(q)
                        || p.getThread().toLowerCase().contains(q)
                        || p.getRawContent().toLowerCase().contains(q);
                })
                // deterministic: newest first, then id
                .sorted(Comparator
                        .comparing(Post::getCreatedAt, Comparator.reverseOrder())
                        .thenComparingInt(Post::getId).reversed())
                .collect(Collectors.toList());
    }

    private Post require(int id){
        Post p = store.get(id);
        if (p==null) throw new NoSuchElementException("post "+id+" not found");
        return p;
    }
}

