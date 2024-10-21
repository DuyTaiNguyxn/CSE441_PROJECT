package com.duytai.cse441_project;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.duytai.cse441_project.fragment.BookTableFragment;
import com.duytai.cse441_project.fragment.CartFragment;
import com.duytai.cse441_project.fragment.CategoryFragment;
import com.duytai.cse441_project.fragment.DetailFoodFragment;
import com.duytai.cse441_project.fragment.HomeFragment;
import com.duytai.cse441_project.fragment.ProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeActivity extends AppCompatActivity {
    // Khai báo giao diện
    private FrameLayout fragmentContainerView;
    private BottomNavigationView bottomNavigationView;
    private ImageButton btn_back_Topnav; // Nút back
    private TextView txt_app_name; // Tên ứng dụng

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);

        // Gán giao diện
        fragmentContainerView = findViewById(R.id.fragmentContainerView);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        btn_back_Topnav = findViewById(R.id.btn_back_Topnav);
        txt_app_name = findViewById(R.id.txt_app_name);

        // Thiết lập tên ứng dụng mặc định là "Chi tiết sản phẩm"
        txt_app_name.setText("Chi tiết sản phẩm");

        // Tiêu thụ toàn bộ insets cho BottomNavigationView để tránh tác động lên các view khác
        ViewCompat.setOnApplyWindowInsetsListener(bottomNavigationView, (v, insets) -> {
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

        // Kiểm tra nếu là DetailFragment thì hiển thị nút back
        if (fragment instanceof DetailFoodFragment) {
            btn_back_Topnav.setVisibility(View.VISIBLE);
            txt_app_name.setText("Chi tiết sản phẩm");
        } else {
            btn_back_Topnav.setVisibility(View.GONE);
        }

        fragmentTransaction.commit();
    }

    // Thiết lập sự kiện khi nhấn nút back
    private void setupBackButton() {
        btn_back_Topnav.setOnClickListener(v -> {
            // Quay lại màn hình trước đó
            getSupportFragmentManager().popBackStack();
        });
    }
}
