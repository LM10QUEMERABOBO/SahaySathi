package com.example.sahaysathi.ui.home;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.sahaysathi.ConstantSp;
import com.example.sahaysathi.Forget_Password;
import com.example.sahaysathi.Login_Page;
import com.example.sahaysathi.R;
import com.example.sahaysathi.databinding.FragmentHomeBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.AggregateQuery;
import com.google.firebase.firestore.AggregateQuerySnapshot;
import com.google.firebase.firestore.AggregateSource;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
//import com.example.sahaysathi.ui.ngo.PostRequestFragment;


public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    SharedPreferences sharedPreferences;
    ImageView imageView;
    ProgressBar progressBar;
    TextView requestHelp,offerHelp;
    String role;
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
        requestHelp = view.findViewById(R.id.btn_requestHelp);
        offerHelp = view.findViewById(R.id.btn_offerHelp);
        role = sharedPreferences.getString(ConstantSp.role,"");


        requestHelp.setOnClickListener(v -> new AlertDialog.Builder(getContext())
                .setTitle("Guide to the Request Volunteer Help!")
                .setMessage("This page Help to post the particular request of the volunteer." +
                        "\n Go to home > post request > enter event details " +
                        "\n > enter volunteer requirements \n > submit the request")
                .setPositiveButton("Got It!",null)
                .show());

        offerHelp.setOnClickListener(v -> new AlertDialog.Builder(getContext())
                .setTitle("Guide to the Offer Volunteer Help!")
                .setMessage("This page Help to browse the events based on the filtered requirements entered by the volunteer." +
                        "\n Go to home >browse request > enter filter of the events " +
                        "\n > select the events \n > click on apply > click on submit")
                .setPositiveButton("Got It!",null)
                .show());


        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Query query = db.collection("users").whereEqualTo("role", "volunteer");
        AggregateQuery countQuery = query.count();
        countQuery.get(AggregateSource.SERVER).addOnCompleteListener(new OnCompleteListener<AggregateQuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<AggregateQuerySnapshot> task) {
                if (task.isSuccessful()) {
                    long count = task.getResult().getCount();
                    volunteerCount.setText(count + "\nVolunteers");
                    Log.d("TAG", "Volunteer Count: " +count);
                } else {
                    Log.e("TAG", "Error getting volunteer count", task.getException());
                }
            }
        });

        Query query1 = db.collection("users").whereEqualTo("role", "ngo");
        AggregateQuery countQuery1 = query1.count();
        countQuery1.get(AggregateSource.SERVER).addOnCompleteListener(new OnCompleteListener<AggregateQuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<AggregateQuerySnapshot> task) {
                if (task.isSuccessful()) {
                    long count = task.getResult().getCount();
                    ngoCount.setText(count + "\nNGOs");
                    Log.d("TAG", "Volunteer Count: " +count);
                } else {
                    Log.e("TAG", "Error getting volunteer count", task.getException());
                }
            }
        });
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
}