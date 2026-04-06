package com.example.sahaysathi.ui.volunteer.browseRequests;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sahaysathi.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class BrowseRequestsFragment extends Fragment {

    private EditText etSearch;
    private RecyclerView recyclerView;
    private EventAdapter adapter;

    private List<Event> eventList = new ArrayList<>();
    private List<Event> filteredList = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_browserequest, container, false);

        etSearch = view.findViewById(R.id.etSearch);
        recyclerView = view.findViewById(R.id.recyclerEvents);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new EventAdapter(getContext(), filteredList);
        recyclerView.setAdapter(adapter);

        loadEvents();
        setupSearch();

        return view;
    }

    private void setupSearch() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterEvents(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void loadEvents() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("ngo_requests");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                eventList.clear();

                for (DataSnapshot data : snapshot.getChildren()) {
                    Event event = data.getValue(Event.class);
                    if (event != null) {
                        eventList.add(event);
                    }
                }

                filteredList.clear();
                filteredList.addAll(eventList);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void filterEvents(String text) {
        List<Event> temp = new ArrayList<>();

        for (Event e : eventList) {
            if (e.getCity() != null && e.getTitle() != null) {
                if (e.getCity().toLowerCase().contains(text.toLowerCase()) ||
                        e.getTitle().toLowerCase().contains(text.toLowerCase())) {
                    temp.add(e);
                }
            }
        }

        adapter.filterList(temp);
    }
}
