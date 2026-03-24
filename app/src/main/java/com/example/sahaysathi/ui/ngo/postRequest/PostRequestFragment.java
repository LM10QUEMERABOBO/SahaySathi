package com.example.sahaysathi.ui.ngo.postRequest;

import static android.content.Context.MODE_PRIVATE;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
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
import com.example.sahaysathi.databinding.FragmentSlideshowBinding;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;

public class PostRequestFragment extends Fragment {

    SharedPreferences sharedPreferences;

    TextView dropEvent, dropLocation, dropVolunteer, dropMedia, dropDeadline;
    LinearLayout formEvent, formLocation, formVolunteer, formMedia, formDeadline;

    ProgressBar progressBar;

    EditText eventName, eventDescription, eventLocation, eventDate, eventTime,
            volunteerCount, experienceRequired, deadline;

        Button submitRequest;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_postrequest, container, false);

        sharedPreferences = requireActivity().getSharedPreferences(ConstantSp.pref, MODE_PRIVATE);

        // Toggle Views
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

        // Inputs
        eventName = view.findViewById(R.id.eventName);
        eventDescription = view.findViewById(R.id.eventDescription);
        eventLocation = view.findViewById(R.id.eventLocation);
        eventDate = view.findViewById(R.id.eventDate);
        eventTime = view.findViewById(R.id.eventTime);
        volunteerCount = view.findViewById(R.id.volunteerCount);
        experienceRequired = view.findViewById(R.id.experienceRequired);
        deadline = view.findViewById(R.id.deadline);

        submitRequest = view.findViewById(R.id.submitRequest);
        progressBar = view.findViewById(R.id.progressBar);

        // Toggle listeners
        dropEvent.setOnClickListener(v -> toggle(formEvent));
        dropLocation.setOnClickListener(v -> toggle(formLocation));
        dropVolunteer.setOnClickListener(v -> toggle(formVolunteer));
        dropMedia.setOnClickListener(v -> toggle(formMedia));
        dropDeadline.setOnClickListener(v -> toggle(formDeadline));

        eventDate.setOnClickListener(v -> {

            Calendar calendar = Calendar.getInstance();

            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    getContext(),
                    (View, selectedYear, selectedMonth, selectedDay) -> {

                        String date = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                        eventDate.setText(date);

                    },
                    year, month, day
            );

            datePickerDialog.show();
        });

        deadline.setOnClickListener(v -> {

            Calendar calendar = Calendar.getInstance();

            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    getContext(),
                    (View, selectedYear, selectedMonth, selectedDay) -> {

                        String deadlineDate = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                        deadline.setText(deadlineDate);

                    },
                    year, month, day
            );

            datePickerDialog.show();
        });

        eventTime.setOnClickListener(v -> {

            Calendar calendar = Calendar.getInstance();

            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);

            TimePickerDialog timePickerDialog = new TimePickerDialog(
                    getContext(),
                    (View, selectedHour, selectedMinute) -> {

                        String time = selectedHour + ":" + selectedMinute;
                        eventTime.setText(time);

                    },
                    hour,
                    minute,
                    true // true = 24-hour format
            );

            timePickerDialog.show();
        });

        // Submit
        submitRequest.setOnClickListener(v -> submitData());

        return view;
    }

    private void toggle(View v) {
        v.setVisibility(v.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
    }

    private void submitData() {

        String name = eventName.getText().toString().trim();
        String desc = eventDescription.getText().toString().trim();
        String location = eventLocation.getText().toString().trim();
        String date = eventDate.getText().toString().trim();
        String time = eventTime.getText().toString().trim();
        String count = volunteerCount.getText().toString().trim();
        String exp = experienceRequired.getText().toString().trim();
        String deadlineText = deadline.getText().toString().trim();
        String ngoId=sharedPreferences.getString(ConstantSp.userid, "");
        String eventId = ngoId + "_EVENT_" + System.currentTimeMillis();
        if (name.isEmpty() || desc.isEmpty() || location.isEmpty()) {
            Toast.makeText(getContext(), "Fill all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        HashMap<String, Object> map = new HashMap<>();
        map.put("requestId",eventId);
        map.put("eventName", name);
        map.put("description", desc);
        map.put("location", location);
        map.put("date", date);
        map.put("time", time);
        map.put("volunteerCount", count);
        map.put("experience", exp);
        map.put("deadline", deadlineText);
        map.put("ngoId", sharedPreferences.getString(ConstantSp.userid, ""));
        map.put("createdAt", com.google.firebase.firestore.FieldValue.serverTimestamp());

        FirebaseFirestore.getInstance()
                .collection("ngo_requests")
                .add(map)
                .addOnSuccessListener(doc -> {

                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "Request Posted", Toast.LENGTH_SHORT).show();

                })
                .addOnFailureListener(e -> {

                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();

                });
    }
}