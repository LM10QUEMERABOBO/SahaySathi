package com.example.sahaysathi.ui.ngo.applicants;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sahaysathi.R;

import java.util.ArrayList;

public class ApplicantAdapter extends RecyclerView.Adapter<ApplicantAdapter.ViewHolder> {

    private Context context;
    private ArrayList<Applicant> applicantList;

    public ApplicantAdapter(Context context, ArrayList<Applicant> applicantList) {
        this.context = context;
        this.applicantList = applicantList;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView name, city, skill, eventName,status;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.applicantName);
            city = itemView.findViewById(R.id.applicantCity);
            skill = itemView.findViewById(R.id.applicantSkill);
            eventName = itemView.findViewById(R.id.applicantEventName);
            status = itemView.findViewById(R.id.applicantEventStatus);
        }
    }

    @NonNull
    @Override
    public ApplicantAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.fragment_applicant_card, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ApplicantAdapter.ViewHolder holder, int position) {

        Applicant applicant = applicantList.get(position);

        holder.name.setText(applicant.getName() != null ? applicant.getName() : "N/A");
        holder.city.setText(applicant.getCity() != null ? applicant.getCity() : "N/A");
        holder.skill.setText("Skill: " + (applicant.getSkill() != null ? applicant.getSkill() : "N/A"));
        holder.eventName.setText("Event: " + (applicant.getEventName() != null ? applicant.getEventName() : "N/A"));
        holder.status.setText("Status: " + (applicant.getStatus() != null ? applicant.getStatus() : "N/A"));

        holder.itemView.setOnClickListener(v -> {
            if ("accepted".equalsIgnoreCase(applicant.getStatus())) {
                Toast.makeText(context, "Due Security Seasons,\nYou Can Not Revert The Status!", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(context, ApplicantDetailActivity.class);
            intent.putExtra("applicationId", applicant.getApplicationId());
            intent.putExtra("volunteerId", applicant.getVolunteerId());
            intent.putExtra("name", applicant.getName());
            intent.putExtra("city", applicant.getCity());
            intent.putExtra("skill", applicant.getSkill());
            intent.putExtra("eventName", applicant.getEventName());
            intent.putExtra("location", applicant.getLocation());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return applicantList.size();
    }
}