package services;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/*******
 * <p> Title: Shared Thread class. </p>
 * 
 * <p> This is making sure the Studentboard, Review, and DiscussionBoard use the same thread list </p>
 * 
 * 
 * @author Christopher Nguyen
 * 
 * @version 2.00		
 * 	
 */

public class ThreadService {
    private final LinkedHashSet<String> threads = new LinkedHashSet<>();

    public ThreadService() {
        // seed defaults (can be empty if you want)
        threads.add("General");
        threads.add("Homework");
        threads.add("Projects");
    }

    /** list in insertion order */
    public List<String> listThreads() {
        return threads.stream().collect(Collectors.toList());
    }

    public boolean hasThread(String name) {
        return name != null && threads.contains(name.trim());
    }

    public boolean addThread(String name) {
        if (name == null) return false;
        String n = name.trim();
        if (n.isEmpty()) return false;
        return threads.add(n);
    }

    /** rename thread; returns false if from missing or to already exists */
    public boolean renameThread(String from, String to) {
        if (!hasThread(from) || to == null) return false;
        String target = to.trim();
        if (target.isEmpty() || threads.contains(target)) return false;
        threads.remove(from);
        threads.add(target);
        return true;
    }

    /**
     * delete thread. Policy options:
     *  - return false if the thread is "General" (protect default)
     *  - caller must decide what to do with posts (block, or reassign)
     */
    public boolean deleteThread(String name) {
        if (name == null) return false;
        String n = name.trim();
        if (n.equals("General")) return false; // keep a default
        return threads.remove(n);
    }
}