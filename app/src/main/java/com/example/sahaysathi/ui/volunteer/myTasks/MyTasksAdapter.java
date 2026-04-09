package com.example.sahaysathi.ui.volunteer.myTasks;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sahaysathi.R;
import com.example.sahaysathi.ui.volunteer.notifications.ApplicationModel;

import java.util.List;
public class MyTasksAdapter extends RecyclerView.Adapter<MyTasksAdapter.ViewHolder> {

    Context context;
    List<ApplicationModel> list;

    public MyTasksAdapter(Context context, List<ApplicationModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_tasks, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        ApplicationModel model = list.get(position);

        holder.name.setText(model.getEventName() != null ? model.getEventName() : "N/A");
        holder.city.setText(model.getlocation() != null ? model.getlocation() : "N/A");
        holder.instructions.setText(model.getInstructions() != null ? model.getInstructions() : "No instructions");

        holder.itemView.setOnClickListener(v -> {
            Intent i = new Intent(context, QRActivity.class);
            i.putExtra("eventId", model.getEventId());
            i.putExtra("eventName", model.getEventName());
            i.putExtra("city", model.getlocation());
            i.putExtra("instructions", model.getInstructions());
            context.startActivity(i);
        });
    }

    @Override
    public int getItemCount() { return list.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView name, city, instructions;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.tvName);
            city = itemView.findViewById(R.id.tvCity);
            instructions = itemView.findViewById(R.id.tvInstructions);
        }
    }
}