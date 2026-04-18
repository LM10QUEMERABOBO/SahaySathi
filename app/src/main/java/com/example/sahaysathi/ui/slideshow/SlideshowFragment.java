package com.example.sahaysathi.ui.slideshow;

import static android.content.Context.MODE_PRIVATE;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.sahaysathi.ConstantSp;
import com.example.sahaysathi.Login_Page;
import com.example.sahaysathi.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class SlideshowFragment extends Fragment {

    FirebaseFirestore db;

    TextView dropUserPref, dropAboutUs, dropContactUs;
    LinearLayout formUserPref, formAboutUs, formContactUs;

    String userId;
    TextView tvName;
    Switch switchDarkMode, switchNotifications;
    Button btnLogout, btnContactMail;

    SharedPreferences sp;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_slideshow, container, false);

        tvName = view.findViewById(R.id.tvName);
        switchDarkMode = view.findViewById(R.id.switchDarkMode);
        switchNotifications = view.findViewById(R.id.switchNotifications);
        btnLogout = view.findViewById(R.id.btnLogout);
        btnContactMail = view.findViewById(R.id.btnContactMail);

        dropUserPref = view.findViewById(R.id.dropUserPreference);
        formUserPref = view.findViewById(R.id.formUserPreference);

        dropAboutUs = view.findViewById(R.id.dropAboutUs);
        formAboutUs = view.findViewById(R.id.formAboutUs);

        dropContactUs = view.findViewById(R.id.dropContactUs);
        formContactUs = view.findViewById(R.id.formContactUs);

        sp = requireActivity().getSharedPreferences(ConstantSp.pref, MODE_PRIVATE);
        db = FirebaseFirestore.getInstance();

        userId = sp.getString(ConstantSp.userid, "");

        String name = sp.getString(ConstantSp.name, "User");
        tvName.setText("Hello " + name + " 👋");

        switchDarkMode.setChecked(sp.getBoolean("dark_mode", false));
        switchNotifications.setChecked(sp.getBoolean("notifications", true));

        switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) ->
                sp.edit().putBoolean("dark_mode", isChecked).apply()
        );

        switchNotifications.setOnCheckedChangeListener((buttonView, isChecked) ->
                sp.edit().putBoolean("notifications", isChecked).apply()
        );

        dropUserPref.setOnClickListener(v -> toggle(formUserPref));
        dropAboutUs.setOnClickListener(v -> toggle(formAboutUs));
        dropContactUs.setOnClickListener(v -> toggle(formContactUs));

        btnContactMail.setOnClickListener(v -> openEmailApp());

        btnLogout.setOnClickListener(v -> showLogoutDialog());

        return view;
    }

    private void openEmailApp() {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:sahaysathi.support@gmail.com"));
        intent.putExtra(Intent.EXTRA_SUBJECT, "SahaySathi Support");
        intent.putExtra(Intent.EXTRA_TEXT, "Hello Team,\n\nI would like to contact you regarding:\n");

        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(getContext(), "No email app found", Toast.LENGTH_SHORT).show();
        }
    }

    private void showLogoutDialog() {
        new AlertDialog.Builder(getContext())
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    FirebaseAuth.getInstance().signOut();
                    sp.edit().clear().apply();

                    Toast.makeText(getContext(), "Logged out", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(getActivity(), Login_Page.class);
//                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);

                    requireActivity().finish();
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void toggle(View view) {
        view.setVisibility(view.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
    }
}