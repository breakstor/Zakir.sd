package com.sudanese.studentassistant;

import java.util.Date;

public class ChatMessage {
    public static final int TYPE_USER = 0;
    public static final int TYPE_BOT = 1;
    
    private int messageId;
    private int sessionId;
    private String message;
    private int messageType;
    private Date timestamp;
    private String subject;
    private boolean isSaved;
    
    public ChatMessage() {
        this.timestamp = new Date();
        this.isSaved = false;
    }
    
    public ChatMessage(String message, int messageType) {
        this();
        this.message = message;
        this.messageType = messageType;
    }
    
    public ChatMessage(String message, int messageType, String subject) {
        this(message, messageType);
        this.subject = subject;
    }
    
    public int getMessageId() { return messageId; }
    public void setMessageId(int messageId) { this.messageId = messageId; }
    
    public int getSessionId() { return sessionId; }
    public void setSessionId(int sessionId) { this.sessionId = sessionId; }
    
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    
    public int getMessageType() { return messageType; }
    public void setMessageType(int messageType) { this.messageType = messageType; }
    
    public Date getTimestamp() { return timestamp; }
    public void setTimestamp(Date timestamp) { this.timestamp = timestamp; }
    
    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }
    
    public boolean isSaved() { return isSaved; }
    public void setSaved(boolean saved) { isSaved = saved; }
    
    public boolean isUserMessage() {
        return messageType == TYPE_USER;
    }
    
    public boolean isBotMessage() {
        return messageType == TYPE_BOT;
    }
    
    public String getFormattedTime() {
        return android.text.format.DateFormat.format("hh:mm a", timestamp).toString();
    }
}