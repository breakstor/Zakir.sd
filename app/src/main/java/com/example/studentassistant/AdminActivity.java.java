package com.sudanese.studentassistant;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.tabs.TabLayout;
import java.util.*;

public class AdminActivity extends AppCompatActivity {
    private TabLayout adminTabs;
    private FrameLayout tabContent;
    private EditText adminPasswordInput;
    private Button loginButton;
    private View loginView, adminView;
    
    private static final String ADMIN_PASSWORD = "admin123";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        
        initializeViews();
        setupTabs();
    }
    
    private void initializeViews() {
        adminTabs = findViewById(R.id.adminTabs);
        tabContent = findViewById(R.id.tabContent);
        adminPasswordInput = findViewById(R.id.adminPasswordInput);
        loginButton = findViewById(R.id.loginButton);
        loginView = findViewById(R.id.loginView);
        adminView = findViewById(R.id.adminView);
        
        loginButton.setOnClickListener(v -> attemptLogin());
        
        adminView.setVisibility(View.GONE);
    }
    
    private void attemptLogin() {
        String password = adminPasswordInput.getText().toString();
        
        if (password.equals(ADMIN_PASSWORD)) {
            loginView.setVisibility(View.GONE);
            adminView.setVisibility(View.VISIBLE);
            Toast.makeText(this, "مرحباً بك في لوحة التحكم", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "كلمة المرور غير صحيحة", Toast.LENGTH_SHORT).show();
        }
    }
    
    private void setupTabs() {
        adminTabs.addTab(adminTabs.newTab().setText("إدارة الكتب"));
        adminTabs.addTab(adminTabs.newTab().setText("إدارة الامتحانات"));
        adminTabs.addTab(adminTabs.newTab().setText("إدارة التفعيل"));
        adminTabs.addTab(adminTabs.newTab().setText("الإعدادات"));
        
        adminTabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        showBooksManagement();
                        break;
                    case 1:
                        showExamsManagement();
                        break;
                    case 2:
                        showActivationManagement();
                        break;
                    case 3:
                        showSettingsManagement();
                        break;
                }
            }
            
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}
            
            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }
    
    private void showBooksManagement() {
        View booksView = getLayoutInflater().inflate(R.layout.tab_books_management, null);
        tabContent.removeAllViews();
        tabContent.addView(booksView);
        
        Button addBookButton = booksView.findViewById(R.id.addBookButton);
        ListView booksListView = booksView.findViewById(R.id.booksListView);
        
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, 
            android.R.layout.simple_list_item_1, getCurrentBooks());
        booksListView.setAdapter(adapter);
        
        addBookButton.setOnClickListener(v -> showAddBookDialog());
    }
    
    private void showExamsManagement() {
        View examsView = getLayoutInflater().inflate(R.layout.tab_exams_management, null);
        tabContent.removeAllViews();
        tabContent.addView(examsView);
        
        Button addExamButton = examsView.findViewById(R.id.addExamButton);
        ListView examsListView = examsView.findViewById(R.id.examsListView);
        
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, 
            android.R.layout.simple_list_item_1, getCurrentExams());
        examsListView.setAdapter(adapter);
        
        addExamButton.setOnClickListener(v -> showAddExamDialog());
    }
    
    private void showActivationManagement() {
        View activationView = getLayoutInflater().inflate(R.layout.tab_activation_management, null);
        tabContent.removeAllViews();
        tabContent.addView(activationView);
        
        ActivationManager activationManager = new ActivationManager(this);
        
        TextView deviceIdText = activationView.findViewById(R.id.deviceIdText);
        TextView activationStatusText = activationView.findViewById(R.id.activationStatusText);
        EditText adminPhoneInput = activationView.findViewById(R.id.adminPhoneInput);
        Button savePhoneButton = activationView.findViewById(R.id.savePhoneButton);
        Button generateCodesButton = activationView.findViewById(R.id.generateCodesButton);
        EditText codesCountInput = activationView.findViewById(R.id.codesCountInput);
        TextView generatedCodesText = activationView.findViewById(R.id.generatedCodesText);
        ListView codesListView = activationView.findViewById(R.id.codesListView);
        
        Map<String, String> activationInfo = activationManager.getActivationInfo();
        deviceIdText.setText("معرف الجهاز: " + activationInfo.get("device_id"));
        activationStatusText.setText("حالة التفعيل: " + 
            (activationManager.isAppActivated() ? "✅ مفعل" : "❌ غير مفعل"));
        
        adminPhoneInput.setText(activationManager.getAdminPhoneNumber());
        
        savePhoneButton.setOnClickListener(v -> {
            String phone = adminPhoneInput.getText().toString();
            activationManager.setAdminPhoneNumber(phone);
            Toast.makeText(this, "تم حفظ رقم الهاتف", Toast.LENGTH_SHORT).show();
        });
        
        generateCodesButton.setOnClickListener(v -> {
            try {
                int count = Integer.parseInt(codesCountInput.getText().toString());
                if (count > 0 && count <= 100) {
                    List<String> codes = activationManager.generateActivationCodes(count);
                    updateCodesList(codesListView);
                    generatedCodesText.setText("تم إنشاء " + count + " رمز تفعيل");
                    
                    showGeneratedCodesDialog(codes);
                } else {
                    Toast.makeText(this, "يرجى إدخال عدد بين 1 و 100", Toast.LENGTH_SHORT).show();
                }
            } catch (NumberFormatException e) {
                Toast.makeText(this, "يرجى إدخال عدد صحيح", Toast.LENGTH_SHORT).show();
            }
        });
        
        updateCodesList(codesListView);
    }
    
    private void showSettingsManagement() {
        View settingsView = getLayoutInflater().inflate(R.layout.tab_settings_management, null);
        tabContent.removeAllViews();
        tabContent.addView(settingsView);
        
        EditText appNameInput = settingsView.findViewById(R.id.appNameInput);
        EditText welcomeMessageInput = settingsView.findViewById(R.id.welcomeMessageInput);
        Button saveSettingsButton = settingsView.findViewById(R.id.saveSettingsButton);
        
        appNameInput.setText("مساعد الشهادة السودانية");
        welcomeMessageInput.setText("مرحباً بك في تطبيق مساعد الشهادة السودانية");
        
        saveSettingsButton.setOnClickListener(v -> saveSettings(
            appNameInput.getText().toString(),
            welcomeMessageInput.getText().toString()
        ));
    }
    
    private String[] getCurrentBooks() {
        return new String[] {
            "كتاب الرياضيات 2024 - https://drive.google.com/...",
            "كتاب الفيزياء 2024 - https://drive.google.com/...",
            "كتاب الكيمياء 2024 - https://drive.google.com/..."
        };
    }
    
    private String[] getCurrentExams() {
        return new String[] {
            "امتحان الرياضيات 2023 - https://drive.google.com/...", 
            "امتحان الفيزياء 2023 - https://drive.google.com/...",
            "امتحان الكيمياء 2023 - https://drive.google.com/..."
        };
    }
    
    private void showAddBookDialog() {
        showAddResourceDialog("كتاب");
    }
    
    private void showAddExamDialog() {
        showAddResourceDialog("امتحان");
    }
    
    private void showAddResourceDialog(String type) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("إضافة " + type + " جديد");
        
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_resource, null);
        builder.setView(dialogView);
        
        EditText titleInput = dialogView.findViewById(R.id.titleInput);
        EditText descriptionInput = dialogView.findViewById(R.id.descriptionInput);
        Spinner subjectSpinner = dialogView.findViewById(R.id.subjectSpinner);
        EditText yearInput = dialogView.findViewById(R.id.yearInput);
        EditText urlInput = dialogView.findViewById(R.id.urlInput);
        
        setupSubjectSpinner(subjectSpinner);
        
        builder.setPositiveButton("إضافة", (dialog, which) -> {
            String title = titleInput.getText().toString();
            String description = descriptionInput.getText().toString();
            String subject = subjectSpinner.getSelectedItem().toString();
            String year = yearInput.getText().toString();
            String url = urlInput.getText().toString();
            
            Toast.makeText(this, "تم إضافة " + type + ": " + title, Toast.LENGTH_SHORT).show();
        });
        
        builder.setNegativeButton("إلغاء", null);
        builder.show();
    }
    
    private void setupSubjectSpinner(Spinner spinner) {
        String[] subjects = {
            "الرياضيات", "الفيزياء", "الكيمياء", "الأحياء",
            "اللغة العربية", "اللغة الإنجليزية", "التربية الإسلامية", 
            "التاريخ", "الجغرافيا"
        };
        
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
            android.R.layout.simple_spinner_item, subjects);
        spinner.setAdapter(adapter);
    }
    
    private void updateCodesList(ListView listView) {
        ActivationManager activationManager = new ActivationManager(this);
        java.util.Set<String> codes = getSharedPreferences("ActivationPrefs", MODE_PRIVATE)
            .getStringSet("pre_generated_codes", new java.util.HashSet<>());
        
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, 
            android.R.layout.simple_list_item_1, codes.toArray(new String[0]));
        listView.setAdapter(adapter);
    }
    
    private void showGeneratedCodesDialog(List<String> codes) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("رموز التفعيل المولدة");
        
        StringBuilder codesText = new StringBuilder();
        for (int i = 0; i < codes.size(); i++) {
            codesText.append((i + 1)).append(". ").append(codes.get(i)).append("\n");
        }
        
        TextView textView = new TextView(this);
        textView.setText(codesText.toString());
        textView.setPadding(50, 50, 50, 50);
        textView.setTextIsSelectable(true);
        
        ScrollView scrollView = new ScrollView(this);
        scrollView.addView(textView);
        
        builder.setView(scrollView);
        builder.setPositiveButton("نسخ الكل", (dialog, which) -> {
            android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            android.content.ClipData clip = android.content.ClipData.newPlainText("Activation Codes", codesText.toString());
            clipboard.setPrimaryClip(clip);
            Toast.makeText(this, "تم نسخ جميع الرموز", Toast.LENGTH_SHORT).show();
        });
        builder.setNegativeButton("إغلاق", null);
        builder.show();
    }
    
    private void saveSettings(String appName, String welcomeMessage) {
        Toast.makeText(this, "تم حفظ الإعدادات بنجاح", Toast.LENGTH_SHORT).show();
    }
}