package com.example.sahaysathi.ui.volunteer.profile;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.sahaysathi.ConstantSp;
import com.example.sahaysathi.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;

public class ProfileFragment extends Fragment {

    private static final int PICK_IMAGE_REQUEST = 1;

    ImageView circleImage;
    SharedPreferences sharedPreferences;
    FirebaseFirestore db;

    // Dropdowns
    TextView dropVolunteerDetails, dropContact, dropVolunteerLogo;
    LinearLayout formVolunteerDetails, formContact, formVolunteerLogo;

    // Volunteer fields
    EditText etVolunteerName, etRegNo, etAddress;
    Button btnUpdateVolunteer;

    // Contact fields
    EditText etPhone, etEmail;
    Button btnUpdateContact;

    // Logo
    Button btnUpdateVolunteerLogo;
    Uri imageUri;

    String userId;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_volunteerprofile, container, false);

        sharedPreferences = requireActivity()
                .getSharedPreferences(ConstantSp.pref, MODE_PRIVATE);

        db = FirebaseFirestore.getInstance();
        userId = sharedPreferences.getString(ConstantSp.userid, "");

        // Dropdowns
        dropVolunteerDetails = view.findViewById(R.id.dropVolunteerDetails);
        formVolunteerDetails = view.findViewById(R.id.formVolunteerDetails);

        dropContact = view.findViewById(R.id.dropContact);
        formContact = view.findViewById(R.id.formContact);

        dropVolunteerLogo = view.findViewById(R.id.dropVolunteerLogo);
        formVolunteerLogo = view.findViewById(R.id.formVolunteerLogo);

        // Volunteer fields
        etVolunteerName = view.findViewById(R.id.etVolunteerName);
        etRegNo = view.findViewById(R.id.etRegNo);
        etAddress = view.findViewById(R.id.etAddress);
        btnUpdateVolunteer = view.findViewById(R.id.btnUpdateVolunteer);

        // Contact fields
        etPhone = view.findViewById(R.id.etPhone);
        etEmail = view.findViewById(R.id.etEmail);
        btnUpdateContact = view.findViewById(R.id.btnUpdateContact);

        // Logo
        circleImage = view.findViewById(R.id.circleImage);
        btnUpdateVolunteerLogo = view.findViewById(R.id.btnUpdateVolunteerLogo);
        // Toggle dropdowns
        dropVolunteerDetails.setOnClickListener(v -> toggle(formVolunteerDetails));
        dropContact.setOnClickListener(v -> toggle(formContact));
        dropVolunteerLogo.setOnClickListener(v -> toggle(formVolunteerLogo));

        // Load data
        loadProfile();

        // Click listeners
        btnUpdateVolunteer.setOnClickListener(v -> updateVolunteerDetails());
        btnUpdateContact.setOnClickListener(v -> updateContact());
        btnUpdateVolunteerLogo.setOnClickListener(v -> updateVolunteerLogo());

        circleImage.setOnClickListener(v -> openImagePicker());

        return view;
    }

    private void toggle(View view) {
        view.setVisibility(view.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
    }

    // 🔥 LOAD PROFILE
    private void loadProfile() {

        db.collection("users")
                .document(userId)
                .get()
                .addOnSuccessListener(doc -> {

                    if (doc.exists()) {

                        etVolunteerName.setText(doc.getString("name"));
                        etRegNo.setText(doc.getString("registrationNumber"));
                        etAddress.setText(doc.getString("address"));

                        etPhone.setText(doc.getString("phone"));
                        etEmail.setText(doc.getString("email"));

                        // Load Image
                        String base64Image = doc.getString("profileImage");
                        if (base64Image != null) {
                            byte[] bytes = Base64.decode(base64Image, Base64.DEFAULT);
                            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                            circleImage.setImageBitmap(bitmap);
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Failed to load", Toast.LENGTH_SHORT).show();
                });
    }

    // 🔥 UPDATE Volunteer DETAILS
    private void updateVolunteerDetails() {

        String name = etVolunteerName.getText().toString().trim();
        String regNo = etRegNo.getText().toString().trim();
        String address = etAddress.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            etVolunteerName.setError("Required");
            return;
        }

        HashMap<String, Object> map = new HashMap<>();
        map.put("name", name);
        map.put("registrationNumber", regNo);
        map.put("address", address);

        db.collection("users")
                .document(userId)
                .update(map)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(getContext(), "Volunteer updated", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Update failed", Toast.LENGTH_SHORT).show();
                });
    }

    // 🔥 UPDATE CONTACT
    private void updateContact() {

        String phone = etPhone.getText().toString().trim();
        String email = etEmail.getText().toString().trim();

        HashMap<String, Object> map = new HashMap<>();
        map.put("phone", phone);
        map.put("email", email);

        db.collection("users")
                .document(userId)
                .update(map)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(getContext(), "Contact updated", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Update failed", Toast.LENGTH_SHORT).show();
                });
    }

    // 🔥 UPDATE Volunteer LOGO
    private void updateVolunteerLogo() {

        if (circleImage.getDrawable() == null) {
            Toast.makeText(getContext(), "Select image first", Toast.LENGTH_SHORT).show();
            return;
        }

        byte[] imageBytes = imageViewToByte(circleImage);
        String base64Image = Base64.encodeToString(imageBytes, Base64.DEFAULT);

        HashMap<String, Object> map = new HashMap<>();
        map.put("profileImage", base64Image);

        db.collection("users")
                .document(userId)
                .update(map)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(getContext(), "Logo updated", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Logo update failed", Toast.LENGTH_SHORT).show();
                });
    }

    // 🔥 IMAGE PICKER
    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    // 🔥 HANDLE IMAGE RESULT
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            imageUri = data.getData();
            circleImage.setImageURI(imageUri);
        }
    }

    // 🔥 CONVERT IMAGEVIEW TO BYTE[]
    public byte[] imageViewToByte(ImageView imageView) {
        Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream);
        return stream.toByteArray();
    }
}