package com.group6.vietravel.admin.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.group6.vietravel.R;
import com.group6.vietravel.admin.ui.auth.AdminLoginActivity;
import com.group6.vietravel.admin.ui.places.PlaceManagementFragment;
import com.group6.vietravel.admin.ui.reviews.ReviewModerationFragment;
import com.group6.vietravel.admin.ui.users.UserManagementFragment;
import com.group6.vietravel.admin.ui.notifications.NotificationFragment;

public class AdminMainActivity extends AppCompatActivity 
        implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_main);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Load default fragment
        if (savedInstanceState == null) {
            loadFragment(new PlaceManagementFragment());
            navigationView.setCheckedItem(R.id.nav_admin_places);
            getSupportActionBar().setTitle(R.string.admin_place_management);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        Fragment fragment = null;
        String title = "";

        if (id == R.id.nav_admin_places) {
            fragment = new PlaceManagementFragment();
            title = getString(R.string.admin_place_management);
        } else if (id == R.id.nav_admin_reviews) {
            fragment = new ReviewModerationFragment();
            title = getString(R.string.admin_review_moderation);
        } else if (id == R.id.nav_admin_users) {
            fragment = new UserManagementFragment();
            title = getString(R.string.admin_user_management);
        } else if (id == R.id.nav_admin_notifications) {
            fragment = new NotificationFragment();
            title = getString(R.string.admin_notifications);
        } else if (id == R.id.nav_admin_logout) {
            logout();
            return true;
        }

        if (fragment != null) {
            loadFragment(fragment);
            getSupportActionBar().setTitle(title);
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.commit();
    }

    private void logout() {
        FirebaseAuth.getInstance().signOut();
        Toast.makeText(this, "Đã đăng xuất", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, AdminLoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
