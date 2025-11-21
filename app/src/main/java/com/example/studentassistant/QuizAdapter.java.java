package com.sudanese.studentassistant;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class QuizAdapter extends RecyclerView.Adapter<QuizAdapter.QuizViewHolder> {
    private List<QuizModel> quizzesList;
    private OnQuizClickListener listener;
    
    public interface OnQuizClickListener {
        void onQuizClick(QuizModel quiz);
    }
    
    public QuizAdapter(List<QuizModel> quizzesList, OnQuizClickListener listener) {
        this.quizzesList = quizzesList;
        this.listener = listener;
    }
    
    @NonNull
    @Override
    public QuizViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_quiz, parent, false);
        return new QuizViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull QuizViewHolder holder, int position) {
        QuizModel quiz = quizzesList.get(position);
        holder.bind(quiz, listener);
    }
    
    @Override
    public int getItemCount() {
        return quizzesList.size();
    }
    
    static class QuizViewHolder extends RecyclerView.ViewHolder {
        private TextView titleText, subjectText, questionsCountText, timeText, passingScoreText;
        
        public QuizViewHolder(@NonNull View itemView) {
            super(itemView);
            titleText = itemView.findViewById(R.id.titleText);
            subjectText = itemView.findViewById(R.id.subjectText);
            questionsCountText = itemView.findViewById(R.id.questionsCountText);
            timeText = itemView.findViewById(R.id.timeText);
            passingScoreText = itemView.findViewById(R.id.passingScoreText);
        }
        
        public void bind(QuizModel quiz, OnQuizClickListener listener) {
            titleText.setText(quiz.getTitle());
            subjectText.setText(quiz.getSubject());
            questionsCountText.setText(quiz.getTotalQuestions() + " أسئلة");
            timeText.setText((quiz.getTimeLimit() / 60) + " دقيقة");
            passingScoreText.setText(quiz.getPassingScore() + "% للنجاح");
            
            itemView.setOnClickListener(v -> listener.onQuizClick(quiz));
        }
    }
}