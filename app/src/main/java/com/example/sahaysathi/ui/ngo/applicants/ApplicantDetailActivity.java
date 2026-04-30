package com.example.sahaysathi.ui.ngo.applicants;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.InputType;
import android.util.Base64;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.sahaysathi.R;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

public class ApplicantDetailActivity extends AppCompatActivity {

    TextView name, city, skill, email, experience, phone;
    TextView eventName, eventLocation;

    ImageView volunteerImage;

    Button btnAccept, btnReject;

    String applicationId, volunteerId;

    FirebaseFirestore db;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_applicant_detail);

        // Initialize views
        volunteerImage = findViewById(R.id.detailVolunteerImage);

        name = findViewById(R.id.detailName);
        city = findViewById(R.id.detailCity);
        skill = findViewById(R.id.detailSkill);
        email = findViewById(R.id.detailEmail);
        phone = findViewById(R.id.detailPhone);
        experience = findViewById(R.id.detailExperience);
        eventName = findViewById(R.id.detailEventName);
        eventLocation = findViewById(R.id.detailEventLocation);
        btnAccept = findViewById(R.id.accept_button);
        btnReject = findViewById(R.id.regect_button);

        db = FirebaseFirestore.getInstance();
        applicationId = getIntent().getStringExtra("applicationId");
        volunteerId = getIntent().getStringExtra("volunteerId");

        name.setText(getIntent().getStringExtra("name"));
        city.setText("City: " + getIntent().getStringExtra("city"));
        skill.setText("Skill: " + getIntent().getStringExtra("skill"));
        eventName.setText("Event: " + getIntent().getStringExtra("eventName"));
        eventLocation.setText("Location: " + getIntent().getStringExtra("location"));

        loadVolunteerDetails();

        btnAccept.setOnClickListener(v -> showConfirmationDialog());
        btnReject.setOnClickListener(v -> updateStatus("rejected"));
    }

    @SuppressLint("SetTextI18n")
    private void loadVolunteerDetails() {
        if (volunteerId == null || volunteerId.isEmpty()) {
            Toast.makeText(this, "Volunteer not found", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("users")
                .document(volunteerId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {

                        String emailText = documentSnapshot.getString("email");
                        String phoneText = documentSnapshot.getString("phone");
                        String experienceText = documentSnapshot.getString("experience");
                        String base64Image = documentSnapshot.getString("profileImage");

                        email.setText("Email: " + (emailText != null ? emailText : "N/A"));
                        phone.setText("Contact: " + (phoneText != null ? phoneText : "N/A"));

                        if (experienceText != null && !experienceText.isEmpty()) {
                            experience.setText("Experience: " + experienceText);
                        } else {
                            experience.setText("Experience: N/A");
                        }

                        // Load volunteer image
                        if (base64Image != null && !base64Image.isEmpty()) {
                            try {
                                byte[] bytes = Base64.decode(base64Image, Base64.DEFAULT);
                                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                volunteerImage.setImageBitmap(bitmap);
                            } catch (Exception e) {
                                volunteerImage.setImageResource(R.drawable.sample_image);
                            }
                        } else {
                            volunteerImage.setImageResource(R.drawable.sample_image);
                        }
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Failed to load volunteer details", Toast.LENGTH_SHORT).show());
    }

    private void showConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Confirm Selection")
                .setMessage("Are you sure you want to accept this volunteer?")
                .setPositiveButton("Yes", (dialog, which) -> showInstructionDialog())
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showInstructionDialog() {

        EditText input = new EditText(this);
        input.setHint("Enter instructions for volunteer");
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        input.setPadding(40, 40, 40, 40);

        new AlertDialog.Builder(this)
                .setTitle("Provide Instructions")
                .setView(input)
                .setPositiveButton("Submit", (dialog, which) -> {

                    String instructionText = input.getText().toString().trim();

                    if (instructionText.isEmpty()) {
                        Toast.makeText(this, "Instructions are required", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    saveAcceptanceWithInstructions(instructionText);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void saveAcceptanceWithInstructions(String instructionText) {

        if (applicationId == null) {
            Toast.makeText(this, "Invalid application", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("applications")
                .document(applicationId)
                .update(
                        "status", "accepted",
                        "instructions", instructionText,
                        "acceptedAt", FieldValue.serverTimestamp()
                )
                .addOnSuccessListener(unused ->
                        Toast.makeText(this, "Volunteer accepted successfully", Toast.LENGTH_SHORT).show())

                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void updateStatus(String status) {

        if (applicationId == null) {
            Toast.makeText(this, "Invalid application", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("applications")
                .document(applicationId)
                .update("status", status)
                .addOnSuccessListener(unused ->
                        Toast.makeText(this, "Status updated", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}