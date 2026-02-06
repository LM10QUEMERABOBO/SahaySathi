package com.example.sahaysathi.ui.ngo.applicants;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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

    // ViewHolder Class
    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView name, city, skill;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.applicantName);
            city = itemView.findViewById(R.id.applicantCity);
            skill = itemView.findViewById(R.id.applicantSkill);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_applicant_card, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Applicant applicant = applicantList.get(position);

        holder.name.setText(applicant.name);
        holder.city.setText(applicant.city);
        holder.skill.setText("Skill: " + applicant.skill);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(applicant.name);

        builder.setMessage(
                "City: " + applicant.city + "\n" +
                        "Skill: " + applicant.skill
        );

        builder.setPositiveButton("Accept", (dialog, which) -> {
            // Accept logic
        });

        builder.setNegativeButton("Reject", (dialog, which) -> {
            // Reject logic
        });

        builder.setNeutralButton("View Details", (dialog, which) -> {
            Intent intent = new Intent(context, ApplicantDetailActivity.class);
            intent.putExtra("name", applicant.name);
            context.startActivity(intent);
        });



        // Open Applicant Detail Page
        holder.itemView.setOnClickListener(view -> {

            Intent intent = new Intent(context, ApplicantDetailActivity.class);
            intent.putExtra("name", applicant.name);
            intent.putExtra("city", applicant.city);
            intent.putExtra("skill", applicant.skill);
           builder.show();
        });
    }

    @Override
    public int getItemCount() {
        return applicantList.size();
    }
}
