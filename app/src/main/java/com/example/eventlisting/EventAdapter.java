package com.example.eventlisting;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Button;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {

    private List<Event> eventList;
    private Context context;
    private OnItemClickListener onItemClickListener;
    private OnEventActionListener actionListener;
    private boolean showActionButtons;

    public EventAdapter(List<Event> eventList, Context context, boolean showActionButtons) {
        this.eventList = (eventList != null) ? eventList : new ArrayList<>();
        this.context = context;
        this.showActionButtons = showActionButtons;
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public interface OnEventActionListener {
        void onEditClick(int position);
        void onDeleteClick(int position);
    }

    public void setOnEventActionListener(OnEventActionListener listener) {
        this.actionListener = listener;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.event_item, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        Event event = eventList.get(position);
        if (event == null) return;

        holder.nameTextView.setText(event.getName());
        holder.dateTextView.setText("Tarih: " + event.getDate());
        holder.capacityTextView.setText("Kapasite: " + event.getCapacity());
        holder.locationTextView.setText("Konum: " + event.getCity() + ", " + event.getDistrict());
        holder.typeTextView.setText("Tür: " + event.getType());

        String uri = event.getImageUri();
        if (uri != null && !uri.trim().isEmpty() && !uri.trim().equalsIgnoreCase("null")) {
            Glide.with(context)
                    .load(Uri.parse(uri))
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(holder.eventImageView);
        } else if (event.getImageResourceId() != 0) {
            holder.eventImageView.setImageResource(event.getImageResourceId());
        }


        // Buton layout'u da dahil tümünü göster/gizle
        holder.actionButtonsLayout.setVisibility(showActionButtons ? View.VISIBLE : View.GONE);
        holder.editButton.setVisibility(showActionButtons ? View.VISIBLE : View.GONE);
        holder.deleteButton.setVisibility(showActionButtons ? View.VISIBLE : View.GONE);

        holder.editButton.setOnClickListener(v -> {
            if (actionListener != null) {
                actionListener.onEditClick(holder.getAdapterPosition());
            }
        });

        holder.deleteButton.setOnClickListener(v -> {
            if (actionListener != null) {
                actionListener.onDeleteClick(holder.getAdapterPosition());
            }
        });

        holder.itemView.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                int pos = holder.getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    onItemClickListener.onItemClick(pos);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return (eventList != null) ? eventList.size() : 0;
    }

    public Event getEventAt(int position) {
        if (eventList != null && position >= 0 && position < eventList.size()) {
            return eventList.get(position);
        }
        return null;
    }

    public void updateList(List<Event> newEventList) {
        this.eventList = (newEventList != null) ? newEventList : new ArrayList<>();
        notifyDataSetChanged();
    }

    public static class EventViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView, dateTextView, capacityTextView, locationTextView, typeTextView;
        ImageView eventImageView;
        Button editButton, deleteButton;
        LinearLayout actionButtonsLayout;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.eventTitleTextView);
            dateTextView = itemView.findViewById(R.id.eventDateTextView);
            capacityTextView = itemView.findViewById(R.id.eventCapacityTextView);
            locationTextView = itemView.findViewById(R.id.eventLocationTextView);
            typeTextView = itemView.findViewById(R.id.eventTypeTextView);
            eventImageView = itemView.findViewById(R.id.eventImage);
            editButton = itemView.findViewById(R.id.editButton);
            deleteButton = itemView.findViewById(R.id.deleteButton);
            actionButtonsLayout = itemView.findViewById(R.id.actionButtonsLayout); // ✅ EKLENDİ
        }
    }
}
