package com.example.sahaysathi.ui.ngo.profile;

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

    private TextView dropNgoDetails, dropContact, dropNgoLogo, dropNgoFocus;
    private LinearLayout formNgoDetails, formContact, formNgoLogo, formNgoFocus;

    private TextView tvProfileStatusBadge;

    private EditText etNgoName, etRegNo, etCity, etAddress, etYearEstablished, etTeamSize, etWebsite, etMission;
    private EditText etPhone, etEmail, etAlternatePhone;

    private Spinner spNgoCategory;
    private ChipGroup chipGroupNgoFocus;

    private Button btnUpdateNgo, btnUpdateContact, btnUpdateNgoLogo, btnUpdateNgoFocus, btnSubmitProfile;
    private Button btnUseCurrentLocation, btnOpenInMaps;

    private Uri imageUri;
    private String userId;

    private FusedLocationProviderClient fusedLocationClient;
    private double selectedLatitude = 0.0;
    private double selectedLongitude = 0.0;

    private final String[] ngoCategoryOptions = {
            "Select NGO Category",
            "Education",
            "Healthcare",
            "Environment",
            "Women Empowerment",
            "Child Welfare",
            "Disaster Relief",
            "Animal Welfare",
            "Community Development",
            "Other"
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

        View view = inflater.inflate(R.layout.fragment_ngoprofile, container, false);

        sharedPreferences = requireActivity().getSharedPreferences(ConstantSp.pref, MODE_PRIVATE);
        db = FirebaseFirestore.getInstance();
        userId = sharedPreferences.getString(ConstantSp.userid, "");
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());

        initViews(view);
        setupSpinner();
        setListeners();
        loadProfile();

        return view;
    }

    private void initViews(View view) {
        tvProfileStatusBadge = view.findViewById(R.id.tvProfileStatusBadge);

        dropNgoDetails = view.findViewById(R.id.dropNgoDetails);
        dropContact = view.findViewById(R.id.dropContact);
        dropNgoLogo = view.findViewById(R.id.dropNgoLogo);
        dropNgoFocus = view.findViewById(R.id.dropNgoFocus);

        formNgoDetails = view.findViewById(R.id.formNgoDetails);
        formContact = view.findViewById(R.id.formContact);
        formNgoLogo = view.findViewById(R.id.formNgoLogo);
        formNgoFocus = view.findViewById(R.id.formNgoFocus);

        etNgoName = view.findViewById(R.id.etNgoName);
        etRegNo = view.findViewById(R.id.etRegNo);
        etCity = view.findViewById(R.id.etCity);
        etAddress = view.findViewById(R.id.etAddress);
        etYearEstablished = view.findViewById(R.id.etYearEstablished);
        etTeamSize = view.findViewById(R.id.etTeamSize);
        etWebsite = view.findViewById(R.id.etWebsite);
        etMission = view.findViewById(R.id.etMission);

        etPhone = view.findViewById(R.id.etPhone);
        etEmail = view.findViewById(R.id.etEmail);
        etAlternatePhone = view.findViewById(R.id.etAlternatePhone);

        spNgoCategory = view.findViewById(R.id.spNgoCategory);
        chipGroupNgoFocus = view.findViewById(R.id.chipGroupNgoFocus);

        btnUpdateNgo = view.findViewById(R.id.btnUpdateNgo);
        btnUpdateContact = view.findViewById(R.id.btnUpdateContact);
        btnUpdateNgoLogo = view.findViewById(R.id.btnUpdateNgoLogo);
        btnUpdateNgoFocus = view.findViewById(R.id.btnUpdateNgoFocus);
        btnSubmitProfile = view.findViewById(R.id.btnSubmitProfile);

        btnUseCurrentLocation = view.findViewById(R.id.btnUseCurrentLocation);
        btnOpenInMaps = view.findViewById(R.id.btnOpenInMaps);

        circleImage = view.findViewById(R.id.circleImage);
    }

    private void setupSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                ngoCategoryOptions
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spNgoCategory.setAdapter(adapter);
    }

    private void setListeners() {
        dropNgoDetails.setOnClickListener(v -> toggle(formNgoDetails));
        dropContact.setOnClickListener(v -> toggle(formContact));
        dropNgoLogo.setOnClickListener(v -> toggle(formNgoLogo));
        dropNgoFocus.setOnClickListener(v -> toggle(formNgoFocus));

        btnUpdateNgo.setOnClickListener(v -> updateNgoDetails());
        btnUpdateContact.setOnClickListener(v -> updateContactDetails());
        btnUpdateNgoLogo.setOnClickListener(v -> updateNgoLogo());
        btnUpdateNgoFocus.setOnClickListener(v -> updateNgoFocusAreas());
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

        etNgoName.setText(doc.getString("name"));
        etRegNo.setText(doc.getString("registrationNumber"));
        etCity.setText(doc.getString("city"));
        etAddress.setText(doc.getString("address"));
        etYearEstablished.setText(doc.getString("yearEstablished"));
        etTeamSize.setText(doc.getString("teamSize"));
        etWebsite.setText(doc.getString("website"));
        etMission.setText(doc.getString("mission"));

        etPhone.setText(doc.getString("phone"));
        etEmail.setText(doc.getString("email"));
        etAlternatePhone.setText(doc.getString("alternatePhone"));

        Double lat = doc.getDouble("latitude");
        Double lng = doc.getDouble("longitude");
        if (lat != null) selectedLatitude = lat;
        if (lng != null) selectedLongitude = lng;

        setSpinnerSelection(spNgoCategory, ngoCategoryOptions, doc.getString("ngoCategory"));
        setSelectedFocusAreas(doc.getString("focusAreas"));

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

    private void setSelectedFocusAreas(String focusAreaString) {
        if (focusAreaString == null || focusAreaString.trim().isEmpty()) return;

        String[] savedAreas = focusAreaString.split(",");
        for (String savedArea : savedAreas) {
            String trimmed = savedArea.trim();
            for (int i = 0; i < chipGroupNgoFocus.getChildCount(); i++) {
                View child = chipGroupNgoFocus.getChildAt(i);
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
                "?q=" + selectedLatitude + "," + selectedLongitude + "(NGO Location)");

        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");

        if (mapIntent.resolveActivity(requireActivity().getPackageManager()) != null) {
            startActivity(mapIntent);
        } else {
            startActivity(new Intent(Intent.ACTION_VIEW, gmmIntentUri));
        }
    }

    private void updateNgoDetails() {
        if (!validateNgoSection()) return;

        Map<String, Object> map = new HashMap<>();
        map.put("name", etNgoName.getText().toString().trim());
        map.put("registrationNumber", etRegNo.getText().toString().trim());
        map.put("ngoCategory", getSelectedSpinnerValue(spNgoCategory));
        map.put("city", etCity.getText().toString().trim());
        map.put("address", etAddress.getText().toString().trim());
        map.put("yearEstablished", etYearEstablished.getText().toString().trim());
        map.put("teamSize", etTeamSize.getText().toString().trim());
        map.put("website", etWebsite.getText().toString().trim());
        map.put("mission", etMission.getText().toString().trim());
        map.put("latitude", selectedLatitude);
        map.put("longitude", selectedLongitude);

        db.collection("users")
                .document(userId)
                .update(map)
                .addOnSuccessListener(unused ->
                        Toast.makeText(getContext(), "NGO details saved", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Failed to save NGO details", Toast.LENGTH_SHORT).show());
    }

    private void updateNgoFocusAreas() {
        if (!validateFocusSection()) return;

        Map<String, Object> map = new HashMap<>();
        map.put("focusAreas", getSelectedFocusAreasText());

        db.collection("users")
                .document(userId)
                .update(map)
                .addOnSuccessListener(unused ->
                        Toast.makeText(getContext(), "Focus areas saved", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Failed to save focus areas", Toast.LENGTH_SHORT).show());
    }

    private void updateContactDetails() {
        if (!validateContactSection()) return;

        Map<String, Object> map = new HashMap<>();
        map.put("phone", etPhone.getText().toString().trim());
        map.put("alternatePhone", etAlternatePhone.getText().toString().trim());

        db.collection("users")
                .document(userId)
                .update(map)
                .addOnSuccessListener(unused ->
                        Toast.makeText(getContext(), "Contact details saved", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Failed to save contact details", Toast.LENGTH_SHORT).show());
    }

    private void submitProfileForReview() {
        if (!validateAllSections()) return;

        Map<String, Object> map = new HashMap<>();
        map.put("name", etNgoName.getText().toString().trim());
        map.put("registrationNumber", etRegNo.getText().toString().trim());
        map.put("ngoCategory", getSelectedSpinnerValue(spNgoCategory));
        map.put("city", etCity.getText().toString().trim());
        map.put("address", etAddress.getText().toString().trim());
        map.put("yearEstablished", etYearEstablished.getText().toString().trim());
        map.put("teamSize", etTeamSize.getText().toString().trim());
        map.put("website", etWebsite.getText().toString().trim());
        map.put("mission", etMission.getText().toString().trim());
        map.put("latitude", selectedLatitude);
        map.put("longitude", selectedLongitude);

        map.put("focusAreas", getSelectedFocusAreasText());

        map.put("phone", etPhone.getText().toString().trim());
        map.put("email", etEmail.getText().toString().trim());
        map.put("alternatePhone", etAlternatePhone.getText().toString().trim());

        map.put("profileStatus", "pending");
        map.put("profileSubmittedAt", System.currentTimeMillis());

        db.collection("users")
                .document(userId)
                .update(map)
                .addOnSuccessListener(unused -> {
                    setProfileStatus("pending");
                    createNotification(
                            "NGO Profile Under Review",
                            "Your NGO profile has been submitted successfully. It will be reviewed by our background team within 24-48 hours."
                    );
                    Toast.makeText(getContext(), "NGO profile submitted for review", Toast.LENGTH_LONG).show();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Failed to submit NGO profile", Toast.LENGTH_SHORT).show());
    }

    private boolean validateAllSections() {
        return validateNgoSection() && validateFocusSection() && validateContactSection();
    }

    private boolean validateNgoSection() {
        String name = etNgoName.getText().toString().trim();
        String regNo = etRegNo.getText().toString().trim();
        String city = etCity.getText().toString().trim();
        String yearEstablished = etYearEstablished.getText().toString().trim();
        String mission = etMission.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            formNgoDetails.setVisibility(View.VISIBLE);
            etNgoName.setError("NGO name is required");
            etNgoName.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(regNo)) {
            formNgoDetails.setVisibility(View.VISIBLE);
            etRegNo.setError("Registration number is required");
            etRegNo.requestFocus();
            return false;
        }

        if (spNgoCategory.getSelectedItemPosition() == 0) {
            formNgoDetails.setVisibility(View.VISIBLE);
            Toast.makeText(getContext(), "Please select NGO category", Toast.LENGTH_SHORT).show();
            spNgoCategory.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(city)) {
            formNgoDetails.setVisibility(View.VISIBLE);
            etCity.setError("City is required");
            etCity.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(yearEstablished) || yearEstablished.length() != 4) {
            formNgoDetails.setVisibility(View.VISIBLE);
            etYearEstablished.setError("Enter valid year");
            etYearEstablished.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(mission)) {
            formNgoDetails.setVisibility(View.VISIBLE);
            etMission.setError("Mission is required");
            etMission.requestFocus();
            return false;
        }

        return true;
    }

    private boolean validateFocusSection() {
        String focusAreas = getSelectedFocusAreasText();

        if (TextUtils.isEmpty(focusAreas)) {
            formNgoFocus.setVisibility(View.VISIBLE);
            Toast.makeText(getContext(), "Please select at least one focus area", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private boolean validateContactSection() {
        String phone = etPhone.getText().toString().trim();
        String alternatePhone = etAlternatePhone.getText().toString().trim();

        if (TextUtils.isEmpty(phone) || phone.length() != 10) {
            formContact.setVisibility(View.VISIBLE);
            etPhone.setError("Enter valid 10-digit phone number");
            etPhone.requestFocus();
            return false;
        }

        if (!TextUtils.isEmpty(alternatePhone) && alternatePhone.length() != 10) {
            formContact.setVisibility(View.VISIBLE);
            etAlternatePhone.setError("Enter valid 10-digit alternate number");
            etAlternatePhone.requestFocus();
            return false;
        }

        return true;
    }

    private String getSelectedSpinnerValue(Spinner spinner) {
        return spinner.getSelectedItem().toString();
    }

    private String getSelectedFocusAreasText() {
        List<String> selectedAreas = new ArrayList<>();

        for (int i = 0; i < chipGroupNgoFocus.getChildCount(); i++) {
            View child = chipGroupNgoFocus.getChildAt(i);
            if (child instanceof Chip) {
                Chip chip = (Chip) child;
                if (chip.isChecked()) {
                    selectedAreas.add(chip.getText().toString());
                }
            }
        }

        return TextUtils.join(", ", selectedAreas);
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

    private void updateNgoLogo() {
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
                            Toast.makeText(getContext(), "NGO logo saved", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e ->
                            Toast.makeText(getContext(), "Failed to save NGO logo", Toast.LENGTH_SHORT).show());

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