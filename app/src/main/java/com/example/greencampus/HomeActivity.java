package com.example.greencampus;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;

public class HomeActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener{



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent intent = getIntent();
        String role = intent.getStringExtra("role");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //getting bottom navigation view and attaching the listener
        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(this);

        if(role.equals("0")||role.equals("1")) {
            navigation.getMenu().removeItem(R.id.navigation_administrator_light);
            navigation.getMenu().removeItem(R.id.navigation_administrator_report);
            loadFragment(new LightFragment());
        }
        else{
            navigation.getMenu().removeItem(R.id.navigation_light);
            navigation.getMenu().removeItem(R.id.navigation_report);
            navigation.getMenu().removeItem(R.id.navigation_profile);
            loadFragment(new AdministratorLightFragment());
        }

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        Fragment fragment = null;
        Intent intent = getIntent();
        String role = intent.getStringExtra("role");

        if(role.equals("0")||role.equals("1")) {
            switch (menuItem.getItemId()) {
                case R.id.navigation_light:
                    fragment = new LightFragment();
                    break;

                case R.id.navigation_profile:
                    fragment = new ProfileFragment();
                    break;

                case R.id.navigation_report:
                    fragment = new ReportFragment();
                    break;
            }
        }
        else{
            switch (menuItem.getItemId()) {
                case R.id.navigation_administrator_light:
                    fragment = new AdministratorLightFragment();
                    break;

                case R.id.navigation_administrator_report:
                    fragment = new AdministratorReportFragment();
                    break;
            }
        }
        return loadFragment(fragment);
    }

    private boolean loadFragment(Fragment fragment) {
        //switching fragment
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
            return true;
        }
        return false;
    }
}
