package com.example.sahaysathi.ui.ngo.manageRequests;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sahaysathi.ConstantSp;
import com.example.sahaysathi.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class ManageRequestsFragment extends Fragment {

    RecyclerView recyclerView;
    ArrayList<Event> list;
    EventAdapter adapter;

    TextView noDataText;

    FirebaseFirestore db;
    SharedPreferences sharedPreferences;

    private static final String TAG = "ManageRequests";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_managerequest, container, false);

        recyclerView = view.findViewById(R.id.recyclerEvents);
        noDataText = view.findViewById(R.id.noDataText);

        sharedPreferences = requireActivity().getSharedPreferences(ConstantSp.pref, MODE_PRIVATE);
        db = FirebaseFirestore.getInstance();

        list = new ArrayList<>();

        adapter = new EventAdapter(getContext(), list, event -> {
            Log.d(TAG, "Clicked Event ID: " + event.id);
            // Future: Open Applicants Screen
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        loadData();

        return view;
    }

    private void loadData() {

        String ngoId = sharedPreferences.getString(ConstantSp.userid, "");

        Log.d(TAG, "NGO ID: " + ngoId);

        if (ngoId == null || ngoId.isEmpty()) {
            Log.e(TAG, "NGO ID is empty!");
            noDataText.setVisibility(View.VISIBLE);
            noDataText.setText("Invalid NGO ID");
            return;
        }

        db.collection("ngo_requests")
                .whereEqualTo("ngoId", ngoId)
                .addSnapshotListener((value, error) -> {

                    if (error != null) {
                        Log.e(TAG, "Firestore Error: ", error);
                        return;
                    }

                    if (value == null) {
                        Log.e(TAG, "Snapshot is null");
                        return;
                    }

                    list.clear();

                    for (DocumentSnapshot doc : value.getDocuments()) {

                        Log.d(TAG, "Doc ID: " + doc.getId());

                        String eventDate = doc.getString("date");

                        if (isEventDatePassed(eventDate)) {
                            Log.d(TAG, "Skipped past event: " + doc.getId());
                            continue;
                        }

                        Event event = new Event(
                                doc.getId(),
                                doc.getString("eventName"),
                                doc.getString("location"),
                                eventDate,
                                doc.getLong("appliedCount") != null ? doc.getLong("appliedCount").intValue() : 0,
                                doc.getLong("selectedCount") != null ? doc.getLong("selectedCount").intValue() : 0
                        );

                        list.add(event);
                    }

                    adapter.notifyDataSetChanged();

                    if (list.isEmpty()) {
                        noDataText.setVisibility(View.VISIBLE);
                        noDataText.setText("No Active Requests Available");
                    } else {
                        noDataText.setVisibility(View.GONE);
                    }
                });
    }

    private boolean isEventDatePassed(String eventDate) {
        try {
            if (eventDate == null || eventDate.trim().isEmpty()) {
                return false;
            }

            SimpleDateFormat sdf = new SimpleDateFormat("d/M/yyyy", Locale.getDefault());
            sdf.setLenient(false);

            Date parsedEventDate = sdf.parse(eventDate);
            Date today = sdf.parse(sdf.format(new Date()));

            return parsedEventDate != null && parsedEventDate.before(today);

        } catch (Exception e) {
            Log.e(TAG, "Invalid event date: " + eventDate, e);
            return false;
        }
    }
}