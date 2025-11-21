package com.sudanese.studentassistant;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class StudyScheduleAdapter extends RecyclerView.Adapter<StudyScheduleAdapter.ScheduleViewHolder> {
    private List<StudySchedule> schedulesList;
    private OnScheduleClickListener listener;
    
    public interface OnScheduleClickListener {
        void onScheduleClick(StudySchedule schedule);
        void onScheduleLongClick(StudySchedule schedule);
        void onToggleCompletion(StudySchedule schedule, boolean isCompleted);
        void onDeleteSchedule(StudySchedule schedule);
    }
    
    public StudyScheduleAdapter(List<StudySchedule> schedulesList, OnScheduleClickListener listener) {
        this.schedulesList = schedulesList;
        this.listener = listener;
    }
    
    @NonNull
    @Override
    public ScheduleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_study_schedule, parent, false);
        return new ScheduleViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull ScheduleViewHolder holder, int position) {
        StudySchedule schedule = schedulesList.get(position);
        holder.bind(schedule, listener);
    }
    
    @Override
    public int getItemCount() {
        return schedulesList.size();
    }
    
    static class ScheduleViewHolder extends RecyclerView.ViewHolder {
        private TextView subjectText, topicText, timeText, durationText, statusText;
        private View completeButton, deleteButton;
        
        public ScheduleViewHolder(@NonNull View itemView) {
            super(itemView);
            subjectText = itemView.findViewById(R.id.subjectText);
            topicText = itemView.findViewById(R.id.topicText);
            timeText = itemView.findViewById(R.id.timeText);
            durationText = itemView.findViewById(R.id.durationText);
            statusText = itemView.findViewById(R.id.statusText);
            completeButton = itemView.findViewById(R.id.completeButton);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
        
        public void bind(StudySchedule schedule, OnScheduleClickListener listener) {
            subjectText.setText(schedule.getSubject());
            topicText.setText(schedule.getTopic());
            timeText.setText(schedule.getFormattedDateTime());
            durationText.setText(schedule.getDuration() + " دقيقة");
            statusText.setText(schedule.getStatusText());
            
            // تلوين الحالة
            if (schedule.isCompleted()) {
                statusText.setTextColor(itemView.getContext().getColor(android.R.color.holo_green_dark));
            } else {
                statusText.setTextColor(itemView.getContext().getColor(android.R.color.holo_red_dark));
            }
            
            itemView.setOnClickListener(v -> listener.onScheduleClick(schedule));
            itemView.setOnLongClickListener(v -> {
                listener.onScheduleLongClick(schedule);
                return true;
            });
            
            completeButton.setOnClickListener(v -> {
                boolean newCompletionState = !schedule.isCompleted();
                listener.onToggleCompletion(schedule, newCompletionState);
            });
            
            deleteButton.setOnClickListener(v -> listener.onDeleteSchedule(schedule));
        }
    }
}