package com.sudanese.studentassistant;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.*;

public class ResourcesActivity extends AppCompatActivity {
    private RecyclerView resourcesRecyclerView;
    private ResourcesAdapter resourcesAdapter;
    private List<ResourceItem> resourcesList;
    private Spinner typeSpinner, yearSpinner, subjectSpinner;
    private Button filterButton;
    private ProgressBar loadingProgress;
    private TextView resultsCountText;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resources);
        
        initializeViews();
        setupSpinners();
        loadResources();
    }
    
    private void initializeViews() {
        resourcesRecyclerView = findViewById(R.id.resourcesRecyclerView);
        typeSpinner = findViewById(R.id.typeSpinner);
        yearSpinner = findViewById(R.id.yearSpinner);
        subjectSpinner = findViewById(R.id.subjectSpinner);
        filterButton = findViewById(R.id.filterButton);
        loadingProgress = findViewById(R.id.loadingProgress);
        resultsCountText = findViewById(R.id.resultsCountText);
        
        resourcesList = new ArrayList<>();
        resourcesAdapter = new ResourcesAdapter(resourcesList, new ResourcesAdapter.OnResourceClickListener() {
            @Override
            public void onResourceClick(ResourceItem resource) {
                openResource(resource);
            }
            
            @Override
            public void onDownloadClick(ResourceItem resource) {
                downloadResource(resource);
            }
            
            @Override
            public void onShareClick(ResourceItem resource) {
                shareResource(resource);
            }
        });
        
        resourcesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        resourcesRecyclerView.setAdapter(resourcesAdapter);
        
        filterButton.setOnClickListener(v -> applyFilters());
    }
    
    private void setupSpinners() {
        // نوع المورد
        String[] types = {"جميع الأنواع", "كتب", "امتحانات"};
        ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(this, 
            android.R.layout.simple_spinner_item, types);
        typeSpinner.setAdapter(typeAdapter);
        
        // السنوات (2015-2025)
        List<String> years = new ArrayList<>();
        years.add("جميع السنوات");
        for (int year = 2015; year <= 2025; year++) {
            years.add(String.valueOf(year));
        }
        ArrayAdapter<String> yearAdapter = new ArrayAdapter<>(this, 
            android.R.layout.simple_spinner_item, years);
        yearSpinner.setAdapter(yearAdapter);
        
        // المواد
        String[] subjects = {
            "جميع المواد", "الرياضيات", "الفيزياء", "الكيمياء", "الأحياء",
            "اللغة العربية", "اللغة الإنجليزية", "التربية الإسلامية",
            "التاريخ", "الجغرافيا"
        };
        ArrayAdapter<String> subjectAdapter = new ArrayAdapter<>(this, 
            android.R.layout.simple_spinner_item, subjects);
        subjectSpinner.setAdapter(subjectAdapter);
    }
    
    private void loadResources() {
        loadingProgress.setVisibility(View.VISIBLE);
        
        new android.os.Handler().postDelayed(() -> {
            resourcesList.clear();
            resourcesList.addAll(getSampleResources());
            resourcesAdapter.notifyDataSetChanged();
            loadingProgress.setVisibility(View.GONE);
            
            updateResultsCount();
        }, 1000);
    }
    
    private List<ResourceItem> getSampleResources() {
        List<ResourceItem> sampleResources = new ArrayList<>();
        
        // كتب عينة
        sampleResources.add(new ResourceItem(
            "كتاب الرياضيات للصف الثالث",
            "المنهج الكامل للرياضيات - الطبعة الجديدة 2024",
            "book", "2024", "الرياضيات",
            "https://drive.google.com/file/d/EXAMPLE1/view"
        ));
        
        sampleResources.add(new ResourceItem(
            "الفيزياء الحديثة - المنهج السوداني",
            "شرح مفصل لمنهج الفيزياء مع أمثلة عملية وتطبيقات",
            "book", "2024", "الفيزياء", 
            "https://drive.google.com/file/d/EXAMPLE2/view"
        ));
        
        sampleResources.add(new ResourceItem(
            "الكيمياء العضوية",
            "شرح شامل للكيمياء العضوية مع التجارب العملية",
            "book", "2024", "الكيمياء",
            "https://drive.google.com/file/d/EXAMPLE3/view"
        ));
        
        // امتحانات عينة
        sampleResources.add(new ResourceItem(
            "امتحان الرياضيات 2023",
            "امتحان الشهادة السودانية النهائي 2023 - مع نموذج الإجابة",
            "exam", "2023", "الرياضيات",
            "https://drive.google.com/file/d/EXAMPLE4/view"
        ));
        
        sampleResources.add(new ResourceItem(
            "امتحان الكيمياء 2022",
            "امتحان نهاية العام 2022 مع حلول مفصلة",
            "exam", "2022", "الكيمياء",
            "https://drive.google.com/file/d/EXAMPLE5/view"
        ));
        
        sampleResources.add(new ResourceItem(
            "امتحان الفيزياء 2021",
            "امتحان تجريبي مع تصحيح ذاتي",
            "exam", "2021", "الفيزياء",
            "https://drive.google.com/file/d/EXAMPLE6/view"
        ));
        
        sampleResources.add(new ResourceItem(
            "امتحان اللغة العربية 2023",
            "امتحان شامل لجميع فروع اللغة العربية",
            "exam", "2023", "اللغة العربية",
            "https://drive.google.com/file/d/EXAMPLE7/view"
        ));
        
        return sampleResources;
    }
    
    private void applyFilters() {
        String selectedType = typeSpinner.getSelectedItem().toString();
        String selectedYear = yearSpinner.getSelectedItem().toString();
        String selectedSubject = subjectSpinner.getSelectedItem().toString();
        
        List<ResourceItem> filteredList = new ArrayList<>();
        
        for (ResourceItem resource : getSampleResources()) {
            boolean typeMatch = selectedType.equals("جميع الأنواع") || 
                               (selectedType.equals("كتب") && resource.getType().equals("book")) ||
                               (selectedType.equals("امتحانات") && resource.getType().equals("exam"));
            
            boolean yearMatch = selectedYear.equals("جميع السنوات") || 
                               resource.getYear().equals(selectedYear);
            
            boolean subjectMatch = selectedSubject.equals("جميع المواد") || 
                                  resource.getSubject().equals(selectedSubject);
            
            if (typeMatch && yearMatch && subjectMatch) {
                filteredList.add(resource);
            }
        }
        
        resourcesList.clear();
        resourcesList.addAll(filteredList);
        resourcesAdapter.notifyDataSetChanged();
        updateResultsCount();
    }
    
    private void updateResultsCount() {
        int count = resourcesList.size();
        resultsCountText.setText("عدد النتائج: " + count);
    }
    
    private void openResource(ResourceItem resource) {
        if (resource.isDownloaded()) {
            // فتح الملف المحلي
            Toast.makeText(this, "جاري فتح الملف المحلي", Toast.LENGTH_SHORT).show();
        } else {
            // فتح عبر المتصفح أو قارئ PDF
            Intent intent = new Intent(this, PdfViewerActivity.class);
            intent.putExtra("resource_title", resource.getTitle());
            intent.putExtra("resource_url", resource.getDownloadUrl());
            startActivity(intent);
        }
    }
    
    private void downloadResource(ResourceItem resource) {
        Toast.makeText(this, "جاري تحميل " + resource.getTitle(), Toast.LENGTH_SHORT).show();
        
        new android.os.Handler().postDelayed(() -> {
            resource.setDownloaded(true);
            resourcesAdapter.notifyDataSetChanged();
            Toast.makeText(this, "تم تحميل " + resource.getTitle(), Toast.LENGTH_SHORT).show();
        }, 2000);
    }
    
    private void shareResource(ResourceItem resource) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, resource.getTitle());
        shareIntent.putExtra(Intent.EXTRA_TEXT, 
            resource.getTitle() + "\n\n" + resource.getDescription() + "\n\n" +
            "رابط التحميل: " + resource.getDownloadUrl());
        
        startActivity(Intent.createChooser(shareIntent, "مشاركة الملف"));
    }
}