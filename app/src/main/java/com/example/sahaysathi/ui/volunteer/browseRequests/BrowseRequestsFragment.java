package com.example.sahaysathi.ui.volunteer.browseRequests;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sahaysathi.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class BrowseRequestsFragment extends Fragment {

    private EditText etSearch;
    private Button btnSearch;
    private TextView tvNoData;
    private RecyclerView recyclerView;

    private EventAdapter adapter;

    private final List<Event> eventList = new ArrayList<>();
    private final List<Event> filteredList = new ArrayList<>();

    private static final String DATE_FORMAT = "d/M/yyyy";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_browserequest, container, false);

        initViews(view);
        setupRecyclerView();
        setupSearchButton();
        loadEvents();

        return view;
    }

    private void initViews(View view) {
        etSearch = view.findViewById(R.id.etSearch);
        btnSearch = view.findViewById(R.id.btnSearch);
        recyclerView = view.findViewById(R.id.recyclerEvents);
        tvNoData = view.findViewById(R.id.tvNoEventData);
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new EventAdapter(getContext(), filteredList);
        recyclerView.setAdapter(adapter);
    }

    private void setupSearchButton() {
        btnSearch.setOnClickListener(v -> {
            hideKeyboard();
            String query = etSearch.getText().toString().trim();
            filterEvents(query);
        });

        etSearch.setOnEditorActionListener((textView, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE) {
                hideKeyboard();
                String query = etSearch.getText().toString().trim();
                filterEvents(query);
                return true;
            }
            return false;
        });
    }

    private void loadEvents() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("ngo_requests")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    eventList.clear();

                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        Event event = doc.toObject(Event.class);

                        if (event == null) {
                            continue;
                        }

                        if (isEventDatePassed(event.getDate())) {
                            continue;
                        }

                        eventList.add(event);
                    }

                    filteredList.clear();
                    filteredList.addAll(eventList);

                    adapter.notifyDataSetChanged();
                    updateUI();
                })
                .addOnFailureListener(e -> showNoData("Failed to load data"));
    }

    private void filterEvents(String query) {
        filteredList.clear();

        if (TextUtils.isEmpty(query)) {
            filteredList.addAll(eventList);
        } else {
            String lowerQuery = query.toLowerCase(Locale.ROOT).trim();

            for (Event e : eventList) {
                String eventName = safeLower(e.getEventName());
                String location = safeLower(e.getLocation());

                String venueName = safeLower(e.getVenueName());
                String fullAddress = safeLower(e.getFullAddress());
                String landmark = safeLower(e.getLandmark());
                String areaSector = safeLower(e.getAreaSector());
                String city = safeLower(e.getCity());
                String state = safeLower(e.getState());
                String pincode = safeLower(e.getPincode());
                String searchableLocation = safeLower(e.getSearchableLocation());

                if (eventName.contains(lowerQuery)
                        || location.contains(lowerQuery)
                        || venueName.contains(lowerQuery)
                        || fullAddress.contains(lowerQuery)
                        || landmark.contains(lowerQuery)
                        || areaSector.contains(lowerQuery)
                        || city.contains(lowerQuery)
                        || state.contains(lowerQuery)
                        || pincode.contains(lowerQuery)
                        || searchableLocation.contains(lowerQuery)) {
                    filteredList.add(e);
                }
            }
        }

        adapter.notifyDataSetChanged();
        updateUI();
    }

    private boolean isEventDatePassed(String eventDate) {
        try {
            if (eventDate == null || eventDate.trim().isEmpty()) {
                return false;
            }

            SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
            sdf.setLenient(false);

            Date parsedEventDate = sdf.parse(eventDate);
            Date today = sdf.parse(sdf.format(new Date()));

            return parsedEventDate != null && parsedEventDate.before(today);

        } catch (Exception e) {
            return false;
        }
    }

    private String safeLower(String value) {
        return value == null ? "" : value.toLowerCase(Locale.ROOT).trim();
    }

    private void updateUI() {
        if (filteredList.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            tvNoData.setVisibility(View.VISIBLE);
            tvNoData.setText("No active events found");
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            tvNoData.setVisibility(View.GONE);
        }
    }

    private void showNoData(String message) {
        recyclerView.setVisibility(View.GONE);
        tvNoData.setVisibility(View.VISIBLE);
        tvNoData.setText(message);
    }

    private void hideKeyboard() {
        if (getActivity() == null) return;

        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        View view = getActivity().getCurrentFocus();

        if (imm != null && view != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}