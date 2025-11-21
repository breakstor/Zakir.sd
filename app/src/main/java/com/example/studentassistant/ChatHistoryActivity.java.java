package com.sudanese.studentassistant;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.card.MaterialCardView;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ChatHistoryActivity extends AppCompatActivity {
    private RecyclerView sessionsRecyclerView;
    private ChatSessionAdapter sessionAdapter;
    private List<ChatSession> sessionsList;
    private ChatHistoryDBHelper dbHelper;
    private TextView emptyStateText;
    private Button backButton, clearAllButton;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_history);
        
        initializeViews();
        setupDatabase();
        loadSessions();
    }
    
    private void initializeViews() {
        sessionsRecyclerView = findViewById(R.id.sessionsRecyclerView);
        emptyStateText = findViewById(R.id.emptyStateText);
        backButton = findViewById(R.id.backButton);
        clearAllButton = findViewById(R.id.clearAllButton);
        
        sessionsList = new ArrayList<>();
        sessionAdapter = new ChatSessionAdapter(sessionsList, new ChatSessionAdapter.OnSessionClickListener() {
            @Override
            public void onSessionClick(ChatSession session) {
                openSessionDetails(session);
            }
            
            @Override
            public void onSessionLongClick(ChatSession session) {
                showSessionOptions(session);
            }
            
            @Override
            public void onDeleteSession(ChatSession session) {
                deleteSession(session);
            }
        });
        
        sessionsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        sessionsRecyclerView.setAdapter(sessionAdapter);
        
        backButton.setOnClickListener(v -> finish());
        clearAllButton.setOnClickListener(v -> showClearAllConfirmation());
    }
    
    private void setupDatabase() {
        dbHelper = new ChatHistoryDBHelper(this);
    }
    
    private void loadSessions() {
        sessionsList.clear();
        sessionsList.addAll(dbHelper.getAllSessions());
        sessionAdapter.notifyDataSetChanged();
        
        if (sessionsList.isEmpty()) {
            emptyStateText.setVisibility(View.VISIBLE);
            sessionsRecyclerView.setVisibility(View.GONE);
        } else {
            emptyStateText.setVisibility(View.GONE);
            sessionsRecyclerView.setVisibility(View.VISIBLE);
        }
    }
    
    private void openSessionDetails(ChatSession session) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(session.getSessionTitle());
        
        StringBuilder messagesText = new StringBuilder();
        for (ChatMessage message : session.getMessages()) {
            String prefix = message.isUserMessage() ? "👤: " : "🤖: ";
            messagesText.append(prefix).append(message.getMessage()).append("\n\n");
        }
        
        TextView messageView = new TextView(this);
        messageView.setText(messagesText.toString());
        messageView.setPadding(50, 30, 50, 30);
        messageView.setTextSize(14);
        messageView.setTextIsSelectable(true);
        
        ScrollView scrollView = new ScrollView(this);
        scrollView.addView(messageView);
        
        builder.setView(scrollView);
        builder.setPositiveButton("حسناً", null);
        builder.setNegativeButton("حذف المحادثة", (dialog, which) -> deleteSession(session));
        builder.show();
    }
    
    private void showSessionOptions(ChatSession session) {
        String[] options = {"عرض المحادثة", "تعديل العنوان", "حذف المحادثة"};
        
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("خيارات المحادثة");
        builder.setItems(options, (dialog, which) -> {
            switch (which) {
                case 0:
                    openSessionDetails(session);
                    break;
                case 1:
                    editSessionTitle(session);
                    break;
                case 2:
                    deleteSession(session);
                    break;
            }
        });
        builder.show();
    }
    
    private void editSessionTitle(ChatSession session) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("تعديل عنوان المحادثة");
        
        final EditText input = new EditText(this);
        input.setText(session.getSessionTitle());
        input.setSelectAllOnFocus(true);
        builder.setView(input);
        
        builder.setPositiveButton("حفظ", (dialog, which) -> {
            String newTitle = input.getText().toString().trim();
            if (!newTitle.isEmpty()) {
                dbHelper.updateSessionTitle(session.getSessionId(), newTitle);
                loadSessions();
                Toast.makeText(this, "تم تحديث العنوان", Toast.LENGTH_SHORT).show();
            }
        });
        
        builder.setNegativeButton("إلغاء", null);
        builder.show();
    }
    
    private void deleteSession(ChatSession session) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("تأكيد الحذف");
        builder.setMessage("هل أنت متأكد من أنك تريد حذف هذه المحادثة؟");
        
        builder.setPositiveButton("حذف", (dialog, which) -> {
            dbHelper.deleteSession(session.getSessionId());
            loadSessions();
            Toast.makeText(this, "تم حذف المحادثة", Toast.LENGTH_SHORT).show();
        });
        
        builder.setNegativeButton("إلغاء", null);
        builder.show();
    }
    
    private void showClearAllConfirmation() {
        if (sessionsList.isEmpty()) {
            Toast.makeText(this, "لا توجد محادثات لحذفها", Toast.LENGTH_SHORT).show();
            return;
        }
        
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("حذف كل المحادثات");
        builder.setMessage("هل أنت متأكد من أنك تريد حذف جميع المحادثات؟");
        
        builder.setPositiveButton("حذف الكل", (dialog, which) -> {
            dbHelper.clearAllData();
            loadSessions();
            Toast.makeText(this, "تم حذف جميع المحادثات", Toast.LENGTH_SHORT).show();
        });
        
        builder.setNegativeButton("إلغاء", null);
        builder.show();
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        loadSessions();
    }
}