package com.sudanese.studentassistant;

import java.util.List;

public class ChatModel {
    private int id;
    private String question;
    private String answer;
    private String detailedAnswer;
    private String subject;
    private List<String> keywords;
    private List<String> formulas;
    private String imageUrl;
    private String difficulty;
    private String chapter;
    private double rating;
    private int ratingCount;
    
    public ChatModel() {}
    
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getQuestion() { return question; }
    public void setQuestion(String question) { this.question = question; }
    
    public String getAnswer() { return answer; }
    public void setAnswer(String answer) { this.answer = answer; }
    
    public String getDetailedAnswer() { return detailedAnswer; }
    public void setDetailedAnswer(String detailedAnswer) { this.detailedAnswer = detailedAnswer; }
    
    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }
    
    public List<String> getKeywords() { return keywords; }
    public void setKeywords(List<String> keywords) { this.keywords = keywords; }
    
    public List<String> getFormulas() { return formulas; }
    public void setFormulas(List<String> formulas) { this.formulas = formulas; }
    
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    
    public String getDifficulty() { return difficulty; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }
    
    public String getChapter() { return chapter; }
    public void setChapter(String chapter) { this.chapter = chapter; }
    
    public double getRating() { return rating; }
    public void setRating(double rating) { this.rating = rating; }
    
    public int getRatingCount() { return ratingCount; }
    public void setRatingCount(int ratingCount) { this.ratingCount = ratingCount; }
}