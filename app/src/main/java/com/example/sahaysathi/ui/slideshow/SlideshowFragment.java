package com.example.sahaysathi.ui.slideshow;

import static android.content.Context.MODE_PRIVATE;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import com.example.sahaysathi.ConstantSp;
import com.example.sahaysathi.Login_Page;
import com.example.sahaysathi.R;

public class SlideshowFragment extends Fragment {

    TextView tvName, tvEmail;
    Switch switchDarkMode, switchNotifications;
    Button btnLogout;

    SharedPreferences sp;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_slideshow, container, false);

        // Initialize Views
        tvName = view.findViewById(R.id.tvName);
        tvEmail = view.findViewById(R.id.tvEmail);
        switchDarkMode = view.findViewById(R.id.switchDarkMode);
        switchNotifications = view.findViewById(R.id.switchNotifications);
        btnLogout = view.findViewById(R.id.btnLogout);

        // SharedPreferences
        sp = requireActivity().getSharedPreferences(ConstantSp.pref, MODE_PRIVATE);

        // Load user data
        String name = sp.getString(ConstantSp.name, "User");
        String email = sp.getString(ConstantSp.email, "user@gmail.com");

        tvName.setText("Name: " + name);
        tvEmail.setText("Email: " + email);

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

        return view;
    }
    private void showLogoutDialog() {
        new AlertDialog.Builder(getContext())
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    Toast.makeText(getContext(), "Logged out", Toast.LENGTH_SHORT).show();
                    sp.edit().clear().apply();
                    startActivity(new Intent(getActivity(), Login_Page.class));
                    getActivity().finish();
                })
                .setNegativeButton("No", null)
                .show();
    }
}