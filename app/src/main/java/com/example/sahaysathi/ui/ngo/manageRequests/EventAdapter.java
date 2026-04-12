package com.example.sahaysathi.ui.ngo.manageRequests;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sahaysathi.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.AggregateQuery;
import com.google.firebase.firestore.AggregateQuerySnapshot;
import com.google.firebase.firestore.AggregateSource;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.ViewHolder> {

    Context context;
    ArrayList<Event> list;
    OnItemClickListener listener;

    public interface OnItemClickListener {
        void onClick(Event event);
    }

    public EventAdapter(Context context, ArrayList<Event> list, OnItemClickListener listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView title, location, date, applied, selected;

        public ViewHolder(View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.eventTitle);
            location = itemView.findViewById(R.id.eventLocation);
            date = itemView.findViewById(R.id.eventDate);
            applied = itemView.findViewById(R.id.appliedCount);
            selected = itemView.findViewById(R.id.selectedCount);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.fragement_managerequest_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        Event event = list.get(position);

        holder.title.setText(event.title);
        holder.location.setText(event.location);
        holder.date.setText(event.date);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        holder.applied.setText("Applied: ...");
        holder.selected.setText("Accepted: ...");

        String eventId = event.id;
        db.collection("applications")
                .whereEqualTo("eventId", eventId)
                .count()
                .get(AggregateSource.SERVER)
                .addOnSuccessListener(snapshot -> {

                    long count = snapshot.getCount();
                    holder.applied.setText("Applied: " + count);

                    Log.d("TAG", "Applied Count: " + count);
                });
        db.collection("applications")
                .whereEqualTo("eventId", eventId)
                .whereEqualTo("status", "accepted")
                .count()
                .get(AggregateSource.SERVER)
                .addOnSuccessListener(snapshot -> {

                    long count = snapshot.getCount();
                    holder.selected.setText("Accepted: " + count);

                    Log.d("TAG", "Accepted Count: " + count);
                });
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onClick(event);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}