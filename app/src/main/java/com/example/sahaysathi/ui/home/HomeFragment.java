package com.example.sahaysathi.ui.home;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.example.sahaysathi.ConstantSp;
import com.example.sahaysathi.R;
import com.example.sahaysathi.databinding.FragmentHomeBinding;
import com.google.firebase.firestore.AggregateSource;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    SharedPreferences sharedPreferences;
    ImageView imageView;
    ProgressBar progressBar;

    TextView textView,volunteerCount,ngoCount,requestCount;
    @SuppressLint("SetTextI18n")
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        sharedPreferences = getActivity().getSharedPreferences(ConstantSp.pref, MODE_PRIVATE);
        textView = view.findViewById(R.id.main_title);
        textView.setText("Welcome "+sharedPreferences.getString(ConstantSp.name,"")+" \uD83D\uDC4B");
        volunteerCount = view.findViewById(R.id.home_volunteer_count);
        ngoCount = view.findViewById(R.id.home_ngo_count);
        requestCount = view.findViewById(R.id.home_request_count);
        FirebaseFirestore db = FirebaseFirestore.getInstance();


        db.collection("users")
                .get()
                .addOnSuccessListener(snapshot -> {

                    int ngo = 0;
                    int volunteer = 0;

                    for (DocumentSnapshot doc : snapshot) {
                        String role = doc.getString("role");

                        Log.d("ROLE_DEBUG", "Role: " + role);

                        if ("ngo".equalsIgnoreCase(role)) {
                            ngo++;
                        } else if ("volunteer".equalsIgnoreCase(role)) {
                            volunteer++;
                        }
                    }

                    ngoCount.setText(ngo + "\nNGOs");
                    volunteerCount.setText(volunteer + "\nVolunteers");
                });
        // REQUEST COUNT
        db.collection("ngo_requests")
                .count()
                .get(AggregateSource.SERVER)
                .addOnSuccessListener(snapshot -> {

                    long request_count = snapshot.getCount();
                    requestCount.setText(request_count + "\nRequests");

                });

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
//    private void showLoading(){
//        progressBar.setVisibility(View.VISIBLE);
//        imageView.setVisibility(View.GONE);
//        textView.setVisibility(View.GONE);
//    }
//
//    private void hideLoading(){
//        progressBar.setVisibility(View.GONE);
//        imageView.setVisibility(View.VISIBLE);
//        textView.setVisibility(View.VISIBLE);
//    }
}