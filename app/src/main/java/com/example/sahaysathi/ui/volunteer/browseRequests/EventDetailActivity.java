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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class EventDetailActivity extends AppCompatActivity {

    private TextView title, city, description;
    private Button btnApply;

    private String eventId;
    private String deadline;
    SharedPreferences sharedPreferences;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);

        sharedPreferences = this.getSharedPreferences(ConstantSp.pref, MODE_PRIVATE);
        db = FirebaseFirestore.getInstance();

        initViews();
        getIntentData();
        bindData();
    }

    private void initViews() {
        title = findViewById(R.id.dispEventTitle);
        city = findViewById(R.id.dispEventCity);
        description = findViewById(R.id.dispEventDesc);
        btnApply = findViewById(R.id.btnApply);
    }

    private void getIntentData() {
        eventId = getIntent().getStringExtra("eventId");
        String eventTitle = getIntent().getStringExtra("title");
        String eventCity = getIntent().getStringExtra("location");
        String eventDesc = getIntent().getStringExtra("description");
        deadline = getIntent().getStringExtra("deadline");
        title.setText(eventTitle != null ? eventTitle : "No Title");
        city.setText(eventCity != null ? eventCity : "No Location");
        description.setText(eventDesc != null ? eventDesc : "No Description");
    }

    private void bindData() {
        btnApply.setOnClickListener(v -> {
            if (isDeadlinePassed()) {
                Toast.makeText(this, "Deadline has passed", Toast.LENGTH_SHORT).show();
                return;
            }
            showConfirmationDialog();
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

        String userId = sharedPreferences.getString(ConstantSp.userid,"");

        if (userId == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

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
        map.put("timestamp", System.currentTimeMillis());

        btnApply.setEnabled(false);

        db.collection("applications").document(applicationId)
                .set(map)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this, "Application Submitted", Toast.LENGTH_SHORT).show();
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