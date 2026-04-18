package com.example.sahaysathi.ui.volunteer.profile;

import static android.content.Context.MODE_PRIVATE;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.GradientDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.sahaysathi.ConstantSp;
import com.example.sahaysathi.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ProfileFragment extends Fragment {

    private static final int PICK_IMAGE_REQUEST = 1;

    private ImageView circleImage;
    private SharedPreferences sharedPreferences;
    private FirebaseFirestore db;

    private TextView dropVolunteerDetails, dropContact, dropVolunteerLogo, dropSkills;
    private LinearLayout formVolunteerDetails, formContact, formVolunteerLogo, formSkills;

    private TextView tvProfileStatusBadge;

    private EditText etVolunteerName, etAge, etCity, etAddress, etOccupation, etExperience;
    private EditText etPhone, etEmail, etEmergencyContact;
    private EditText etPreferredWork, etLanguages, etMotivation;

    private Spinner spGender, spAvailability;
    private ChipGroup chipGroupSkills;

    private Button btnUpdateVolunteer, btnUpdateContact, btnUpdateSkills;
    private Button btnUpdateVolunteerLogo, btnSubmitProfile;
    private Button btnUseCurrentLocation, btnOpenInMaps;

    private Uri imageUri;
    private String userId;

    private FusedLocationProviderClient fusedLocationClient;
    private double selectedLatitude = 0.0;
    private double selectedLongitude = 0.0;

    private final String[] genderOptions = {
            "Select Gender", "Male", "Female", "Other", "Prefer not to say"
    };

    private final String[] availabilityOptions = {
            "Select Availability", "Weekdays", "Weekends", "Evenings", "Full Time", "Flexible"
    };

    private final ActivityResultLauncher<String[]> locationPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), result -> {
                boolean fineGranted = Boolean.TRUE.equals(result.get(Manifest.permission.ACCESS_FINE_LOCATION));
                boolean coarseGranted = Boolean.TRUE.equals(result.get(Manifest.permission.ACCESS_COARSE_LOCATION));

                if (fineGranted || coarseGranted) {
                    fetchCurrentLocation();
                } else {
                    Toast.makeText(getContext(), "Location permission denied", Toast.LENGTH_SHORT).show();
                }
            });

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_volunteerprofile, container, false);

        sharedPreferences = requireActivity().getSharedPreferences(ConstantSp.pref, MODE_PRIVATE);
        db = FirebaseFirestore.getInstance();
        userId = sharedPreferences.getString(ConstantSp.userid, "");
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());

        initViews(view);
        setupSpinners();
        setListeners();
        loadProfile();

        return view;
    }

    private void initViews(View view) {
        tvProfileStatusBadge = view.findViewById(R.id.tvProfileStatusBadge);

        dropVolunteerDetails = view.findViewById(R.id.dropVolunteerDetails);
        dropContact = view.findViewById(R.id.dropContact);
        dropVolunteerLogo = view.findViewById(R.id.dropVolunteerLogo);
        dropSkills = view.findViewById(R.id.dropSkills);

        formVolunteerDetails = view.findViewById(R.id.formVolunteerDetails);
        formContact = view.findViewById(R.id.formContact);
        formVolunteerLogo = view.findViewById(R.id.formVolunteerLogo);
        formSkills = view.findViewById(R.id.formSkills);

        etVolunteerName = view.findViewById(R.id.etVolunteerName);
        etAge = view.findViewById(R.id.etAge);
        etCity = view.findViewById(R.id.etCity);
        etAddress = view.findViewById(R.id.etAddress);
        etOccupation = view.findViewById(R.id.etOccupation);
        etExperience = view.findViewById(R.id.etExperience);

        spGender = view.findViewById(R.id.spGender);

        etPhone = view.findViewById(R.id.etPhone);
        etEmail = view.findViewById(R.id.etEmail);
        etEmergencyContact = view.findViewById(R.id.etEmergencyContact);

        chipGroupSkills = view.findViewById(R.id.chipGroupSkills);
        spAvailability = view.findViewById(R.id.spAvailability);
        etPreferredWork = view.findViewById(R.id.etPreferredWork);
        etLanguages = view.findViewById(R.id.etLanguages);
        etMotivation = view.findViewById(R.id.etMotivation);

        btnUpdateVolunteer = view.findViewById(R.id.btnUpdateVolunteer);
        btnUpdateContact = view.findViewById(R.id.btnUpdateContact);
        btnUpdateSkills = view.findViewById(R.id.btnUpdateSkills);
        btnUpdateVolunteerLogo = view.findViewById(R.id.btnUpdateVolunteerLogo);
        btnSubmitProfile = view.findViewById(R.id.btnSubmitProfile);

        btnUseCurrentLocation = view.findViewById(R.id.btnUseCurrentLocation);
        btnOpenInMaps = view.findViewById(R.id.btnOpenInMaps);

        circleImage = view.findViewById(R.id.circleImage);
    }

    private void setupSpinners() {
        ArrayAdapter<String> genderAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                genderOptions
        );
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spGender.setAdapter(genderAdapter);

        ArrayAdapter<String> availabilityAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                availabilityOptions
        );
        availabilityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spAvailability.setAdapter(availabilityAdapter);
    }

    private void setListeners() {
        dropVolunteerDetails.setOnClickListener(v -> toggle(formVolunteerDetails));
        dropContact.setOnClickListener(v -> toggle(formContact));
        dropVolunteerLogo.setOnClickListener(v -> toggle(formVolunteerLogo));
        dropSkills.setOnClickListener(v -> toggle(formSkills));

        btnUpdateVolunteer.setOnClickListener(v -> updateVolunteerDetails());
        btnUpdateContact.setOnClickListener(v -> updateContactDetails());
        btnUpdateSkills.setOnClickListener(v -> updateSkillsDetails());
        btnUpdateVolunteerLogo.setOnClickListener(v -> updateVolunteerLogo());
        btnSubmitProfile.setOnClickListener(v -> submitProfileForReview());

        btnUseCurrentLocation.setOnClickListener(v -> checkLocationPermissionAndFetch());
        btnOpenInMaps.setOnClickListener(v -> openLocationInGoogleMaps());

        circleImage.setOnClickListener(v -> openImagePicker());
    }

    private void toggle(View view) {
        view.setVisibility(view.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
    }

    private void loadProfile() {
        db.collection("users")
                .document(userId)
                .get()
                .addOnSuccessListener(this::fillProfileData)
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Failed to load profile", Toast.LENGTH_SHORT).show());
    }

    private void fillProfileData(DocumentSnapshot doc) {
        if (!doc.exists()) return;

        etVolunteerName.setText(doc.getString("name"));
        etAge.setText(doc.getString("age"));
        etCity.setText(doc.getString("city"));
        etAddress.setText(doc.getString("address"));
        etOccupation.setText(doc.getString("occupation"));
        etExperience.setText(doc.getString("experience"));

        etPhone.setText(doc.getString("phone"));
        etEmail.setText(doc.getString("email"));
        etEmergencyContact.setText(doc.getString("emergencyContact"));

        etPreferredWork.setText(doc.getString("preferredWork"));
        etLanguages.setText(doc.getString("languages"));
        etMotivation.setText(doc.getString("motivation"));

        Double lat = doc.getDouble("latitude");
        Double lng = doc.getDouble("longitude");
        if (lat != null) selectedLatitude = lat;
        if (lng != null) selectedLongitude = lng;

        setSpinnerSelection(spGender, genderOptions, doc.getString("gender"));
        setSpinnerSelection(spAvailability, availabilityOptions, doc.getString("availability"));

        setSelectedSkills(doc.getString("skills"));

        String status = doc.getString("profileStatus");
        setProfileStatus(status);

        String base64Image = doc.getString("profileImage");
        if (base64Image != null && !base64Image.isEmpty()) {
            try {
                byte[] bytes = Base64.decode(base64Image, Base64.DEFAULT);
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                circleImage.setImageBitmap(bitmap);
            } catch (Exception ignored) {
            }
        }
    }

    private void setSpinnerSelection(Spinner spinner, String[] options, String value) {
        if (value == null) return;

        for (int i = 0; i < options.length; i++) {
            if (options[i].equalsIgnoreCase(value)) {
                spinner.setSelection(i);
                return;
            }
        }
    }

    private void setSelectedSkills(String skillsString) {
        if (skillsString == null || skillsString.trim().isEmpty()) return;

        String[] savedSkills = skillsString.split(",");
        for (String savedSkill : savedSkills) {
            String trimmed = savedSkill.trim();
            for (int i = 0; i < chipGroupSkills.getChildCount(); i++) {
                View child = chipGroupSkills.getChildAt(i);
                if (child instanceof Chip) {
                    Chip chip = (Chip) child;
                    if (chip.getText().toString().equalsIgnoreCase(trimmed)) {
                        chip.setChecked(true);
                    }
                }
            }
        }
    }

    private void setProfileStatus(String status) {
        if (getContext() == null) return;

        if (status == null || status.isEmpty()) {
            setStatusBadge("Not Submitted", R.color.orange);
            return;
        }

        switch (status) {
            case "pending":
                setStatusBadge("Pending Review", R.color.orange);
                break;
            case "approved":
                setStatusBadge("Accepted", R.color.green);
                break;
            case "rejected":
                setStatusBadge("Rejected", R.color.red);
                break;
            default:
                setStatusBadge(status, R.color.orange);
                break;
        }
    }

    private void setStatusBadge(String text, int colorRes) {
        tvProfileStatusBadge.setText(text);

        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.RECTANGLE);
        drawable.setCornerRadius(50f);
        drawable.setColor(ContextCompat.getColor(requireContext(), colorRes));

        tvProfileStatusBadge.setBackground(drawable);
        tvProfileStatusBadge.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.white));
    }

    private void checkLocationPermissionAndFetch() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            fetchCurrentLocation();
        } else {
            locationPermissionLauncher.launch(new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            });
        }
    }

    private void fetchCurrentLocation() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(location -> {
                    if (location == null) {
                        Toast.makeText(getContext(), "Unable to get current location", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    selectedLatitude = location.getLatitude();
                    selectedLongitude = location.getLongitude();

                    getAddressFromLatLng(selectedLatitude, selectedLongitude);
                })
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Failed to fetch location", Toast.LENGTH_SHORT).show());
    }

    private void getAddressFromLatLng(double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(requireContext(), Locale.getDefault());

        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);

            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);

                String fullAddress = address.getAddressLine(0);
                String city = address.getLocality();

                if (TextUtils.isEmpty(city)) {
                    city = address.getSubAdminArea();
                }
                if (TextUtils.isEmpty(city)) {
                    city = address.getAdminArea();
                }

                etAddress.setText(fullAddress != null ? fullAddress : "");
                etCity.setText(city != null ? city : "");

                Toast.makeText(getContext(), "Location added successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Address not found", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            Toast.makeText(getContext(), "Geocoder failed", Toast.LENGTH_SHORT).show();
        }
    }

    private void openLocationInGoogleMaps() {
        if (selectedLatitude == 0.0 && selectedLongitude == 0.0) {
            Toast.makeText(getContext(), "No saved location found. Please use current location first.", Toast.LENGTH_SHORT).show();
            return;
        }

        Uri gmmIntentUri = Uri.parse("geo:" + selectedLatitude + "," + selectedLongitude +
                "?q=" + selectedLatitude + "," + selectedLongitude + "(Selected Location)");

        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");

        if (mapIntent.resolveActivity(requireActivity().getPackageManager()) != null) {
            startActivity(mapIntent);
        } else {
            startActivity(new Intent(Intent.ACTION_VIEW, gmmIntentUri));
        }
    }

    private void updateVolunteerDetails() {
        if (!validatePersonalSection()) return;

        Map<String, Object> map = new HashMap<>();
        map.put("name", etVolunteerName.getText().toString().trim());
        map.put("age", etAge.getText().toString().trim());
        map.put("gender", getSelectedSpinnerValue(spGender));
        map.put("city", etCity.getText().toString().trim());
        map.put("address", etAddress.getText().toString().trim());
        map.put("occupation", etOccupation.getText().toString().trim());
        map.put("experience", etExperience.getText().toString().trim());
        map.put("latitude", selectedLatitude);
        map.put("longitude", selectedLongitude);

        db.collection("users")
                .document(userId)
                .update(map)
                .addOnSuccessListener(unused ->
                        Toast.makeText(getContext(), "Personal details saved", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Failed to save personal details", Toast.LENGTH_SHORT).show());
    }

    private void updateContactDetails() {
        if (!validateContactSection()) return;

        Map<String, Object> map = new HashMap<>();
        map.put("phone", etPhone.getText().toString().trim());
        map.put("emergencyContact", etEmergencyContact.getText().toString().trim());

        db.collection("users")
                .document(userId)
                .update(map)
                .addOnSuccessListener(unused ->
                        Toast.makeText(getContext(), "Contact details saved", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Failed to save contact details", Toast.LENGTH_SHORT).show());
    }

    private void updateSkillsDetails() {
        if (!validateSkillsSection()) return;

        Map<String, Object> map = new HashMap<>();
        map.put("skills", getSelectedSkillsText());
        map.put("availability", getSelectedSpinnerValue(spAvailability));
        map.put("preferredWork", etPreferredWork.getText().toString().trim());
        map.put("languages", etLanguages.getText().toString().trim());
        map.put("motivation", etMotivation.getText().toString().trim());

        db.collection("users")
                .document(userId)
                .update(map)
                .addOnSuccessListener(unused ->
                        Toast.makeText(getContext(), "Skills details saved", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Failed to save skills details", Toast.LENGTH_SHORT).show());
    }

    private void submitProfileForReview() {
        if (!validateAllSections()) return;

        Map<String, Object> map = new HashMap<>();
        map.put("name", etVolunteerName.getText().toString().trim());
        map.put("age", etAge.getText().toString().trim());
        map.put("gender", getSelectedSpinnerValue(spGender));
        map.put("city", etCity.getText().toString().trim());
        map.put("address", etAddress.getText().toString().trim());
        map.put("occupation", etOccupation.getText().toString().trim());
        map.put("experience", etExperience.getText().toString().trim());
        map.put("latitude", selectedLatitude);
        map.put("longitude", selectedLongitude);

        map.put("phone", etPhone.getText().toString().trim());
        map.put("email", etEmail.getText().toString().trim());
        map.put("emergencyContact", etEmergencyContact.getText().toString().trim());

        map.put("skills", getSelectedSkillsText());
        map.put("availability", getSelectedSpinnerValue(spAvailability));
        map.put("preferredWork", etPreferredWork.getText().toString().trim());
        map.put("languages", etLanguages.getText().toString().trim());
        map.put("motivation", etMotivation.getText().toString().trim());

        map.put("profileStatus", "pending");
        map.put("profileSubmittedAt", System.currentTimeMillis());

        db.collection("users")
                .document(userId)
                .update(map)
                .addOnSuccessListener(unused -> {
                    setProfileStatus("pending");
                    createNotification(
                            "Profile Under Review",
                            "Your profile has been submitted successfully. It will be reviewed by our background team within 24-48 hours."
                    );
                    Toast.makeText(getContext(), "Profile submitted for review", Toast.LENGTH_LONG).show();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Failed to submit profile", Toast.LENGTH_SHORT).show());
    }

    private boolean validateAllSections() {
        return validatePersonalSection() && validateContactSection() && validateSkillsSection();
    }

    private boolean validatePersonalSection() {
        String name = etVolunteerName.getText().toString().trim();
        String age = etAge.getText().toString().trim();
        String city = etCity.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            formVolunteerDetails.setVisibility(View.VISIBLE);
            etVolunteerName.setError("Full name is required");
            etVolunteerName.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(age)) {
            formVolunteerDetails.setVisibility(View.VISIBLE);
            etAge.setError("Age is required");
            etAge.requestFocus();
            return false;
        }

        try {
            int ageValue = Integer.parseInt(age);
            if (ageValue < 16 || ageValue > 100) {
                formVolunteerDetails.setVisibility(View.VISIBLE);
                etAge.setError("Enter a valid age");
                etAge.requestFocus();
                return false;
            }
        } catch (Exception e) {
            formVolunteerDetails.setVisibility(View.VISIBLE);
            etAge.setError("Enter valid age");
            etAge.requestFocus();
            return false;
        }

        if (spGender.getSelectedItemPosition() == 0) {
            formVolunteerDetails.setVisibility(View.VISIBLE);
            Toast.makeText(getContext(), "Please select gender", Toast.LENGTH_SHORT).show();
            spGender.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(city)) {
            formVolunteerDetails.setVisibility(View.VISIBLE);
            etCity.setError("City is required");
            etCity.requestFocus();
            return false;
        }

        return true;
    }

    private boolean validateContactSection() {
        String phone = etPhone.getText().toString().trim();
        String emergencyContact = etEmergencyContact.getText().toString().trim();

        if (TextUtils.isEmpty(phone) || phone.length() != 10) {
            formContact.setVisibility(View.VISIBLE);
            etPhone.setError("Enter valid 10-digit phone number");
            etPhone.requestFocus();
            return false;
        }

        if (!TextUtils.isEmpty(emergencyContact) && emergencyContact.length() != 10) {
            formContact.setVisibility(View.VISIBLE);
            etEmergencyContact.setError("Enter valid 10-digit emergency contact");
            etEmergencyContact.requestFocus();
            return false;
        }

        return true;
    }

    private boolean validateSkillsSection() {
        String selectedSkills = getSelectedSkillsText();
        String motivation = etMotivation.getText().toString().trim();

        if (TextUtils.isEmpty(selectedSkills)) {
            formSkills.setVisibility(View.VISIBLE);
            Toast.makeText(getContext(), "Please select at least one skill", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (spAvailability.getSelectedItemPosition() == 0) {
            formSkills.setVisibility(View.VISIBLE);
            Toast.makeText(getContext(), "Please select availability", Toast.LENGTH_SHORT).show();
            spAvailability.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(motivation)) {
            formSkills.setVisibility(View.VISIBLE);
            etMotivation.setError("Please tell us why you want to volunteer");
            etMotivation.requestFocus();
            return false;
        }

        return true;
    }

    private String getSelectedSpinnerValue(Spinner spinner) {
        return spinner.getSelectedItem().toString();
    }

    private String getSelectedSkillsText() {
        List<String> selectedSkills = new ArrayList<>();

        for (int i = 0; i < chipGroupSkills.getChildCount(); i++) {
            View child = chipGroupSkills.getChildAt(i);
            if (child instanceof Chip) {
                Chip chip = (Chip) child;
                if (chip.isChecked()) {
                    selectedSkills.add(chip.getText().toString());
                }
            }
        }

        return TextUtils.join(", ", selectedSkills);
    }

    private void createNotification(String title, String message) {
        Map<String, Object> notificationMap = new HashMap<>();
        notificationMap.put("userId", userId);
        notificationMap.put("title", title);
        notificationMap.put("message", message);
        notificationMap.put("timestamp", System.currentTimeMillis());
        notificationMap.put("read", false);

        db.collection("notifications").add(notificationMap);
    }

    private void updateVolunteerLogo() {
        if (circleImage.getDrawable() == null) {
            Toast.makeText(getContext(), "Select image first", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            byte[] imageBytes = imageViewToByte(circleImage);
            String base64Image = Base64.encodeToString(imageBytes, Base64.DEFAULT);

            Map<String, Object> map = new HashMap<>();
            map.put("profileImage", base64Image);

            db.collection("users")
                    .document(userId)
                    .update(map)
                    .addOnSuccessListener(unused ->
                            Toast.makeText(getContext(), "Profile photo saved", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e ->
                            Toast.makeText(getContext(), "Failed to save photo", Toast.LENGTH_SHORT).show());

        } catch (Exception e) {
            Toast.makeText(getContext(), "Image conversion failed", Toast.LENGTH_SHORT).show();
        }
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST &&
                resultCode == Activity.RESULT_OK &&
                data != null &&
                data.getData() != null) {

            imageUri = data.getData();
            circleImage.setImageURI(imageUri);
        }
    }

    private byte[] imageViewToByte(ImageView imageView) {
        Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream);
        return stream.toByteArray();
    }
}