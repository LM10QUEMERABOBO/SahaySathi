package com.example.sahaysathi.ui.volunteer.browseRequests;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sahaysathi.R;
import com.google.firebase.database.*;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class BrowseRequestsFragment extends Fragment {

    private EditText etSearch;
    private RecyclerView recyclerView;
    private TextView tvNoData;

    private EventAdapter adapter;

    private final List<Event> eventList = new ArrayList<>();
    private final List<Event> filteredList = new ArrayList<>();

    // Debounce handler
    private final Handler handler = new Handler(Looper.getMainLooper());
    private Runnable searchRunnable;

    private static final long SEARCH_DELAY = 400; // ms

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_browserequest, container, false);

        initViews(view);
        setupRecyclerView();
        setupSearch();

        loadEvents();

        return view;
    }

    private void initViews(View view) {
        etSearch = view.findViewById(R.id.etSearch);
        recyclerView = view.findViewById(R.id.recyclerEvents);
        tvNoData = view.findViewById(R.id.tvNoEventData);
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new EventAdapter(getContext(), filteredList);
        recyclerView.setAdapter(adapter);
    }

    // 🔍 Debounced Search
    private void setupSearch() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (searchRunnable != null) {
                    handler.removeCallbacks(searchRunnable);
                }

                searchRunnable = () -> filterEvents(s.toString().trim());
                handler.postDelayed(searchRunnable, SEARCH_DELAY);
            }

            @Override
            public void afterTextChanged(Editable s) {}
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

                        if (event != null) {
                            eventList.add(event);
                        }
                    }

                    filterEvents(etSearch.getText().toString().trim());
                })
                .addOnFailureListener(e -> showNoData("Failed to load data"));
    }

    private void filterEvents(String query) {

        filteredList.clear();
        if (query.isEmpty()) {
            filteredList.addAll(eventList);
        } else {
            String lowerQuery = query.toLowerCase();

            for (Event e : eventList) {

                String name = e.getEventName() != null ? e.getEventName().toLowerCase() : "";
                String location = e.getLocation() != null ? e.getLocation().toLowerCase() : "";

                if (name.contains(lowerQuery) || location.contains(lowerQuery)) {
                    filteredList.add(e);
                }
            }
        }

        adapter.notifyDataSetChanged();
        updateUI();
    }

    // 🎯 Handle empty state UI
    private void updateUI() {
        if (filteredList.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            tvNoData.setVisibility(View.VISIBLE);
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        // Prevent memory leak
        if (searchRunnable != null) {
            handler.removeCallbacks(searchRunnable);
        }
    }
}