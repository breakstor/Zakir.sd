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

public class ChatSessionAdapter extends RecyclerView.Adapter<ChatSessionAdapter.SessionViewHolder> {
    private List<ChatSession> sessionsList;
    private OnSessionClickListener listener;
    
    public interface OnSessionClickListener {
        void onSessionClick(ChatSession session);
        void onSessionLongClick(ChatSession session);
        void onDeleteSession(ChatSession session);
    }
    
    public ChatSessionAdapter(List<ChatSession> sessionsList, OnSessionClickListener listener) {
        this.sessionsList = sessionsList;
        this.listener = listener;
    }
    
    @NonNull
    @Override
    public SessionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_chat_session, parent, false);
        return new SessionViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull SessionViewHolder holder, int position) {
        ChatSession session = sessionsList.get(position);
        holder.bind(session, listener);
    }
    
    @Override
    public int getItemCount() {
        return sessionsList.size();
    }
    
    static class SessionViewHolder extends RecyclerView.ViewHolder {
        private TextView sessionTitle, sessionPreview, sessionDate, messageCount, subjectText;
        private View deleteButton;
        
        public SessionViewHolder(@NonNull View itemView) {
            super(itemView);
            sessionTitle = itemView.findViewById(R.id.sessionTitle);
            sessionPreview = itemView.findViewById(R.id.sessionPreview);
            sessionDate = itemView.findViewById(R.id.sessionDate);
            messageCount = itemView.findViewById(R.id.messageCount);
            subjectText = itemView.findViewById(R.id.subjectText);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
        
        public void bind(ChatSession session, OnSessionClickListener listener) {
            sessionTitle.setText(session.getSessionTitle());
            sessionPreview.setText(session.getPreview());
            
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy - HH:mm", Locale.getDefault());
            sessionDate.setText(dateFormat.format(session.getUpdatedAt()));
            
            int count = session.getMessageCount();
            messageCount.setText(count + " رسالة");
            
            if (session.getSubject() != null && !session.getSubject().isEmpty()) {
                subjectText.setText(session.getSubject());
                subjectText.setVisibility(View.VISIBLE);
            } else {
                subjectText.setVisibility(View.GONE);
            }
            
            itemView.setOnClickListener(v -> listener.onSessionClick(session));
            itemView.setOnLongClickListener(v -> {
                listener.onSessionLongClick(session);
                return true;
            });
            
            deleteButton.setOnClickListener(v -> listener.onDeleteSession(session));
        }
    }
}