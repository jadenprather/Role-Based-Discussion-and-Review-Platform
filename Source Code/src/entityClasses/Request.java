package entityClasses;


import java.time.LocalDateTime;

public class Request {
    private int id;
    private String title;
    private String description;
    private String createdBy;
    private String status;          // "OPEN" or "CLOSED"
    private String adminNotes;      // optional free-form notes
    private Integer parentId;       // null unless this was reopened; points to original
    private LocalDateTime createdAt;

    public Request(int id, String title, String description, String createdBy,
                   String status, String adminNotes, Integer parentId, LocalDateTime createdAt) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.createdBy = createdBy;
        this.status = status;
        this.adminNotes = adminNotes;
        this.parentId = parentId;
        this.createdAt = createdAt;
    }

    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getCreatedBy() { return createdBy; }
    public String getStatus() { return status; }
    public String getAdminNotes() { return adminNotes; }
    public Integer getParentId() { return parentId; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    @Override
    public String toString() {
        // nice for ListView — shows id and title
        return "#" + id + " • " + title + " (" + status + ")";
    }
}
