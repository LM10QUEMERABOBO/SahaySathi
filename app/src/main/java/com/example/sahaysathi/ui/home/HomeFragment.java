package com.example.sahaysathi.ui.home;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
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
import com.example.sahaysathi.ConstantSp;
import com.example.sahaysathi.R;
import com.example.sahaysathi.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    SharedPreferences sharedPreferences;
    ProgressBar progressBar;
    ImageView imageView;
    TextView textView;
    @SuppressLint("SetTextI18n")
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        sharedPreferences = getActivity().getSharedPreferences(ConstantSp.pref, MODE_PRIVATE);
        textView = view.findViewById(R.id.main_title);
        textView.setText("Welcome "+sharedPreferences.getString(ConstantSp.name,"")+" \uD83D\uDC4B");
//        progressBar = view.findViewById(R.id.progressBar);
//        imageView= view.findViewById(R.id.fragment_container);
//        showLoading();

        // simulate loading
//        new Handler().postDelayed(this::hideLoading, 1000);
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