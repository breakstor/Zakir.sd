package com.sudanese.studentassistant;

import java.util.List;

public class QuizModel {
    private int id;
    private String title;
    private String subject;
    private List<QuizQuestion> questions;
    private int timeLimit;
    private int passingScore;
    
    public QuizModel() {}
    
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }
    
    public List<QuizQuestion> getQuestions() { return questions; }
    public void setQuestions(List<QuizQuestion> questions) { this.questions = questions; }
    
    public int getTimeLimit() { return timeLimit; }
    public void setTimeLimit(int timeLimit) { this.timeLimit = timeLimit; }
    
    public int getPassingScore() { return passingScore; }
    public void setPassingScore(int passingScore) { this.passingScore = passingScore; }
    
    public int getTotalQuestions() {
        return questions != null ? questions.size() : 0;
    }
    
    public int getTotalMarks() {
        return getTotalQuestions() * 10; // 10 درجات لكل سؤال
    }
}

class QuizQuestion {
    private String question;
    private List<String> options;
    private int correctAnswer;
    private String explanation;
    
    public QuizQuestion() {}
    
    public String getQuestion() { return question; }
    public void setQuestion(String question) { this.question = question; }
    
    public List<String> getOptions() { return options; }
    public void setOptions(List<String> options) { this.options = options; }
    
    public int getCorrectAnswer() { return correctAnswer; }
    public void setCorrectAnswer(int correctAnswer) { this.correctAnswer = correctAnswer; }
    
    public String getExplanation() { return explanation; }
    public void setExplanation(String explanation) { this.explanation = explanation; }
}