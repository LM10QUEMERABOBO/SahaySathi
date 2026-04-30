package com.example.sahaysathi.ui.volunteer.notifications;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sahaysathi.ConstantSp;
import com.example.sahaysathi.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NotificationsFragment extends Fragment {

    RecyclerView recyclerView;
    List<ApplicationModel> list = new ArrayList<>();
    NotificationAdapter adapter;
    SharedPreferences sharedPreferences;

    private static final String DATE_FORMAT = "d/M/yyyy";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_notifications, container, false);

        recyclerView = view.findViewById(R.id.recyclerNotifications);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        sharedPreferences = requireActivity().getSharedPreferences(ConstantSp.pref, MODE_PRIVATE);

        adapter = new NotificationAdapter(getContext(), list);
        recyclerView.setAdapter(adapter);

        loadData();

        return view;
    }

    private void loadData() {

        String uid = sharedPreferences.getString(ConstantSp.userid, "");

        if (TextUtils.isEmpty(uid) && FirebaseAuth.getInstance().getCurrentUser() != null) {
            uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        }

        if (TextUtils.isEmpty(uid)) {
            return;
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("applications")
                .whereEqualTo("userId", uid)
                .addSnapshotListener((value, error) -> {

                    if (error != null || value == null) {
                        return;
                    }

                    list.clear();

                    for (QueryDocumentSnapshot doc : value) {

                        ApplicationModel model = doc.toObject(ApplicationModel.class);
                        model.setApplicationId(doc.getId());

                        if (isEventDatePassed(model.getDate())) {
                            continue;
                        }

                        list.add(model);
                    }

                    adapter.notifyDataSetChanged();
                });
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
}