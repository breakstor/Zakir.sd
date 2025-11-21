package com.sudanese.studentassistant;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ResourcesAdapter extends RecyclerView.Adapter<ResourcesAdapter.ResourceViewHolder> {
    private List<ResourceItem> resourcesList;
    private OnResourceClickListener listener;
    
    public interface OnResourceClickListener {
        void onResourceClick(ResourceItem resource);
        void onDownloadClick(ResourceItem resource);
        void onShareClick(ResourceItem resource);
    }
    
    public ResourcesAdapter(List<ResourceItem> resourcesList, OnResourceClickListener listener) {
        this.resourcesList = resourcesList;
        this.listener = listener;
    }
    
    @NonNull
    @Override
    public ResourceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_resource, parent, false);
        return new ResourceViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull ResourceViewHolder holder, int position) {
        ResourceItem resource = resourcesList.get(position);
        holder.bind(resource, listener);
    }
    
    @Override
    public int getItemCount() {
        return resourcesList.size();
    }
    
    static class ResourceViewHolder extends RecyclerView.ViewHolder {
        private TextView iconText, titleText, descriptionText, typeText, subjectText, yearText;
        private View downloadButton, shareButton;
        
        public ResourceViewHolder(@NonNull View itemView) {
            super(itemView);
            iconText = itemView.findViewById(R.id.iconText);
            titleText = itemView.findViewById(R.id.titleText);
            descriptionText = itemView.findViewById(R.id.descriptionText);
            typeText = itemView.findViewById(R.id.typeText);
            subjectText = itemView.findViewById(R.id.subjectText);
            yearText = itemView.findViewById(R.id.yearText);
            downloadButton = itemView.findViewById(R.id.downloadButton);
            shareButton = itemView.findViewById(R.id.shareButton);
        }
        
        public void bind(ResourceItem resource, OnResourceClickListener listener) {
            iconText.setText(resource.getTypeIcon());
            titleText.setText(resource.getTitle());
            descriptionText.setText(resource.getDescription());
            typeText.setText(resource.getTypeText());
            subjectText.setText(resource.getSubject());
            yearText.setText(resource.getYear());
            
            // تحديث حالة زر التحميل
            if (resource.isDownloaded()) {
                downloadButton.setBackgroundColor(itemView.getContext().getColor(android.R.color.holo_green_light));
                ((TextView) downloadButton).setText("📂 مفتوح");
            } else {
                downloadButton.setBackgroundColor(itemView.getContext().getColor(android.R.color.holo_blue_light));
                ((TextView) downloadButton).setText("⬇️ تحميل");
            }
            
            itemView.setOnClickListener(v -> listener.onResourceClick(resource));
            downloadButton.setOnClickListener(v -> listener.onDownloadClick(resource));
            shareButton.setOnClickListener(v -> listener.onShareClick(resource));
        }
    }
}