package student;



import java.time.LocalDateTime;

import java.util.*;

import java.util.concurrent.atomic.AtomicInteger;

import java.util.stream.Collectors;



/**

 * StudentRequirements.java

 *

 * Single-file example implementation for the "student requirements" feature set.

 * Add this file under: src/studentRequirements/

 * NOTE: This is an in-memory implementation for demo/testing.

 * For production, integrate with DB or persistent storage.

 */

public class StudentRequirements {



    // ----------- Model classes ------------

    public static class Reply {

        private final int id;

        private final String author;

        private final String content;

        private final LocalDateTime createdAt;

        private final int parentPostId;



        // Track readers of this reply (student usernames)

        private final Set<String> readers = new HashSet<>();



        public Reply(int id, String author, String content, int parentPostId) {

            this.id = id;

            this.author = author;

            this.content = content;

            this.parentPostId = parentPostId;

            this.createdAt = LocalDateTime.now();

        }



        public void markRead(String username) {

            readers.add(username);

        }



        public boolean isReadBy(String username) {

            return readers.contains(username);

        }



        public int getId() { return id; }

        public String getAuthor() { return author; }

        public String getContent() { return content; }

        public LocalDateTime getCreatedAt() { return createdAt; }

        public int getParentPostId() { return parentPostId; }

    }



    public static class Post {

        private final int id;

        private final String author;

        private final String content;

        private final String thread;

        private final LocalDateTime createdAt;

        private final List<Reply> replies = new ArrayList<>();



        // Track readers of this post

        private final Set<String> readers = new HashSet<>();

        private boolean deleted = false;



        public Post(int id, String author, String content, String thread) {

            this.id = id;

            this.author = author;

            this.content = content;

            this.thread = thread == null || thread.isBlank() ? "General" : thread;

            this.createdAt = LocalDateTime.now();

        }



        public void markRead(String username) {

            readers.add(username);

        }



        public boolean isReadBy(String username) {

            return readers.contains(username);

        }



        public void addReply(Reply reply) {

            replies.add(reply);

        }



        public List<Reply> getReplies() { return replies; }

        public int getId() { return id; }

        public String getAuthor() { return author; }

        public String getContent() { return deleted ? "[deleted]" : content; }

        public String getThread() { return thread; }

        public LocalDateTime getCreatedAt() { return createdAt; }

        public boolean isDeleted() { return deleted; }

        public void setDeleted() { this.deleted = true; }

    }



    // ----------- Service logic ------------

    private final Map<Integer, Post> posts = new HashMap<>();

    private final Map<Integer, Reply> replies = new HashMap<>();

    private final AtomicInteger postIdCounter = new AtomicInteger(1);

    private final AtomicInteger replyIdCounter = new AtomicInteger(1);



    // Create new post

    public Post createPost(String author, String content, String thread) {

        int id = postIdCounter.getAndIncrement();

        Post post = new Post(id, author, content, thread);

        posts.put(id, post);

        return post;

    }



    // Delete post (mark as deleted, keep replies)

    public boolean deletePost(int postId) {

        Post post = posts.get(postId);

        if (post != null) {

            post.setDeleted();

            return true;

        }

        return false;

    }



    // Add reply to post

    public Reply addReply(int postId, String author, String content) {

        Post post = posts.get(postId);

        if (post == null) return null;

        int id = replyIdCounter.getAndIncrement();

        Reply reply = new Reply(id, author, content, postId);

        replies.put(id, reply);

        post.addReply(reply);

        return reply;

    }



    // List all posts

    public List<Post> listAllPosts() {

        return new ArrayList<>(posts.values());

    }



    // Search posts by keyword

    public List<Post> searchPosts(String keyword, String thread) {

        return posts.values().stream()

            .filter(p -> (thread == null || thread.isBlank() || p.getThread().equalsIgnoreCase(thread)))

            .filter(p -> p.getContent().toLowerCase().contains(keyword.toLowerCase()))

            .collect(Collectors.toList());

    }



    // Mark post as read

    public void markPostRead(int postId, String username) {

        Post post = posts.get(postId);

        if (post != null) {

            post.markRead(username);

        }

    }



    // Mark reply as read

    public void markReplyRead(int replyId, String username) {

        Reply reply = replies.get(replyId);

        if (reply != null) {

            reply.markRead(username);

        }

    }



    // List unread posts for user

    public List<Post> listUnreadPosts(String username) {

        return posts.values().stream()

            .filter(p -> !p.isReadBy(username))

            .collect(Collectors.toList());

    }



    // List replies of a post (all or unread only)

    public List<Reply> listReplies(int postId, String username, boolean unreadOnly) {

        Post post = posts.get(postId);

        if (post == null) return List.of();

        return post.getReplies().stream()

            .filter(r -> !unreadOnly || !r.isReadBy(username))

            .collect(Collectors.toList());

    }



    // Utility: Get summary of posts (with replies + unread count)

    public Map<String, Object> getPostSummary(Post post, String username) {

        Map<String, Object> summary = new HashMap<>();

        summary.put("id", post.getId());

        summary.put("author", post.getAuthor());

        summary.put("thread", post.getThread());

        summary.put("content", post.getContent());

        summary.put("createdAt", post.getCreatedAt());

        summary.put("deleted", post.isDeleted());

        summary.put("replies", post.getReplies().size());

        long unreadReplies = post.getReplies().stream()

            .filter(r -> !r.isReadBy(username))

            .count();

        summary.put("unreadReplies", unreadReplies);

        return summary;

    }



    // ---------- Demo main ----------

    public static void main(String[] args) {

        StudentRequirements sr = new StudentRequirements();

        Post p1 = sr.createPost("Alice", "How do I fix error X?", "General");

        sr.addReply(p1.getId(), "Bob", "Try cleaning and rebuilding the project.");

        sr.addReply(p1.getId(), "Charlie", "Check your JDK version.");

       



        sr.markPostRead(p1.getId(), "Eve");



        System.out.println("=== All Posts ===");

        sr.listAllPosts().forEach(p -> System.out.println(sr.getPostSummary(p, "Eve")));



        System.out.println("\n=== Search 'IDE' ===");

        sr.searchPosts("IDE", null).forEach(p -> System.out.println(p.getContent()));



        System.out.println("\n=== Unread Posts for Eve ===");

        sr.listUnreadPosts("Eve").forEach(p -> System.out.println(p.getContent()));

    }

}


