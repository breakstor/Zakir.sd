package com.sudanese.studentassistant;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.card.MaterialCardView;
import java.io.InputStream;
import java.util.*;

public class MainActivity extends AppCompatActivity {
    private AdvancedChatDataLoader chatDataLoader;
    private ActivationManager activationManager;
    private EditText questionInput;
    private Button sendButton, searchFormulaButton;
    private TextView answerText;
    private Spinner subjectSpinner;
    private RatingBar answerRating;
    private RecyclerView formulasRecyclerView;
    private String currentSubject = null;
    
    // عناصر القائمة الرئيسية
    private MaterialCardView cardStudySchedule, cardResources, cardChatHistory, cardQuizzes;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // التحقق من التفعيل قبل المتابعة
        activationManager = new ActivationManager(this);
        if (!activationManager.isAppActivated()) {
            startActivity(new Intent(this, ActivationActivity.class));
            finish();
            return;
        }
        
        setContentView(R.layout.activity_main);
        
        initializeDataLoader();
        initializeViews();
        setupSubjectSpinner();
    }
    
    private void initializeDataLoader() {
        try {
            InputStream inputStream = getAssets().open("enhanced_chat_data.json");
            chatDataLoader = new AdvancedChatDataLoader(inputStream);
            Toast.makeText(this, "تم تحميل البيانات بنجاح", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "خطأ في تحميل البيانات", Toast.LENGTH_SHORT).show();
        }
    }
    
    private void initializeViews() {
        questionInput = findViewById(R.id.questionInput);
        sendButton = findViewById(R.id.sendButton);
        searchFormulaButton = findViewById(R.id.searchFormulaButton);
        answerText = findViewById(R.id.answerText);
        subjectSpinner = findViewById(R.id.subjectSpinner);
        answerRating = findViewById(R.id.answerRating);
        formulasRecyclerView = findViewById(R.id.formulasRecyclerView);
        
        // عناصر القائمة الرئيسية
        cardStudySchedule = findViewById(R.id.cardStudySchedule);
        cardResources = findViewById(R.id.cardResources);
        cardChatHistory = findViewById(R.id.cardChatHistory);
        cardQuizzes = findViewById(R.id.cardQuizzes);
        
        sendButton.setOnClickListener(v -> sendMessage());
        searchFormulaButton.setOnClickListener(v -> searchFormulas());
        
        // إعداد مستمعين للبطاقات
        cardStudySchedule.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, StudyScheduleActivity.class));
        });
        
        cardResources.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, ResourcesActivity.class));
        });
        
        cardChatHistory.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, ChatHistoryActivity.class));
        });
        
        cardQuizzes.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, QuizActivity.class));
        });
        
        formulasRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
    
    private void setupSubjectSpinner() {
        Map<String, SubjectInfo> subjects = chatDataLoader.getSubjects();
        List<String> subjectNames = new ArrayList<>();
        subjectNames.add("جميع المواد");
        
        for (SubjectInfo subject : subjects.values()) {
            subjectNames.add(subject.getName());
        }
        
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, 
            android.R.layout.simple_spinner_item, subjectNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        subjectSpinner.setAdapter(adapter);
        
        subjectSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    currentSubject = null;
                } else {
                    String selectedSubjectName = subjectNames.get(position);
                    currentSubject = findSubjectKeyByName(selectedSubjectName);
                }
            }
            
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                currentSubject = null;
            }
        });
    }
    
    private String findSubjectKeyByName(String name) {
        for (Map.Entry<String, SubjectInfo> entry : chatDataLoader.getSubjects().entrySet()) {
            if (entry.getValue().getName().equals(name)) {
                return entry.getKey();
            }
        }
        return null;
    }
    
    private void sendMessage() {
        String question = questionInput.getText().toString();
        if (!question.trim().isEmpty()) {
            SearchResult result = chatDataLoader.findAnswer(question, currentSubject);
            
            if (result.isFound()) {
                displayAnswer(result.getChatModel());
            } else {
                answerText.setText("عذراً، لم أتمكن من العثور على إجابة لسؤالك.\n\nاقتراحات:\n- تأكد من صياغة السؤال بشكل واضح\n- استخدم مصطلحات دراسية محددة\n- حاول إعادة صياغة السؤال");
                answerRating.setVisibility(View.GONE);
            }
        } else {
            Toast.makeText(this, "يرجى كتابة سؤال أولاً", Toast.LENGTH_SHORT).show();
        }
    }
    
    private void displayAnswer(ChatModel answer) {
        StringBuilder answerBuilder = new StringBuilder();
        
        answerBuilder.append("📚 ").append(answer.getQuestion()).append("\n\n");
        answerBuilder.append("💡 الإجابة:\n").append(answer.getDetailedAnswer()).append("\n\n");
        
        if (answer.getFormulas() != null && !answer.getFormulas().isEmpty()) {
            answerBuilder.append("📐 الصيغ الرياضية:\n");
            for (String formula : answer.getFormulas()) {
                answerBuilder.append("• ").append(formula).append("\n");
            }
            answerBuilder.append("\n");
        }
        
        SubjectInfo subjectInfo = chatDataLoader.getSubjects().get(answer.getSubject());
        String subjectName = (subjectInfo != null) ? subjectInfo.getName() : answer.getSubject();
        
        answerBuilder.append("📖 المادة: ").append(subjectName).append("\n");
        answerBuilder.append("🏷️ الفصل: ").append(answer.getChapter()).append("\n");
        answerBuilder.append("⭐ الصعوبة: ").append(getDifficultyText(answer.getDifficulty()));
        
        answerText.setText(answerBuilder.toString());
        answerRating.setVisibility(View.VISIBLE);
    }
    
    private String getDifficultyText(String difficulty) {
        switch (difficulty) {
            case "easy": return "سهلة";
            case "medium": return "متوسطة";
            case "hard": return "صعبة";
            default: return "متوسطة";
        }
    }
    
    private void searchFormulas() {
        String formulaText = questionInput.getText().toString();
        if (!formulaText.trim().isEmpty()) {
            List<ChatModel> results = chatDataLoader.searchFormulas(formulaText);
            if (!results.isEmpty()) {
                displayFormulas(results);
            } else {
                Toast.makeText(this, "لم يتم العثور على صيغ مطابقة", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "يرجى كتابة نص للبحث عن الصيغ", Toast.LENGTH_SHORT).show();
        }
    }
    
    private void displayFormulas(List<ChatModel> formulas) {
        FormulaAdapter adapter = new FormulaAdapter(formulas);
        formulasRecyclerView.setAdapter(adapter);
    }
}