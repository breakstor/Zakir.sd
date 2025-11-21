package com.sudanese.studentassistant;

import java.util.Date;

public class StudySchedule {
    private int id;
    private String subject;
    private String topic;
    private Date studyDate;
    private Date studyTime;
    private int duration;
    private boolean isCompleted;
    private boolean hasReminder;
    private int reminderMinutes;
    private String notes;
    
    public StudySchedule() {}
    
    public StudySchedule(String subject, String topic, Date studyDate, Date studyTime, int duration) {
        this.subject = subject;
        this.topic = topic;
        this.studyDate = studyDate;
        this.studyTime = studyTime;
        this.duration = duration;
        this.isCompleted = false;
        this.hasReminder = true;
        this.reminderMinutes = 15;
    }
    
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }
    
    public String getTopic() { return topic; }
    public void setTopic(String topic) { this.topic = topic; }
    
    public Date getStudyDate() { return studyDate; }
    public void setStudyDate(Date studyDate) { this.studyDate = studyDate; }
    
    public Date getStudyTime() { return studyTime; }
    public void setStudyTime(Date studyTime) { this.studyTime = studyTime; }
    
    public int getDuration() { return duration; }
    public void setDuration(int duration) { this.duration = duration; }
    
    public boolean isCompleted() { return isCompleted; }
    public void setCompleted(boolean completed) { isCompleted = completed; }
    
    public boolean isHasReminder() { return hasReminder; }
    public void setHasReminder(boolean hasReminder) { this.hasReminder = hasReminder; }
    
    public int getReminderMinutes() { return reminderMinutes; }
    public void setReminderMinutes(int reminderMinutes) { this.reminderMinutes = reminderMinutes; }
    
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    
    public String getFormattedDateTime() {
        return android.text.format.DateFormat.format("dd/MM/yyyy - hh:mm a", studyTime).toString();
    }
    
    public String getStatusIcon() {
        return isCompleted ? "✅" : "⏰";
    }
    
    public String getStatusText() {
        return isCompleted ? "مكتمل" : "قيد الانتظار";
    }
}