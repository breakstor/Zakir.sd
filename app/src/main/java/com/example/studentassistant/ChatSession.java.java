package com.sudanese.studentassistant;

import java.util.Date;
import java.util.ArrayList;
import java.util.List;

public class ChatSession {
    private int sessionId;
    private String sessionTitle;
    private Date createdAt;
    private Date updatedAt;
    private List<ChatMessage> messages;
    private String subject;
    
    public ChatSession() {
        this.messages = new ArrayList<>();
        this.createdAt = new Date();
        this.updatedAt = new Date();
    }
    
    public ChatSession(int sessionId, String sessionTitle) {
        this();
        this.sessionId = sessionId;
        this.sessionTitle = sessionTitle;
    }
    
    public int getSessionId() { return sessionId; }
    public void setSessionId(int sessionId) { this.sessionId = sessionId; }
    
    public String getSessionTitle() { return sessionTitle; }
    public void setSessionTitle(String sessionTitle) { this.sessionTitle = sessionTitle; }
    
    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
    
    public Date getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Date updatedAt) { this.updatedAt = updatedAt; }
    
    public List<ChatMessage> getMessages() { return messages; }
    public void setMessages(List<ChatMessage> messages) { this.messages = messages; }
    
    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }
    
    public void addMessage(ChatMessage message) {
        this.messages.add(message);
        this.updatedAt = new Date();
    }
    
    public String getPreview() {
        if (messages != null && !messages.isEmpty()) {
            String lastMessage = messages.get(messages.size() - 1).getMessage();
            return lastMessage.length() > 30 ? lastMessage.substring(0, 30) + "..." : lastMessage;
        }
        return "لا توجد رسائل";
    }
    
    public int getMessageCount() {
        return messages != null ? messages.size() : 0;
    }
}