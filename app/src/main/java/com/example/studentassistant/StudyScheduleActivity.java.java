package com.sudanese.studentassistant;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.text.SimpleDateFormat;
import java.util.*;

public class StudyScheduleActivity extends AppCompatActivity {
    private RecyclerView schedulesRecyclerView;
    private StudyScheduleAdapter scheduleAdapter;
    private List<StudySchedule> schedulesList;
    private StudyScheduleDBHelper dbHelper;
    private TextView emptyStateText;
    private Spinner subjectSpinner;
    private Button addScheduleButton;
    private FloatingActionButton fabAddSchedule;
    private ProgressBar loadingProgress;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_study_schedule);
        
        initializeViews();
        setupDatabase();
        setupSubjectSpinner();
        loadSchedules();
    }
    
    private void initializeViews() {
        schedulesRecyclerView = findViewById(R.id.schedulesRecyclerView);
        emptyStateText = findViewById(R.id.emptyStateText);
        subjectSpinner = findViewById(R.id.subjectSpinner);
        addScheduleButton = findViewById(R.id.addScheduleButton);
        fabAddSchedule = findViewById(R.id.fabAddSchedule);
        loadingProgress = findViewById(R.id.loadingProgress);
        
        schedulesList = new ArrayList<>();
        scheduleAdapter = new StudyScheduleAdapter(schedulesList, new StudyScheduleAdapter.OnScheduleClickListener() {
            @Override
            public void onScheduleClick(StudySchedule schedule) {
                showScheduleDetails(schedule);
            }
            
            @Override
            public void onScheduleLongClick(StudySchedule schedule) {
                showScheduleOptions(schedule);
            }
            
            @Override
            public void onToggleCompletion(StudySchedule schedule, boolean isCompleted) {
                dbHelper.updateScheduleCompletion(schedule.getId(), isCompleted);
                loadSchedules();
                Toast.makeText(StudyScheduleActivity.this, 
                    isCompleted ? "تم تمييز الجدول كمكتمل" : "تم إلغاء إكتمال الجدول", 
                    Toast.LENGTH_SHORT).show();
            }
            
            @Override
            public void onDeleteSchedule(StudySchedule schedule) {
                deleteSchedule(schedule);
            }
        });
        
        schedulesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        schedulesRecyclerView.setAdapter(scheduleAdapter);
        
        addScheduleButton.setOnClickListener(v -> showAddScheduleDialog());
        fabAddSchedule.setOnClickListener(v -> showAddScheduleDialog());
        
        subjectSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                loadSchedules();
            }
            
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }
    
    private void setupDatabase() {
        dbHelper = new StudyScheduleDBHelper(this);
    }
    
    private void setupSubjectSpinner() {
        String[] subjects = {
            "جميع المواد", "الرياضيات", "الفيزياء", "الكيمياء", "الأحياء", 
            "اللغة العربية", "اللغة الإنجليزية", "التربية الإسلامية",
            "التاريخ", "الجغرافيا"
        };
        
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, 
            android.R.layout.simple_spinner_item, subjects);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        subjectSpinner.setAdapter(adapter);
    }
    
    private void loadSchedules() {
        loadingProgress.setVisibility(View.VISIBLE);
        
        new android.os.Handler().postDelayed(() -> {
            schedulesList.clear();
            
            String selectedSubject = subjectSpinner.getSelectedItem().toString();
            if (selectedSubject.equals("جميع المواد")) {
                schedulesList.addAll(dbHelper.getAllSchedules());
            } else {
                schedulesList.addAll(dbHelper.getSchedulesBySubject(selectedSubject));
            }
            
            scheduleAdapter.notifyDataSetChanged();
            loadingProgress.setVisibility(View.GONE);
            
            if (schedulesList.isEmpty()) {
                emptyStateText.setVisibility(View.VISIBLE);
                schedulesRecyclerView.setVisibility(View.GONE);
            } else {
                emptyStateText.setVisibility(View.GONE);
                schedulesRecyclerView.setVisibility(View.VISIBLE);
            }
        }, 500);
    }
    
    private void showAddScheduleDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("إضافة جدول مذاكرة جديد");
        
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_schedule, null);
        builder.setView(dialogView);
        
        Spinner dialogSubjectSpinner = dialogView.findViewById(R.id.dialogSubjectSpinner);
        EditText topicEditText = dialogView.findViewById(R.id.topicEditText);
        DatePicker datePicker = dialogView.findViewById(R.id.datePicker);
        TimePicker timePicker = dialogView.findViewById(R.id.timePicker);
        EditText durationEditText = dialogView.findViewById(R.id.durationEditText);
        EditText notesEditText = dialogView.findViewById(R.id.notesEditText);
        CheckBox reminderCheckBox = dialogView.findViewById(R.id.reminderCheckBox);
        
        setupDialogSubjectSpinner(dialogSubjectSpinner);
        
        // تعيين القيم الافتراضية
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, 1); // غداً
        datePicker.updateDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        
        timePicker.setCurrentHour(18);
        timePicker.setCurrentMinute(0);
        durationEditText.setText("60");
        
        builder.setPositiveButton("حفظ", (dialog, which) -> {
            String subject = dialogSubjectSpinner.getSelectedItem().toString();
            String topic = topicEditText.getText().toString();
            String notes = notesEditText.getText().toString();
            
            if (topic.isEmpty()) {
                Toast.makeText(this, "يرجى إدخال موضوع المذاكرة", Toast.LENGTH_SHORT).show();
                return;
            }
            
            int duration;
            try {
                duration = Integer.parseInt(durationEditText.getText().toString());
            } catch (NumberFormatException e) {
                duration = 60;
            }
            
            Calendar studyCalendar = Calendar.getInstance();
            studyCalendar.set(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth(),
                            timePicker.getCurrentHour(), timePicker.getCurrentMinute());
            
            StudySchedule schedule = new StudySchedule(subject, topic, studyCalendar.getTime(), studyCalendar.getTime(), duration);
            schedule.setNotes(notes);
            schedule.setHasReminder(reminderCheckBox.isChecked());
            
            long scheduleId = dbHelper.addSchedule(schedule);
            
            if (schedule.isHasReminder()) {
                setScheduleReminder(schedule);
            }
            
            loadSchedules();
            Toast.makeText(this, "تم إضافة جدول المذاكرة", Toast.LENGTH_SHORT).show();
        });
        
        builder.setNegativeButton("إلغاء", null);
        builder.show();
    }
    
    private void setupDialogSubjectSpinner(Spinner spinner) {
        String[] subjects = {
            "الرياضيات", "الفيزياء", "الكيمياء", "الأحياء", 
            "اللغة العربية", "اللغة الإنجليزية", "التربية الإسلامية",
            "التاريخ", "الجغرافيا"
        };
        
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, 
            android.R.layout.simple_spinner_item, subjects);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }
    
    private void setScheduleReminder(StudySchedule schedule) {
        try {
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(this, StudyReminderReceiver.class);
            intent.putExtra("schedule_id", schedule.getId());
            intent.putExtra("subject", schedule.getSubject());
            intent.putExtra("topic", schedule.getTopic());
            
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, schedule.getId(), intent, 
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
            
            Calendar reminderTime = Calendar.getInstance();
            reminderTime.setTime(schedule.getStudyTime());
            reminderTime.add(Calendar.MINUTE, -schedule.getReminderMinutes());
            
            alarmManager.set(AlarmManager.RTC_WAKEUP, reminderTime.getTimeInMillis(), pendingIntent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void showScheduleDetails(StudySchedule schedule) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("تفاصيل جدول المذاكرة");
        
        String details = "📚 المادة: " + schedule.getSubject() + "\n\n" +
                        "📖 الموضوع: " + schedule.getTopic() + "\n\n" +
                        "⏰ الوقت: " + schedule.getFormattedDateTime() + "\n\n" +
                        "⏱️ المدة: " + schedule.getDuration() + " دقيقة\n\n" +
                        "📌 الحالة: " + schedule.getStatusText() + "\n\n" +
                        "🔔 التنبيه: " + (schedule.isHasReminder() ? "مفعل" : "غير مفعل") + "\n\n" +
                        "📝 ملاحظات: " + (schedule.getNotes() != null ? schedule.getNotes() : "لا توجد");
        
        builder.setMessage(details);
        builder.setPositiveButton("حسناً", null);
        builder.setNegativeButton("تمييز كمكتمل", (dialog, which) -> {
            dbHelper.updateScheduleCompletion(schedule.getId(), true);
            loadSchedules();
            Toast.makeText(this, "تم تمييز الجدول كمكتمل", Toast.LENGTH_SHORT).show();
        });
        builder.show();
    }
    
    private void showScheduleOptions(StudySchedule schedule) {
        String[] options = {"عرض التفاصيل", "تمييز كمكتمل", "حذف الجدول"};
        
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("خيارات جدول المذاكرة");
        builder.setItems(options, (dialog, which) -> {
            switch (which) {
                case 0:
                    showScheduleDetails(schedule);
                    break;
                case 1:
                    dbHelper.updateScheduleCompletion(schedule.getId(), true);
                    loadSchedules();
                    Toast.makeText(this, "تم تمييز الجدول كمكتمل", Toast.LENGTH_SHORT).show();
                    break;
                case 2:
                    deleteSchedule(schedule);
                    break;
            }
        });
        builder.show();
    }
    
    private void deleteSchedule(StudySchedule schedule) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("تأكيد الحذف");
        builder.setMessage("هل أنت متأكد من حذف جدول المذاكرة هذا؟");
        
        builder.setPositiveButton("حذف", (dialog, which) -> {
            dbHelper.deleteSchedule(schedule.getId());
            loadSchedules();
            Toast.makeText(this, "تم حذف جدول المذاكرة", Toast.LENGTH_SHORT).show();
        });
        
        builder.setNegativeButton("إلغاء", null);
        builder.show();
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        loadSchedules();
    }
}