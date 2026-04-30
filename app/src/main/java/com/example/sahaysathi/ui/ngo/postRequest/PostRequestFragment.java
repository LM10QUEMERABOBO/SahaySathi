package com.example.sahaysathi.ui.ngo.postRequest;

import static android.content.Context.MODE_PRIVATE;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.sahaysathi.ConstantSp;
import com.example.sahaysathi.R;
import com.example.sahaysathi.services.NotificationHelper;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class PostRequestFragment extends Fragment {

    private SharedPreferences sharedPreferences;

    private TextView dropEvent, dropLocation, dropVolunteer, dropMedia, dropDeadline;
    private LinearLayout formEvent, formLocation, formVolunteer, formMedia, formDeadline;

    private ProgressBar progressBar;

    private EditText eventName, eventDescription;
    private EditText venueName, fullAddress, landmark, areaSector, city, state, pincode;
    private EditText eventDate, eventTime, volunteerCount, experienceRequired, deadline;

    private Button submitRequest;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_postrequest, container, false);

        sharedPreferences = requireActivity().getSharedPreferences(ConstantSp.pref, MODE_PRIVATE);

        initViews(view);
        setClickListeners();

        return view;
    }

    private void initViews(View view) {
        dropEvent = view.findViewById(R.id.dropEvent);
        formEvent = view.findViewById(R.id.formEvent);

        dropLocation = view.findViewById(R.id.dropLocation);
        formLocation = view.findViewById(R.id.formLocation);

        dropVolunteer = view.findViewById(R.id.dropVolunteer);
        formVolunteer = view.findViewById(R.id.formVolunteer);

        dropMedia = view.findViewById(R.id.dropMedia);
        formMedia = view.findViewById(R.id.formMedia);

        dropDeadline = view.findViewById(R.id.dropDeadline);
        formDeadline = view.findViewById(R.id.formDeadline);

        eventName = view.findViewById(R.id.eventName);
        eventDescription = view.findViewById(R.id.eventDescription);

        venueName = view.findViewById(R.id.venueName);
        fullAddress = view.findViewById(R.id.fullAddress);
        landmark = view.findViewById(R.id.landmark);
        areaSector = view.findViewById(R.id.areaSector);
        city = view.findViewById(R.id.postCity);
        state = view.findViewById(R.id.state);
        pincode = view.findViewById(R.id.pincode);

        eventDate = view.findViewById(R.id.eventDate);
        eventTime = view.findViewById(R.id.eventTime);
        volunteerCount = view.findViewById(R.id.volunteerCount);
        experienceRequired = view.findViewById(R.id.experienceRequired);
        deadline = view.findViewById(R.id.deadline);

        submitRequest = view.findViewById(R.id.submitRequest);
        progressBar = view.findViewById(R.id.progressBar);
    }

    private void setClickListeners() {
        dropEvent.setOnClickListener(v -> toggle(formEvent));
        dropLocation.setOnClickListener(v -> toggle(formLocation));
        dropVolunteer.setOnClickListener(v -> toggle(formVolunteer));
        dropMedia.setOnClickListener(v -> toggle(formMedia));
        dropDeadline.setOnClickListener(v -> toggle(formDeadline));

        eventDate.setOnClickListener(v -> showDatePicker(eventDate));
        deadline.setOnClickListener(v -> showDatePicker(deadline));
        eventTime.setOnClickListener(v -> showTimePicker());

        submitRequest.setOnClickListener(v -> submitData());
    }

    private void toggle(View view) {
        view.setVisibility(view.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
    }

    private void showDatePicker(EditText targetEditText) {
        Calendar calendar = Calendar.getInstance();

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                requireContext(),
                (datePicker, selectedYear, selectedMonth, selectedDay) -> {
                    String selectedDate = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                    targetEditText.setText(selectedDate);
                },
                year, month, day
        );

        datePickerDialog.show();
    }

    private void showTimePicker() {
        Calendar calendar = Calendar.getInstance();

        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(
                requireContext(),
                (timePicker, selectedHour, selectedMinute) -> {
                    String formattedTime = String.format(Locale.getDefault(), "%02d:%02d", selectedHour, selectedMinute);
                    eventTime.setText(formattedTime);
                },
                hour,
                minute,
                true
        );

        timePickerDialog.show();
    }

    private void submitData() {
        String name = getText(eventName);
        String desc = getText(eventDescription);

        String venue = getText(venueName);
        String address = getText(fullAddress);
        String landmarkText = getText(landmark);
        String areaText = getText(areaSector);
        String cityText = getText(city);
        String stateText = getText(state);
        String pincodeText = getText(pincode);

        String date = getText(eventDate);
        String time = getText(eventTime);
        String count = getText(volunteerCount);
        String exp = getText(experienceRequired);
        String deadlineText = getText(deadline);

        String ngoId = sharedPreferences.getString(ConstantSp.userid, "");
        String eventId = ngoId + "_EVENT_" + System.currentTimeMillis();

        if (name.isEmpty() || desc.isEmpty() || venue.isEmpty() || address.isEmpty() || cityText.isEmpty()) {
            Toast.makeText(getContext(), "Please fill all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        String fullLocation = buildFullLocation(venue, address, landmarkText, areaText, cityText, stateText, pincodeText);
        String searchableLocation = fullLocation.toLowerCase(Locale.ROOT).trim();

        progressBar.setVisibility(View.VISIBLE);
        submitRequest.setEnabled(false);

        Map<String, Object> map = new HashMap<>();
        map.put("requestId", eventId);
        map.put("eventName", name);
        map.put("description", desc);

        // Old combined field kept for compatibility
        map.put("location", fullLocation);

        // New structured location fields
        map.put("venueName", venue);
        map.put("fullAddress", address);
        map.put("landmark", landmarkText);
        map.put("areaSector", areaText);
        map.put("city", cityText);
        map.put("state", stateText);
        map.put("pincode", pincodeText);
        map.put("searchableLocation", searchableLocation);

        map.put("date", date);
        map.put("time", time);
        map.put("volunteerCount", count);
        map.put("experience", exp);
        map.put("deadline", deadlineText);
        map.put("ngoId", ngoId);
        map.put("createdAt", FieldValue.serverTimestamp());

        FirebaseFirestore.getInstance()
                .collection("ngo_requests")
                .add(map)
                .addOnSuccessListener(documentReference -> {
                    progressBar.setVisibility(View.GONE);
                    submitRequest.setEnabled(true);
                    Toast.makeText(getContext(), "Request Posted", Toast.LENGTH_SHORT).show();
                    NotificationHelper.postRequestSuccess(requireContext(), eventName.getText().toString());
                    clearForm();
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    submitRequest.setEnabled(true);
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private String getText(EditText editText) {
        return editText.getText().toString().trim();
    }

    private String buildFullLocation(String venue,
                                     String address,
                                     String landmark,
                                     String area,
                                     String city,
                                     String state,
                                     String pincode) {

        StringBuilder builder = new StringBuilder();

        appendPart(builder, venue);
        appendPart(builder, address);
        appendPart(builder, landmark);
        appendPart(builder, area);
        appendPart(builder, city);
        appendPart(builder, state);
        appendPart(builder, pincode);

        return builder.toString();
    }

    private void appendPart(StringBuilder builder, String value) {
        if (!TextUtils.isEmpty(value)) {
            if (builder.length() > 0) {
                builder.append(", ");
            }
            builder.append(value);
        }
    }

    private void clearForm() {
        eventName.setText("");
        eventDescription.setText("");

        venueName.setText("");
        fullAddress.setText("");
        landmark.setText("");
        areaSector.setText("");
        city.setText("");
        state.setText("");
        pincode.setText("");

        eventDate.setText("");
        eventTime.setText("");
        volunteerCount.setText("");
        experienceRequired.setText("");
        deadline.setText("");
    }
}