package com.example.sahaysathi.ui.ngo.profile;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.sahaysathi.ConstantSp;
import com.example.sahaysathi.R;

public class ProfileFragment extends Fragment {

    SharedPreferences sharedPreferences;


    // Dropdown headers
    TextView dropNgoDetails, dropContact;

    // Expandable forms
    LinearLayout formNgoDetails, formContact;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_ngoprofile, container, false);

        sharedPreferences = requireActivity()
                .getSharedPreferences(ConstantSp.pref, MODE_PRIVATE);


        // NGO section
        dropNgoDetails = view.findViewById(R.id.dropNgoDetails);
        formNgoDetails = view.findViewById(R.id.formNgoDetails);

        // Contact section
        dropContact = view.findViewById(R.id.dropContact);
        formContact = view.findViewById(R.id.formContact);

        // NGO details dropdown
        dropNgoDetails.setOnClickListener(v -> toggle(formNgoDetails));

        // Contact dropdown
        dropContact.setOnClickListener(v -> toggle(formContact));

        return view;
    }

    private void toggle(View view){
        if(view.getVisibility() == View.GONE){
            view.setVisibility(View.VISIBLE);
        }else{
            view.setVisibility(View.GONE);
        }
    }
}