package com.example.sahaysathi.ui.volunteer.myTasks;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.sahaysathi.ConstantSp;
import com.example.sahaysathi.R;
import com.example.sahaysathi.databinding.FragmentSlideshowBinding;
import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sahaysathi.ConstantSp;
import com.example.sahaysathi.R;
import com.example.sahaysathi.databinding.FragmentSlideshowBinding;
import com.example.sahaysathi.ui.volunteer.notifications.ApplicationModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class MyTasksFragment extends Fragment {

    RecyclerView recyclerView;
    List<ApplicationModel> list = new ArrayList<>();
    MyTasksAdapter adapter;
    SharedPreferences sharedPreferences;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_mytasks, container, false);

        recyclerView = view.findViewById(R.id.recyclerTasks);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        sharedPreferences = requireActivity().getSharedPreferences(ConstantSp.pref, MODE_PRIVATE);
        adapter = new MyTasksAdapter(getContext(), list);
        recyclerView.setAdapter(adapter);

        loadTasks();

        return view;
    }

    private void loadTasks() {

        String uid = sharedPreferences.getString(ConstantSp.userid, "");

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("applications")
                .whereEqualTo("userId", uid)
                .whereEqualTo("status", "accepted")
                .orderBy("timestamp")
                .addSnapshotListener((value, error) -> {

                    if (error != null) return;

                    list.clear();

                    for (QueryDocumentSnapshot doc : value) {

                        ApplicationModel model = doc.toObject(ApplicationModel.class);

                        // Set document ID if needed
                        model.setApplicationId(doc.getId());

                        list.add(model);
                    }

                    adapter.notifyDataSetChanged();
                });
    }
}