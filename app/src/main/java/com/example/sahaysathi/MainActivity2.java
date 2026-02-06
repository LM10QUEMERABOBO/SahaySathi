package com.example.sahaysathi;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.sahaysathi.databinding.ActivityMain2Binding;
import com.google.android.material.navigation.NavigationView;

public class MainActivity2 extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMain2Binding binding;

    TextView email, name;
    SharedPreferences sharedPreferences;

    NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMain2Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Toolbar toolbar = binding.appBarMain.toolbar;
        setSupportActionBar(toolbar);

        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;

        sharedPreferences = getSharedPreferences(ConstantSp.pref, MODE_PRIVATE);
        String role = sharedPreferences.getString(ConstantSp.role, "");

        // Get NavHostFragment safely
        NavHostFragment navHostFragment =
                (NavHostFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.nav_host_fragment_content_main);

        navController = navHostFragment.getNavController();

        navigationView.getMenu().clear();

        // Load menu + navigation graph based on role
        if ("VOLUNTEER".equals(role)) {

            navigationView.inflateMenu(R.menu.menu_volunteer);

            navController.setGraph(R.navigation.navigation_volunteer);

            mAppBarConfiguration = new AppBarConfiguration.Builder(
                    R.id.nav_volunteer_home
            ).setOpenableLayout(drawer).build();

        } else if ("NGO".equals(role)) {

            navigationView.inflateMenu(R.menu.menu_ngo);

            navController.setGraph(R.navigation.navigation_ngo);

            mAppBarConfiguration = new AppBarConfiguration.Builder(
                    R.id.nav_ngo_home
            ).setOpenableLayout(drawer).build();

        } else {

            // fallback
            navigationView.inflateMenu(R.menu.menu_volunteer);

            navController.setGraph(R.navigation.navigation_volunteer);

            mAppBarConfiguration = new AppBarConfiguration.Builder(
                    R.id.nav_volunteer_home
            ).setOpenableLayout(drawer).build();
        }

        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main_activity2, menu);

        sharedPreferences = getSharedPreferences(ConstantSp.pref, MODE_PRIVATE);

        email = findViewById(R.id.dashboard_email);
        name = findViewById(R.id.dashboard_name);

        email.setText(sharedPreferences.getString(ConstantSp.email, ""));
        name.setText(sharedPreferences.getString(ConstantSp.name, ""));

        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {

        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}