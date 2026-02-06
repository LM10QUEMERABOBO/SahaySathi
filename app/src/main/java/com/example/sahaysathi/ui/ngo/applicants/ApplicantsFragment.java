package com.example.sahaysathi.ui.ngo.applicants;

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
import com.example.sahaysathi.ui.ngo.applicants.Applicant;
import java.util.ArrayList;

public class ApplicantsFragment extends Fragment {

    SharedPreferences sharedPreferences;

    ProgressBar progressBar;
    TextView textView;

    RecyclerView recyclerView;
    ApplicantAdapter adapter;
    ArrayList<Applicant> applicantList;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_applicants, container, false);

        sharedPreferences = getActivity().getSharedPreferences(ConstantSp.pref, MODE_PRIVATE);

//        progressBar = view.findViewById(R.id.progressBar);
//        textView = view.findViewById(R.id.text_applicant_fragment);

        recyclerView = view.findViewById(R.id.recyclerApplicants);
        setupRecyclerView();

        return view;
    }

    private void setupRecyclerView() {

        applicantList = new ArrayList<>();

        // Dummy data (later replace with database data)
        applicantList.add(new Applicant("Rahul Sharma", "Surat", "Medical Support"));
        applicantList.add(new Applicant("Priya Patel", "Ahmedabad", "Food Distribution"));
        applicantList.add(new Applicant("Amit Verma", "Vadodara", "Event Management"));

        adapter = new ApplicantAdapter(getContext(), applicantList);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}