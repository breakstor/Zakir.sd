package com.sudanese.studentassistant;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

public class ActivationActivity extends AppCompatActivity {
    private EditText activationCodeInput;
    private Button activateButton, contactButton;
    private TextView deviceIdText, statusText;
    private ProgressBar progressBar;
    private ActivationManager activationManager;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activation);
        
        activationManager = new ActivationManager(this);
        initializeViews();
        checkInitialActivation();
    }
    
    private void initializeViews() {
        activationCodeInput = findViewById(R.id.activationCodeInput);
        activateButton = findViewById(R.id.activateButton);
        contactButton = findViewById(R.id.contactButton);
        deviceIdText = findViewById(R.id.deviceIdText);
        statusText = findViewById(R.id.statusText);
        progressBar = findViewById(R.id.progressBar);
        
        // عرض معرف الجهاز
        String deviceId = activationManager.getDeviceId();
        deviceIdText.setText("معرف الجهاز: " + deviceId);
        
        activateButton.setOnClickListener(v -> attemptActivation());
        contactButton.setOnClickListener(v -> openWhatsAppContact());
    }
    
    private void checkInitialActivation() {
        if (activationManager.isAppActivated()) {
            startMainActivity();
        } else {
            if (activationManager.isOnline()) {
                checkForAutoActivation();
            } else {
                statusText.setText("⚠️ يرجى الاتصال بالإنترنت لإكمال التفعيل");
            }
        }
    }
    
    private void checkForAutoActivation() {
        progressBar.setVisibility(View.VISIBLE);
        statusText.setText("🔍 جاري التحقق من حالة التفعيل...");
        
        new android.os.Handler().postDelayed(() -> {
            progressBar.setVisibility(View.GONE);
            if (activationManager.hasPendingActivation()) {
                statusText.setText("✅ تم تفعيل التطبيق تلقائياً");
                activationManager.activateApp(activationManager.getPendingActivationCode());
                startMainActivity();
            } else {
                statusText.setText("📝 يرجى إدخال رمز التفعيل");
            }
        }, 2000);
    }
    
    private void attemptActivation() {
        String code = activationCodeInput.getText().toString().trim();
        
        if (code.isEmpty()) {
            Toast.makeText(this, "يرجى إدخال رمز التفعيل", Toast.LENGTH_SHORT).show();
            return;
        }
        
        progressBar.setVisibility(View.VISIBLE);
        activateButton.setEnabled(false);
        
        if (activationManager.activateApp(code)) {
            statusText.setText("✅ تم تفعيل التطبيق بنجاح");
            Toast.makeText(this, "مرحباً بك في تطبيق مساعد الشهادة السودانية", Toast.LENGTH_LONG).show();
            
            new android.os.Handler().postDelayed(() -> {
                startMainActivity();
            }, 1500);
        } else {
            statusText.setText("❌ رمز التفعيل غير صحيح");
            activateButton.setEnabled(true);
        }
        
        progressBar.setVisibility(View.GONE);
    }
    
    private void openWhatsAppContact() {
        String phoneNumber = activationManager.getAdminPhoneNumber();
        String message = "أريد تفعيل تطبيق مساعد الشهادة السودانية\n\n" +
                        "معرف الجهاز: " + activationManager.getDeviceId() + "\n" +
                        "الإصدار: " + activationManager.getAppVersion();
        
        try {
            String url = "https://wa.me/" + phoneNumber + "?text=" + Uri.encode(message);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this, "يرجى تثبيت تطبيق WhatsApp", Toast.LENGTH_SHORT).show();
        }
    }
    
    private void startMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}