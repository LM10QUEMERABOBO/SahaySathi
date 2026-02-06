package com.example.sahaysathi.ui.volunteer.profile;

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

public class ProfileFragment extends Fragment {

    private FragmentSlideshowBinding binding;
    SharedPreferences sharedPreferences;
    TextView textView;
    ProgressBar progressBar;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_slideshow, container, false);
        sharedPreferences = getActivity().getSharedPreferences(ConstantSp.pref, MODE_PRIVATE);
        progressBar = view.findViewById(R.id.progressBar);
//        textView = view.findViewById(R.id.text_applicant_fragment);

        showLoading();

        // simulate loading
        new Handler().postDelayed(this::hideLoading, 1500);
        return view;
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
    private void showLoading(){
        progressBar.setVisibility(View.VISIBLE);
        textView.setVisibility(View.GONE);
    }

    private void hideLoading(){
        progressBar.setVisibility(View.GONE);
        textView.setVisibility(View.VISIBLE);
    }
}