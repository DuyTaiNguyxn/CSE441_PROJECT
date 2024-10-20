package com.duytai.cse441_project;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.FrameLayout;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.duytai.cse441_project.fragment.BookTableFragment;
import com.duytai.cse441_project.fragment.CartFragment;
import com.duytai.cse441_project.fragment.CategoryFragment;
import com.duytai.cse441_project.fragment.HomeFragment;
import com.duytai.cse441_project.fragment.ProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeActivity extends AppCompatActivity {
    // Khai báo giao diện
    private FrameLayout frame_Main;
    private FrameLayout fragmentContainerView;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);

        // Gán giao diện
        fragmentContainerView = findViewById(R.id.fragmentContainerView);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        ViewCompat.setOnApplyWindowInsetsListener(bottomNavigationView, (v, insets) -> {
            // Tiêu thụ toàn bộ insets cho BottomNavigationView để tránh tác động lên các view khác
            return WindowInsetsCompat.CONSUMED;
        });

        // Thiết lập sự kiện chọn item cho Bottom Navigation
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int Itemid = item.getItemId();
                if (Itemid == R.id.nav_bottom_home) {
                    loadFragment(new HomeFragment(), false);
                } else if (Itemid == R.id.nav_bottom_directory) {
                    loadFragment(new CategoryFragment(), false);
                } else if (Itemid == R.id.nav_bottom_map) {
                    loadFragment(new BookTableFragment(), false);
                } else if (Itemid == R.id.nav_bottom_cart) {
                    loadFragment(new CartFragment(), false);
                } else {
                    loadFragment(new ProfileFragment(), false);
                }
                return true;
            }
        });

        // Tải HomeFragment làm mặc định khi HomeActivity mở ra
        loadFragment(new HomeFragment(), true);
    }

    // Phương thức loadFragment
    private void loadFragment(Fragment fragment, boolean isAppInitialized) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        if (isAppInitialized) {
            fragmentTransaction.add(R.id.fragmentContainerView, fragment);
        } else {
            fragmentTransaction.replace(R.id.fragmentContainerView, fragment);
        }

        fragmentTransaction.commit();
    }



}
