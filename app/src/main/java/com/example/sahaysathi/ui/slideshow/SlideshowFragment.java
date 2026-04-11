package com.example.sahaysathi.ui.slideshow;

import static android.content.Context.MODE_PRIVATE;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import com.example.sahaysathi.ConstantSp;
import com.example.sahaysathi.Login_Page;
import com.example.sahaysathi.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class SlideshowFragment extends Fragment {
    FirebaseFirestore db;

    // Dropdowns
    TextView dropUserPref;
    LinearLayout formUserPref;


    String userId;
    TextView tvName;
    Switch switchDarkMode, switchNotifications;
    Button btnLogout;

    SharedPreferences sp;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_slideshow, container, false);
        tvName = view.findViewById(R.id.tvName);
        switchDarkMode = view.findViewById(R.id.switchDarkMode);
        switchNotifications = view.findViewById(R.id.switchNotifications);
        btnLogout = view.findViewById(R.id.btnLogout);
        sp = requireActivity().getSharedPreferences(ConstantSp.pref, MODE_PRIVATE);

        // Load user data
        String name = sp.getString(ConstantSp.name, "User");
        String email = sp.getString(ConstantSp.email, "user@gmail.com");
        tvName.setText("Hello "+name+" \uD83D\uDC4B");
//        tvEmail.setText("Email: " + email);


        // Load switch states
        switchDarkMode.setChecked(sp.getBoolean("dark_mode", false));
        switchNotifications.setChecked(sp.getBoolean("notifications", true));

        // Dark Mode toggle
        switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            sp.edit().putBoolean("dark_mode", isChecked).apply();
        });

        // Notification toggle
        switchNotifications.setOnCheckedChangeListener((buttonView, isChecked) -> {
            sp.edit().putBoolean("notifications", isChecked).apply();
        });

        // Logout button
        btnLogout.setOnClickListener(v -> {
            showLogoutDialog();
        });

        db = FirebaseFirestore.getInstance();

        userId = sp.getString(ConstantSp.userid, "");
        dropUserPref = view.findViewById(R.id.dropUserPreference);
        formUserPref = view.findViewById(R.id.formUserPreference);
        dropUserPref.setOnClickListener(v -> toggle(formUserPref));

        return view;
    }
    private void showLogoutDialog() {
        new AlertDialog.Builder(getContext())
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Yes", (dialog, which) -> {

                    // 🔥 Firebase logout
                    FirebaseAuth.getInstance().signOut();

                    // 🔥 Clear local data
                    sp.edit().clear().apply();

                    Toast.makeText(getContext(), "Logged out", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(getActivity(), Login_Page.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);

                    getActivity().finish();
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void toggle(View view){
        view.setVisibility(view.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
    }

}