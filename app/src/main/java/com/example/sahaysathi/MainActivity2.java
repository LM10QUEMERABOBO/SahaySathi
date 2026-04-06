package com.example.sahaysathi;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.sahaysathi.databinding.ActivityMain2Binding;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity2 extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMain2Binding binding;

    TextView email, name;
    ImageView profileImage;

    SharedPreferences sharedPreferences;
    FirebaseFirestore db;

    NavController navController;

    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMain2Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sharedPreferences = getSharedPreferences(ConstantSp.pref, MODE_PRIVATE);
        db = FirebaseFirestore.getInstance();

        userId = sharedPreferences.getString(ConstantSp.userid, "");

        Toolbar toolbar = binding.appBarMain.toolbar;
        setSupportActionBar(toolbar);

        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;

        // 🔥 HEADER VIEW
        android.view.View headerView = navigationView.getHeaderView(0);

        email = headerView.findViewById(R.id.dashboard_email);
        name = headerView.findViewById(R.id.dashboard_name);
        profileImage = headerView.findViewById(R.id.circleImage1);

        email.setText(sharedPreferences.getString(ConstantSp.email, ""));
        name.setText(sharedPreferences.getString(ConstantSp.name, ""));

        // 🔥 LOAD IMAGE FROM FIRESTORE
        loadProfileImage();

        String role = sharedPreferences.getString(ConstantSp.role, "");

        // Navigation setup
        NavHostFragment navHostFragment =
                (NavHostFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.nav_host_fragment_content_main);

        navController = navHostFragment.getNavController();

        navigationView.getMenu().clear();

        if ("volunteer".equals(role)) {

            navigationView.inflateMenu(R.menu.menu_volunteer);
            navController.setGraph(R.navigation.navigation_volunteer);

            mAppBarConfiguration = new AppBarConfiguration.Builder(
                    R.id.nav_volunteer_home
            ).setOpenableLayout(drawer).build();

        } else if ("ngo".equals(role)) {

            navigationView.inflateMenu(R.menu.menu_ngo);
            navController.setGraph(R.navigation.navigation_ngo);

            mAppBarConfiguration = new AppBarConfiguration.Builder(
                    R.id.nav_ngo_home
            ).setOpenableLayout(drawer).build();

        } else {

            navigationView.inflateMenu(R.menu.menu_volunteer);
            navController.setGraph(R.navigation.navigation_volunteer);

            mAppBarConfiguration = new AppBarConfiguration.Builder(
                    R.id.nav_volunteer_home
            ).setOpenableLayout(drawer).build();
        }

        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
    }

    // 🔥 METHOD TO LOAD IMAGE
    private void loadProfileImage() {

        db.collection("users")
                .document(userId)
                .get()
                .addOnSuccessListener(doc -> {

                    if (doc.exists()) {

                        String base64Image = doc.getString("profileImage");

                        if (base64Image != null) {

                            byte[] bytes = Base64.decode(base64Image, Base64.DEFAULT);
                            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

                            profileImage.setImageBitmap(bitmap);
                        }
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show()
                );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main_activity2, menu);

        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {

        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}