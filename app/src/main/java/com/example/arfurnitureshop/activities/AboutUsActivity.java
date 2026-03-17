package com.example.arfurnitureshop.activities;

import android.os.Bundle;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.arfurnitureshop.R;
import com.example.arfurnitureshop.utils.MenuHelper;
import com.google.android.material.navigation.NavigationView;

public class AboutUsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);

        DrawerLayout drawerLayout = findViewById(R.id.drawerLayout);
        NavigationView navigationView = findViewById(R.id.navigationView);
        ImageView ivMenu = findViewById(R.id.ivMenu);

        if (drawerLayout != null && navigationView != null && ivMenu != null) {
            MenuHelper.setupMenu(this, drawerLayout, ivMenu, navigationView);
        }
    }
}