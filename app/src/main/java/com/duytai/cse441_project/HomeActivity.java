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

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeActivity extends AppCompatActivity {
    // Khai báo giao diện
    private FrameLayout frame_Main;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);

        // Gán giao diện
        frame_Main = findViewById(R.id.frame_Main);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Thiết lập sự kiện chọn item cho Bottom Navigation
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int Itemid = item.getItemId();
                if (Itemid == R.id.nav_bottom_home) {
                    loadFragment(new HomeFragment(), false);
                } else if (Itemid == R.id.nav_bottom_directory) {
                    loadFragment(new DetailProductFragment(), false);
                } else if (Itemid == R.id.nav_bottom_map) {
                    loadFragment(new BookTableFagment(), false);
                } else if (Itemid == R.id.nav_bottom_cart) {
                    loadFragment(new CartFagment(), false);
                } else {
                    loadFragment(new ProfileFagment(), false);
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
            fragmentTransaction.add(R.id.frame_Main, fragment);
        } else {
            fragmentTransaction.replace(R.id.frame_Main, fragment);
        }

        fragmentTransaction.commit();
    }
}
