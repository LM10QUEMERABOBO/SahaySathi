package com.example.sahaysathi.ui.volunteer.browseRequests;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sahaysathi.R;

import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.ViewHolder> {

    Context context;
    List<Event> list;

    public EventAdapter(Context context, List<Event> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_event, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Event event = list.get(position);

        holder.title.setText(event.getEventName());
        holder.city.setText(event.getLocation());

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, EventDetailActivity.class);

            intent.putExtra("eventId", event.getRequestId());
            intent.putExtra("title", event.getEventName());
            intent.putExtra("location", event.getLocation());
            intent.putExtra("description", event.getDescription());
            intent.putExtra("deadline", event.getDeadline());
            intent.putExtra("ngoId", event.getNgoId());
            intent.putExtra("date", event.getDate());
            intent.putExtra("time", event.getTime());
            intent.putExtra("volunteerCount", event.getVolunteerCount());

            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void filterList(List<Event> filteredList) {
        this.list = filteredList;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, city;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.tvTitle);
            city = itemView.findViewById(R.id.tvCity);
        }
    }
}
