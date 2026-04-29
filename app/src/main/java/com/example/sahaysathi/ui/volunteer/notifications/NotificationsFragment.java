package com.example.sahaysathi.ui.volunteer.notifications;

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

public class NotificationsFragment extends Fragment {

    RecyclerView recyclerView;
    List<ApplicationModel> list = new ArrayList<>();
    NotificationAdapter adapter;
    SharedPreferences sharedPreferences;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_notifications, container, false);

        recyclerView = view.findViewById(R.id.recyclerNotifications);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // SharedPreferences
        sharedPreferences = requireActivity().getSharedPreferences(ConstantSp.pref, MODE_PRIVATE);
        adapter = new NotificationAdapter(getContext(), list);
        recyclerView.setAdapter(adapter);

        loadData();

        return view;
    }

    private void loadData() {

        String uid = sharedPreferences.getString(ConstantSp.userid, "");

        if (uid == null || uid.trim().isEmpty()) {
            if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            }
        }

        if (uid == null || uid.trim().isEmpty()) {
            return;
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("applications")
                .whereEqualTo("userId", uid)
                .addSnapshotListener((value, error) -> {

                    if (error != null) {
                        return;
                    }

                    list.clear();

                    if (value != null) {
                        for (QueryDocumentSnapshot doc : value) {
                            ApplicationModel model = doc.toObject(ApplicationModel.class);
                            model.setApplicationId(doc.getId());
                            list.add(model);
                        }
                    }

                    adapter.notifyDataSetChanged();
                });
    }
}