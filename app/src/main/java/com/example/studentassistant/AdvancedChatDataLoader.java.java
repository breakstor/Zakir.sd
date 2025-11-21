package com.sudanese.studentassistant;

import org.json.JSONArray;
import org.json.JSONObject;
import java.io.InputStream;
import java.util.*;

public class AdvancedChatDataLoader {
    private List<ChatModel> chatData;
    private List<QuizModel> quizzes;
    private Map<String, List<Formula>> formulasLibrary;
    private Map<String, SubjectInfo> subjects;
    
    public AdvancedChatDataLoader(InputStream inputStream) {
        chatData = new ArrayList<>();
        quizzes = new ArrayList<>();
        formulasLibrary = new HashMap<>();
        subjects = new HashMap<>();
        loadData(inputStream);
    }
    
    private void loadData(InputStream inputStream) {
        try {
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();
            String jsonString = new String(buffer, "UTF-8");
            
            JSONObject jsonObject = new JSONObject(jsonString);
            
            // تحليل المواد
            JSONObject subjectsObj = jsonObject.getJSONObject("subjects");
            Iterator<String> keys = subjectsObj.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                JSONObject subjectObj = subjectsObj.getJSONObject(key);
                SubjectInfo subject = new SubjectInfo(
                    subjectObj.getString("name"),
                    subjectObj.getString("color"),
                    subjectObj.getString("icon")
                );
                subjects.put(key, subject);
            }
            
            // تحليل الأسئلة والإجابات
            JSONArray qaArray = jsonObject.getJSONArray("qa_data");
            for (int i = 0; i < qaArray.length(); i++) {
                JSONObject item = qaArray.getJSONObject(i);
                
                List<String> keywords = new ArrayList<>();
                JSONArray keywordsArray = item.getJSONArray("keywords");
                for (int j = 0; j < keywordsArray.length(); j++) {
                    keywords.add(keywordsArray.getString(j));
                }
                
                List<String> formulas = new ArrayList<>();
                if (item.has("formulas")) {
                    JSONArray formulasArray = item.getJSONArray("formulas");
                    for (int j = 0; j < formulasArray.length(); j++) {
                        formulas.add(formulasArray.getString(j));
                    }
                }
                
                ChatModel chatModel = new ChatModel();
                chatModel.setId(item.getInt("id"));
                chatModel.setQuestion(item.getString("question"));
                chatModel.setAnswer(item.getString("answer"));
                chatModel.setDetailedAnswer(item.getString("detailed_answer"));
                chatModel.setSubject(item.getString("subject"));
                chatModel.setKeywords(keywords);
                chatModel.setFormulas(formulas);
                chatModel.setImageUrl(item.optString("image_url", ""));
                chatModel.setDifficulty(item.optString("difficulty", "medium"));
                chatModel.setChapter(item.optString("chapter", ""));
                chatModel.setRating(0.0);
                chatModel.setRatingCount(0);
                
                chatData.add(chatModel);
            }
            
            // تحليل الاختبارات
            if (jsonObject.has("interactive_quizzes")) {
                JSONArray quizzesArray = jsonObject.getJSONArray("interactive_quizzes");
                for (int i = 0; i < quizzesArray.length(); i++) {
                    JSONObject quizObj = quizzesArray.getJSONObject(i);
                    QuizModel quiz = new QuizModel();
                    quiz.setId(quizObj.getInt("id"));
                    quiz.setTitle(quizObj.getString("title"));
                    quiz.setSubject(quizObj.getString("subject"));
                    quiz.setTimeLimit(quizObj.getInt("time_limit"));
                    quiz.setPassingScore(quizObj.getInt("passing_score"));
                    
                    List<QuizQuestion> questions = new ArrayList<>();
                    JSONArray questionsArray = quizObj.getJSONArray("questions");
                    for (int j = 0; j < questionsArray.length(); j++) {
                        JSONObject qObj = questionsArray.getJSONObject(j);
                        QuizQuestion question = new QuizQuestion();
                        question.setQuestion(qObj.getString("question"));
                        
                        List<String> options = new ArrayList<>();
                        JSONArray optionsArray = qObj.getJSONArray("options");
                        for (int k = 0; k < optionsArray.length(); k++) {
                            options.add(optionsArray.getString(k));
                        }
                        question.setOptions(options);
                        question.setCorrectAnswer(qObj.getInt("correct_answer"));
                        question.setExplanation(qObj.getString("explanation"));
                        
                        questions.add(question);
                    }
                    quiz.setQuestions(questions);
                    quizzes.add(quiz);
                }
            }
            
            // تحليل مكتبة الصيغ
            if (jsonObject.has("formulas_library")) {
                JSONObject formulasObj = jsonObject.getJSONObject("formulas_library");
                Iterator<String> subjectKeys = formulasObj.keys();
                while (subjectKeys.hasNext()) {
                    String subjectKey = subjectKeys.next();
                    JSONArray formulasArray = formulasObj.getJSONArray(subjectKey);
                    List<Formula> formulaList = new ArrayList<>();
                    
                    for (int i = 0; i < formulasArray.length(); i++) {
                        JSONObject formulaObj = formulasArray.getJSONObject(i);
                        Formula formula = new Formula(
                            formulaObj.getString("name"),
                            formulaObj.getString("formula"),
                            formulaObj.getString("description")
                        );
                        formulaList.add(formula);
                    }
                    formulasLibrary.put(subjectKey, formulaList);
                }
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public SearchResult findAnswer(String userQuestion, String selectedSubject) {
        userQuestion = userQuestion.toLowerCase().trim();
        SearchResult result = new SearchResult();
        
        for (ChatModel item : chatData) {
            if (selectedSubject != null && !selectedSubject.equals(item.getSubject())) {
                continue;
            }
            
            boolean matchFound = false;
            
            if (item.getQuestion().toLowerCase().contains(userQuestion) || 
                userQuestion.contains(item.getQuestion().toLowerCase())) {
                matchFound = true;
            }
            
            if (!matchFound) {
                for (String keyword : item.getKeywords()) {
                    if (userQuestion.contains(keyword.toLowerCase())) {
                        matchFound = true;
                        break;
                    }
                }
            }
            
            if (matchFound) {
                result.setChatModel(item);
                result.setMatchType("exact");
                return result;
            }
        }
        
        if (selectedSubject != null) {
            for (ChatModel item : chatData) {
                for (String keyword : item.getKeywords()) {
                    if (userQuestion.contains(keyword.toLowerCase())) {
                        result.setChatModel(item);
                        result.setMatchType("keyword");
                        return result;
                    }
                }
            }
        }
        
        result.setMatchType("not_found");
        return result;
    }
    
    public List<ChatModel> searchFormulas(String formulaText) {
        List<ChatModel> results = new ArrayList<>();
        for (ChatModel item : chatData) {
            if (item.getFormulas() != null) {
                for (String formula : item.getFormulas()) {
                    if (formula.toLowerCase().contains(formulaText.toLowerCase())) {
                        results.add(item);
                        break;
                    }
                }
            }
        }
        return results;
    }
    
    public void rateAnswer(int answerId, double rating) {
        for (ChatModel item : chatData) {
            if (item.getId() == answerId) {
                double currentTotal = item.getRating() * item.getRatingCount();
                item.setRatingCount(item.getRatingCount() + 1);
                item.setRating((currentTotal + rating) / item.getRatingCount());
                break;
            }
        }
    }
    
    public List<ChatModel> getChatData() { return chatData; }
    public List<QuizModel> getQuizzes() { return quizzes; }
    public Map<String, List<Formula>> getFormulasLibrary() { return formulasLibrary; }
    public Map<String, SubjectInfo> getSubjects() { return subjects; }
}

class SubjectInfo {
    private String name;
    private String color;
    private String icon;
    
    public SubjectInfo(String name, String color, String icon) {
        this.name = name;
        this.color = color;
        this.icon = icon;
    }
    
    public String getName() { return name; }
    public String getColor() { return color; }
    public String getIcon() { return icon; }
}

class Formula {
    private String name;
    private String formula;
    private String description;
    
    public Formula(String name, String formula, String description) {
        this.name = name;
        this.formula = formula;
        this.description = description;
    }
    
    public String getName() { return name; }
    public String getFormula() { return formula; }
    public String getDescription() { return description; }
}

class SearchResult {
    private ChatModel chatModel;
    private String matchType;
    
    public ChatModel getChatModel() { return chatModel; }
    public void setChatModel(ChatModel chatModel) { this.chatModel = chatModel; }
    
    public String getMatchType() { return matchType; }
    public void setMatchType(String matchType) { this.matchType = matchType; }
    
    public boolean isFound() { return chatModel != null; }
}