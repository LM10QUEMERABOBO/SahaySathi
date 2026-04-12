package com.example.sahaysathi.ui.ngo.applicants;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sahaysathi.ConstantSp;
import com.example.sahaysathi.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class ApplicantsFragment extends Fragment {

    RecyclerView recyclerView;
    ApplicantAdapter adapter;
    ArrayList<Applicant> applicantList;

    String ngoId;
    TextView noDataText;
    FirebaseFirestore db;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_applicants, container, false);

        recyclerView = view.findViewById(R.id.recyclerApplicants);
        noDataText = view.findViewById(R.id.noApplicantText);

        SharedPreferences sp = requireActivity()
                .getSharedPreferences(ConstantSp.pref, MODE_PRIVATE);

        ngoId = sp.getString(ConstantSp.userid, "");
        db = FirebaseFirestore.getInstance();

        applicantList = new ArrayList<>();
        adapter = new ApplicantAdapter(getContext(), applicantList);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        noDataText.setVisibility(View.GONE);
        fetchApplicants();

        return view;
    }

    private void fetchApplicants() {

        db.collection("applications")
                .whereEqualTo("ngoId", ngoId)
                .get()
                .addOnSuccessListener(applicationSnapshots -> {

                    applicantList.clear();

                    if (applicationSnapshots.isEmpty()) {
                        noDataText.setVisibility(View.VISIBLE);
                        adapter.notifyDataSetChanged();
                        return;
                    } else {
                        noDataText.setVisibility(View.GONE);
                    }

                    for (DocumentSnapshot appDoc : applicationSnapshots) {

                        String volunteerId = appDoc.getString("userId");
                        String status = appDoc.getString("status");
                        String appId = appDoc.getId();
                        String eventName = appDoc.getString("eventName");
                        String location = appDoc.getString("location");

                        db.collection("users")
                                .document(volunteerId)
                                .get()
                                .addOnSuccessListener(userDoc -> {

                                    if (userDoc.exists()) {

                                        String name = userDoc.getString("name");
                                        String city = userDoc.getString("city");
                                        String skill = userDoc.getString("skill");

                                        applicantList.add(new Applicant(
                                                appId,
                                                volunteerId,
                                                name != null ? name : "N/A",
                                                city != null ? city : "N/A",
                                                skill != null ? skill : "N/A",
                                                status,
                                                eventName != null ? eventName : "N/A",
                                                location != null ? location : "N/A"
                                        ));

                                        adapter.notifyDataSetChanged();
                                    }
                                });
                    }
                })
                .addOnFailureListener(e -> noDataText.setVisibility(View.VISIBLE));
    }
}