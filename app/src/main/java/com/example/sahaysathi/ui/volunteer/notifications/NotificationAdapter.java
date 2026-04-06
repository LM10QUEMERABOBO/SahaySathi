package com.example.sahaysathi.ui.volunteer.notifications;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sahaysathi.R;

import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    Context context;
    List<ApplicationModel> list;

    public NotificationAdapter(Context context, List<ApplicationModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_notification, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        ApplicationModel model = list.get(position);

        holder.name.setText(model.getEventName());
        holder.city.setText(model.getCity());
        holder.status.setText(model.getStatus());

        switch (model.getStatus()) {
            case "accepted":
                holder.status.setBackgroundColor(Color.GREEN);
                break;
            case "pending":
                holder.status.setBackgroundColor(Color.YELLOW);
                break;
            case "rejected":
                holder.status.setBackgroundColor(Color.RED);
                break;
        }
    }

    @Override
    public int getItemCount() { return list.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView name, city, status;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.tvName);
            city = itemView.findViewById(R.id.tvCity);
            status = itemView.findViewById(R.id.tvStatus);
        }
    }
}