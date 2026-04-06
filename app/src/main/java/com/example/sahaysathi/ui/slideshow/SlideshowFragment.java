package com.example.sahaysathi.ui.slideshow;

import static android.content.Context.MODE_PRIVATE;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import com.example.sahaysathi.ConstantSp;
import com.example.sahaysathi.Login_Page;
import com.example.sahaysathi.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class SlideshowFragment extends Fragment {
    SharedPreferences sharedPreferences;
    FirebaseFirestore db;

    // Dropdowns
    TextView dropNgoDetails, dropContact,dropUserPref;
    LinearLayout formNgoDetails, formContact,formUserPref;

    // NGO fields
    EditText etNgoName, etRegNo, etAddress;
    Button btnUpdateNgo;

    // Contact fields
    EditText etPhone, etEmail;
    Button btnUpdateContact;

    ProgressBar progressBar;

    String userId;
    TextView tvName, tvEmail;
    Switch switchDarkMode, switchNotifications;
    Button btnLogout;

    SharedPreferences sp;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_slideshow, container, false);

        // Initialize Views
        tvName = view.findViewById(R.id.tvName);
//        tvEmail = view.findViewById(R.id.tvEmail);
        switchDarkMode = view.findViewById(R.id.switchDarkMode);
        switchNotifications = view.findViewById(R.id.switchNotifications);
        btnLogout = view.findViewById(R.id.btnLogout);
        sp = requireActivity().getSharedPreferences(ConstantSp.pref, MODE_PRIVATE);

        // Load user data
        String name = sp.getString(ConstantSp.name, "User");
        String email = sp.getString(ConstantSp.email, "user@gmail.com");
        tvName.setText("Hello "+name+" \uD83D\uDC4B");
//        tvEmail.setText("Email: " + email);


        // Load switch states
        switchDarkMode.setChecked(sp.getBoolean("dark_mode", false));
        switchNotifications.setChecked(sp.getBoolean("notifications", true));

        // Dark Mode toggle
        switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            sp.edit().putBoolean("dark_mode", isChecked).apply();
        });

        // Notification toggle
        switchNotifications.setOnCheckedChangeListener((buttonView, isChecked) -> {
            sp.edit().putBoolean("notifications", isChecked).apply();
        });

        // Logout button
        btnLogout.setOnClickListener(v -> {
            showLogoutDialog();
        });

        db = FirebaseFirestore.getInstance();

        userId = sp.getString(ConstantSp.userid, "");

        // Dropdowns
        dropNgoDetails = view.findViewById(R.id.dropNgoDetails);
        formNgoDetails = view.findViewById(R.id.formNgoDetails);

        dropContact = view.findViewById(R.id.dropContact);
        formContact = view.findViewById(R.id.formContact);
        dropUserPref = view.findViewById(R.id.dropUserPreference);
        formUserPref = view.findViewById(R.id.formUserPreference);


        // NGO fields
        etNgoName = view.findViewById(R.id.etNgoName);
        etRegNo = view.findViewById(R.id.etRegNo);
        etAddress = view.findViewById(R.id.etAddress);
        btnUpdateNgo = view.findViewById(R.id.btnUpdateNgo);

        // Contact fields
        etPhone = view.findViewById(R.id.etPhone);
        etEmail = view.findViewById(R.id.etEmail);
        btnUpdateContact = view.findViewById(R.id.btnUpdateContact);

        progressBar = view.findViewById(R.id.progressBar);

        // Toggle
        dropUserPref.setOnClickListener(v -> toggle(formUserPref));
        dropNgoDetails.setOnClickListener(v -> toggle(formNgoDetails));
        dropContact.setOnClickListener(v -> toggle(formContact));
//        etEmail.setOnClickListener(v -> Toast.makeText(this,"Email not Modifialbe!",Toast.LENGTH_SHORT).show());
        // Load existing data
        loadProfile();

        // Update NGO details
        btnUpdateNgo.setOnClickListener(v -> updateNgoDetails());

        // Update Contact
        btnUpdateContact.setOnClickListener(v -> updateContact());

        return view;
    }
    private void showLogoutDialog() {
        new AlertDialog.Builder(getContext())
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Yes", (dialog, which) -> {

                    // 🔥 Firebase logout
                    FirebaseAuth.getInstance().signOut();

                    // 🔥 Clear local data
                    sp.edit().clear().apply();

                    Toast.makeText(getContext(), "Logged out", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(getActivity(), Login_Page.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);

                    getActivity().finish();
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void toggle(View view){
        view.setVisibility(view.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
    }

    // 🔥 LOAD DATA FROM FIRESTORE
    private void loadProfile() {

        progressBar.setVisibility(View.VISIBLE);

        db.collection("users")
                .document(userId)
                .get()
                .addOnSuccessListener(doc -> {
                    progressBar.setVisibility(View.GONE);

                    if (doc.exists()) {
                        etNgoName.setText(doc.getString("name"));
                        etRegNo.setText(doc.getString("registrationNumber"));
                        etAddress.setText(doc.getString("address"));

                        etPhone.setText(doc.getString("phone"));
                        etEmail.setText(doc.getString("email"));
                    }
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "Failed to load", Toast.LENGTH_SHORT).show();
                });
    }

    // 🔥 UPDATE NGO DETAILS
    private void updateNgoDetails() {

        String name = etNgoName.getText().toString().trim();
        String regNo = etRegNo.getText().toString().trim();
        String address = etAddress.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            etNgoName.setError("Required");
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        HashMap<String, Object> map = new HashMap<>();
        map.put("ngoName", name);
        map.put("registrationNumber", regNo);
        map.put("address", address);

        db.collection("users")
                .document(userId)
                .update(map)
                .addOnSuccessListener(unused -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "Updated successfully", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "Update failed", Toast.LENGTH_SHORT).show();
                });
    }

    // 🔥 UPDATE CONTACT
    private void updateContact() {

        String phone = etPhone.getText().toString().trim();
        String email = etEmail.getText().toString().trim();

        progressBar.setVisibility(View.VISIBLE);

        HashMap<String, Object> map = new HashMap<>();
        map.put("phone", phone);
        map.put("email", email);

        db.collection("users")
                .document(userId)
                .update(map)
                .addOnSuccessListener(unused -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "Contact updated", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "Update failed", Toast.LENGTH_SHORT).show();
                });
    }
}