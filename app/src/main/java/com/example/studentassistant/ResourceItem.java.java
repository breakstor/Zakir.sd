package com.sudanese.studentassistant;

public class ResourceItem {
    private int id;
    private String title;
    private String description;
    private String type; // "book" أو "exam"
    private String year;
    private String subject;
    private String downloadUrl;
    private String fileSize;
    private int pages;
    private boolean isDownloaded;
    private String localPath;
    
    public ResourceItem() {}
    
    public ResourceItem(String title, String description, String type, String year, String subject, String downloadUrl) {
        this.title = title;
        this.description = description;
        this.type = type;
        this.year = year;
        this.subject = subject;
        this.downloadUrl = downloadUrl;
        this.isDownloaded = false;
    }
    
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    
    public String getYear() { return year; }
    public void setYear(String year) { this.year = year; }
    
    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }
    
    public String getDownloadUrl() { return downloadUrl; }
    public void setDownloadUrl(String downloadUrl) { this.downloadUrl = downloadUrl; }
    
    public String getFileSize() { return fileSize; }
    public void setFileSize(String fileSize) { this.fileSize = fileSize; }
    
    public int getPages() { return pages; }
    public void setPages(int pages) { this.pages = pages; }
    
    public boolean isDownloaded() { return isDownloaded; }
    public void setDownloaded(boolean downloaded) { isDownloaded = downloaded; }
    
    public String getLocalPath() { return localPath; }
    public void setLocalPath(String localPath) { this.localPath = localPath; }
    
    public String getTypeIcon() {
        return type.equals("book") ? "📚" : "📝";
    }
    
    public String getTypeText() {
        return type.equals("book") ? "كتاب" : "امتحان";
    }
}