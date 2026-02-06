package com.example.sahaysathi.ui.ngo.manageRequests;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sahaysathi.ConstantSp;
import com.example.sahaysathi.R;
import com.example.sahaysathi.databinding.FragmentSlideshowBinding;

import java.util.ArrayList;

public class ManageRequestsFragment extends Fragment {

    RecyclerView recyclerView;
    ArrayList<Event> list;
    EventAdapter adapter;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_managerequest, container, false);

        recyclerView = view.findViewById(R.id.recyclerEvents);

        list = new ArrayList<>();

        // Dummy Data
        list.add(new Event("Food Donation", "Surat", "12 March", 15, 5));
        list.add(new Event("Medical Camp", "Ahmedabad", "18 March", 20, 10));
        list.add(new Event("Tree Plantation", "Vadodara", "25 March", 12, 6));

        adapter = new EventAdapter(getContext(), list);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        return view;
    }
}