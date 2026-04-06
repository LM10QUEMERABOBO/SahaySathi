package com.example.sahaysathi.ui.ngo.applicants;

import android.app.AlertDialog;
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
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class ApplicantAdapter extends RecyclerView.Adapter<ApplicantAdapter.ViewHolder> {

    private Context context;
    private ArrayList<Applicant> applicantList;

    public ApplicantAdapter(Context context, ArrayList<Applicant> applicantList) {
        this.context = context;
        this.applicantList = applicantList;
    }

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

        holder.name.setText(applicant.getName() != null ? applicant.getName() : "N/A");
        holder.city.setText(applicant.getCity() != null ? applicant.getCity() : "N/A");
        holder.skill.setText("Skill: " + (applicant.getSkill() != null ? applicant.getSkill() : "N/A"));

        // Fade animation
        holder.itemView.setAlpha(0f);
        holder.itemView.animate().alpha(1f).setDuration(300).start();

        holder.itemView.setOnClickListener(view -> {

            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle(applicant.getName());

            builder.setMessage(
                    "City: " + applicant.getCity() + "\n" +
                            "Skill: " + applicant.getSkill()
            );

            builder.setPositiveButton("Yes", null);
            builder.setNegativeButton("No",null);
            builder.setNeutralButton("View Details", (dialog, which) -> {
                Intent intent = new Intent(context, ApplicantDetailActivity.class);
                intent.putExtra("applicationId", applicant.getApplicationId());
                intent.putExtra("volunteerId",applicant.getVolunteerId());
                intent.putExtra("name", applicant.getName());
                intent.putExtra("city", applicant.getCity());
                intent.putExtra("skill", applicant.getSkill());
                context.startActivity(intent);
            });

            builder.show();
        });
    }

    private void updateStatus(Applicant applicant, int position, String status) {

        FirebaseFirestore.getInstance()
                .collection("applications")
                .document(applicant.getApplicationId())
                .update("status", status)
                .addOnSuccessListener(unused -> {

                    // 🔥 Instant UI removal
                    applicantList.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, applicantList.size());

                    Toast.makeText(context, "Updated", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(context, "Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }

    @Override
    public int getItemCount() {
        return applicantList.size();
    }
}