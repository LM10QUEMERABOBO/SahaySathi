package com.example.sahaysathi.ui.volunteer.browseRequests;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.sahaysathi.ConstantSp;
import com.example.sahaysathi.R;
import com.example.sahaysathi.services.NotificationHelper;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class EventDetailActivity extends AppCompatActivity {

    private TextView title, city, description, dateTv, timeTv, deadlineStatus;
    private Button btnApply;
    private String eventId, ngoId, deadline, date, time, volunteerLimit;
    private FirebaseFirestore db;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);

        db = FirebaseFirestore.getInstance();
        sharedPreferences = getSharedPreferences(ConstantSp.pref, MODE_PRIVATE);

        initViews();
        getIntentData();
        bindData();
    }

    private void initViews() {
        title = findViewById(R.id.dispEventTitle);
        city = findViewById(R.id.dispEventCity);
        description = findViewById(R.id.dispEventDesc);

        // NEW
        dateTv = findViewById(R.id.dispEventDate);
        timeTv = findViewById(R.id.dispEventTime);
        deadlineStatus = findViewById(R.id.dispDeadlineStatus);

        btnApply = findViewById(R.id.btnApply);
    }

    private void getIntentData() {

        eventId = getIntent().getStringExtra("eventId");
        deadline = getIntent().getStringExtra("deadline");
        date = getIntent().getStringExtra("date");
        time = getIntent().getStringExtra("time");
        volunteerLimit = getIntent().getStringExtra("volunteerCount");
        ngoId = getIntent().getStringExtra("ngoId");
        title.setText(getIntent().getStringExtra("title"));
        city.setText(getIntent().getStringExtra("location"));
        description.setText(getIntent().getStringExtra("description"));
        // SET NEW DATA
        dateTv.setText("Date: " + date);
        timeTv.setText("Time: " + time);

        if (isDeadlinePassed()) {
            deadlineStatus.setText("Application Deadline Passed ❌");
            btnApply.setEnabled(false);
        } else {
            deadlineStatus.setText("Applications Are Applicable ✅");
        }
    }

    private void bindData() {
        btnApply.setOnClickListener(v -> {

            if (isDeadlinePassed()) {
                Toast.makeText(this, "Deadline has passed", Toast.LENGTH_SHORT).show();
                return;
            }

            checkVolunteerLimit();
        });
    }

    // ✅ CHECK LIMIT
    private void checkVolunteerLimit() {

        db.collection("applications")
                .whereEqualTo("eventId", eventId)
                .whereEqualTo("status", "accepted")
                .get()
                .addOnSuccessListener(query -> {

                    int limit;

                    try {
                        limit = Integer.parseInt(volunteerLimit);
                        showConfirmationDialog();
                    } catch (Exception e) {
                        Toast.makeText(this, "Invalid volunteer limit", Toast.LENGTH_SHORT).show();
                        return;
                    }
                });
    }

    private void showConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Confirm Application")
                .setMessage("Apply for this event?")
                .setPositiveButton("Yes", (dialog, which) -> checkAndApply())
                .setNegativeButton("No", null)
                .show();
    }
    private void checkAndApply() {
        String userId = sharedPreferences.getString(ConstantSp.userid, "");
        db.collection("applications")
                .whereEqualTo("eventId", eventId)
                .whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener(query -> {
                    if (!query.isEmpty()) {
                        Toast.makeText(this, "Already applied", Toast.LENGTH_SHORT).show();
                    } else {
                        applyForEvent(userId);
                    }
                });
    }

    private void applyForEvent(String userId) {

        String applicationId = userId + "_APPLIED_" + eventId;

        HashMap<String, Object> map = new HashMap<>();
        map.put("applicationId", applicationId);
        map.put("eventId", eventId);
        map.put("userId", userId);
        map.put("status", "pending");
        map.put("ngoId", ngoId);
        map.put("eventName", title.getText().toString());
        map.put("location", city.getText().toString());
        map.put("timestamp", System.currentTimeMillis());

        btnApply.setEnabled(false);

        db.collection("applications")
                .add(map)
                .addOnSuccessListener(unused -> {
                    NotificationHelper.appliedEventSuccess(this, title.getText().toString());
                    finish();
                })
                .addOnFailureListener(e -> {
                    btnApply.setEnabled(true);
                    Toast.makeText(this, "Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private boolean isDeadlinePassed() {
        try {
            if (deadline == null) return false;

            SimpleDateFormat sdf = new SimpleDateFormat("d/M/yyyy", Locale.getDefault());

            Date deadlineDate = sdf.parse(deadline);
            Date today = new Date();

            return deadlineDate != null && deadlineDate.before(today);

        } catch (Exception e) {
            return false;
        }
    }
}