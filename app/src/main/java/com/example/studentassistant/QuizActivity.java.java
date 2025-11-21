package com.sudanese.studentassistant;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.*;

public class QuizActivity extends AppCompatActivity {
    private RecyclerView quizzesRecyclerView;
    private QuizAdapter quizAdapter;
    private List<QuizModel> quizzesList;
    private ProgressBar loadingProgress;
    private TextView emptyStateText;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);
        
        initializeViews();
        loadQuizzes();
    }
    
    private void initializeViews() {
        quizzesRecyclerView = findViewById(R.id.quizzesRecyclerView);
        loadingProgress = findViewById(R.id.loadingProgress);
        emptyStateText = findViewById(R.id.emptyStateText);
        
        quizzesList = new ArrayList<>();
        quizAdapter = new QuizAdapter(quizzesList, new QuizAdapter.OnQuizClickListener() {
            @Override
            public void onQuizClick(QuizModel quiz) {
                startQuiz(quiz);
            }
        });
        
        quizzesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        quizzesRecyclerView.setAdapter(quizAdapter);
    }
    
    private void loadQuizzes() {
        loadingProgress.setVisibility(View.VISIBLE);
        
        new android.os.Handler().postDelayed(() -> {
            quizzesList.clear();
            quizzesList.addAll(getSampleQuizzes());
            quizAdapter.notifyDataSetChanged();
            loadingProgress.setVisibility(View.GONE);
            
            if (quizzesList.isEmpty()) {
                emptyStateText.setVisibility(View.VISIBLE);
                quizzesRecyclerView.setVisibility(View.GONE);
            } else {
                emptyStateText.setVisibility(View.GONE);
                quizzesRecyclerView.setVisibility(View.VISIBLE);
            }
        }, 1000);
    }
    
    private List<QuizModel> getSampleQuizzes() {
        List<QuizModel> sampleQuizzes = new ArrayList<>();
        
        // اختبار الرياضيات
        QuizModel mathQuiz = new QuizModel();
        mathQuiz.setId(1);
        mathQuiz.setTitle("اختبار الرياضيات - الجبر");
        mathQuiz.setSubject("الرياضيات");
        mathQuiz.setTimeLimit(1800); // 30 دقيقة
        mathQuiz.setPassingScore(70);
        
        List<QuizQuestion> mathQuestions = new ArrayList<>();
        
        QuizQuestion q1 = new QuizQuestion();
        q1.setQuestion("ما حل المعادلة: ٢س + ٥ = ١٥؟");
        q1.setOptions(Arrays.asList("س = ٥", "س = ١٠", "س = ٧.٥", "س = ٢٠"));
        q1.setCorrectAnswer(0);
        q1.setExplanation("٢س = ١٥ - ٥ = ١٠، إذن س = ١٠ ÷ ٢ = ٥");
        mathQuestions.add(q1);
        
        QuizQuestion q2 = new QuizQuestion();
        q2.setQuestion("ما هي قيمة س في المعادلة: س² - ٩ = ٠؟");
        q2.setOptions(Arrays.asList("س = ٣", "س = -٣", "س = ٣ أو -٣", "س = ٩"));
        q2.setCorrectAnswer(2);
        q2.setExplanation("س² = ٩، إذن س = ±٣");
        mathQuestions.add(q2);
        
        mathQuiz.setQuestions(mathQuestions);
        sampleQuizzes.add(mathQuiz);
        
        // اختبار الفيزياء
        QuizModel physicsQuiz = new QuizModel();
        physicsQuiz.setId(2);
        physicsQuiz.setTitle("اختبار الفيزياء - القوانين الأساسية");
        physicsQuiz.setSubject("الفيزياء");
        physicsQuiz.setTimeLimit(1200); // 20 دقيقة
        physicsQuiz.setPassingScore(60);
        
        List<QuizQuestion> physicsQuestions = new ArrayList<>();
        
        QuizQuestion pq1 = new QuizQuestion();
        pq1.setQuestion("ما هو قانون نيوتن الثاني؟");
        pq1.setOptions(Arrays.asList(
            "القوة = الكتلة × التسارع",
            "لكل فعل رد فعل مساوٍ في المقدار",
            "الجسم الساكن يبقى ساكناً",
            "الطاقة لا تفنى ولا تستحدث"
        ));
        pq1.setCorrectAnswer(0);
        pq1.setExplanation("قانون نيوتن الثاني: القوة = الكتلة × التسارع (ق = ك × ت)");
        physicsQuestions.add(pq1);
        
        physicsQuiz.setQuestions(physicsQuestions);
        sampleQuizzes.add(physicsQuiz);
        
        return sampleQuizzes;
    }
    
    private void startQuiz(QuizModel quiz) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("بدء الاختبار");
        
        String quizInfo = "📝 " + quiz.getTitle() + "\n\n" +
                         "📚 المادة: " + quiz.getSubject() + "\n" +
                         "❓ عدد الأسئلة: " + quiz.getTotalQuestions() + "\n" +
                         "⏱️ الوقت: " + (quiz.getTimeLimit() / 60) + " دقيقة\n" +
                         "🎯 درجة النجاح: " + quiz.getPassingScore() + "%\n\n" +
                         "هل أنت مستعد لبدء الاختبار؟";
        
        builder.setMessage(quizInfo);
        builder.setPositiveButton("بدء الاختبار", (dialog, which) -> {
            Intent intent = new Intent(QuizActivity.this, QuizSessionActivity.class);
            intent.putExtra("quiz_id", quiz.getId());
            startActivity(intent);
        });
        builder.setNegativeButton("إلغاء", null);
        builder.show();
    }
}