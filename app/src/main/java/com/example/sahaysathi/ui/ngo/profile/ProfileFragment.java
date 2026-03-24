package com.example.sahaysathi.ui.ngo.profile;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.sahaysathi.ConstantSp;
import com.example.sahaysathi.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class ProfileFragment extends Fragment {

    SharedPreferences sharedPreferences;
    FirebaseFirestore db;

    // Dropdowns
    TextView dropNgoDetails, dropContact;
    LinearLayout formNgoDetails, formContact;

    // NGO fields
    EditText etNgoName, etRegNo, etAddress;
    Button btnUpdateNgo;

    // Contact fields
    EditText etPhone, etEmail;
    Button btnUpdateContact;

    ProgressBar progressBar;

    String userId;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_ngoprofile, container, false);

        sharedPreferences = requireActivity()
                .getSharedPreferences(ConstantSp.pref, MODE_PRIVATE);

        db = FirebaseFirestore.getInstance();

        userId = sharedPreferences.getString(ConstantSp.userid, "");

        // Dropdowns
        dropNgoDetails = view.findViewById(R.id.dropNgoDetails);
        formNgoDetails = view.findViewById(R.id.formNgoDetails);

        dropContact = view.findViewById(R.id.dropContact);
        formContact = view.findViewById(R.id.formContact);

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