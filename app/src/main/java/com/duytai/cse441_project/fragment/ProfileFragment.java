package com.duytai.cse441_project.fragment;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.duytai.cse441_project.MainActivity;
import com.duytai.cse441_project.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileFragment extends Fragment {

    private TextView tvUserName, tvUserPhone, tvUserAddress, tvUserPassword;
    private ImageView ivUserAvatar;
    private Button btnEditInfo, btnLogOut;
    private SharedPreferences sharedPreferences;
    private FrameLayout fragmentContainerView;
    private BottomNavigationView bottomNavigationView;
    private ImageButton btn_back_Topnav,btn_view_order_detail; // Nút back
    private TextView txt_app_name; // Tên ứng dụng


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Khởi tạo các view
        initializeViews(view);

        // Lấy SharedPreferences để truy xuất userId
        sharedPreferences = requireActivity().getSharedPreferences("currentUserId", MODE_PRIVATE);
        int userId = sharedPreferences.getInt("userId", -1); // Lấy userId, nếu không có thì là -1

        // Nếu userId hợp lệ, truy vấn dữ liệu từ Firebase
        if (userId != -1) {
            loadUserProfile(userId);
        } else {
            Toast.makeText(getActivity(), "Không tìm thấy userId", Toast.LENGTH_SHORT).show();
        }

        // Xử lý sự kiện khi nhấn nút đăng xuất
        btnLogOut.setOnClickListener(view1 -> showLogoutConfirmation());
        btnEditInfo.setOnClickListener(view12 -> loadEditor(new EditProfileFragment(), false));

        return view;
    }

    private void initializeViews(View view) {
        tvUserName = view.findViewById(R.id.tv_user_name);
        tvUserPhone = view.findViewById(R.id.tv_user_phone);
        tvUserAddress = view.findViewById(R.id.tv_user_address);
        //tvUserPassword = view.findViewById(R.id.tv_user_password);
        //ivUserAvatar = view.findViewById(R.id.iv_user_avatar);
        btnEditInfo = view.findViewById(R.id.btn_edit_info);
        btnLogOut = view.findViewById(R.id.btn_log_out);
    }

    private void loadEditor(Fragment fragment, boolean isAppInitialized) {
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        // Cập nhật tiêu đề ứng dụng
        if (fragment instanceof EditProfileFragment) {
            requireActivity().setTitle("Cập nhật thông tin cá nhân!");
        } else {
            requireActivity().setTitle("Thông tin cá nhân!");
        }


        // Thay thế hoặc thêm fragment trong FrameLayout với ID container của nó
        if (isAppInitialized) {
            fragmentTransaction.add(R.id.fragmentContainerView, fragment);
        } else {
            fragmentTransaction.replace(R.id.fragmentContainerView, fragment);
        }

        fragmentTransaction.addToBackStack(null); // Thêm vào back stack
        fragmentTransaction.commit();
    }


    // Phương thức để tải thông tin người dùng từ Firebase
    private void loadUserProfile(int userId) {
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("User").child(String.valueOf(userId));

        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // Lấy thông tin từ snapshot
                    String userName = snapshot.child("name").getValue(String.class);
                    String userPhone = snapshot.child("phone").getValue(String.class);
                    String userAddress = snapshot.child("address").getValue(String.class);
                    //String userAvatarUrl = snapshot.child("avatar_img_url").getValue(String.class);

                    // Cập nhật thông tin lên giao diện
                    updateUserInfo(userName, userPhone, userAddress);
                } else {
                    Toast.makeText(getActivity(), "Không tìm thấy thông tin người dùng", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), "Lỗi kết nối đến Firebase", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUserInfo(String userName, String userPhone, String userAddress) {
        tvUserName.setText(userName);
        tvUserPhone.setText(userPhone);
        tvUserAddress.setText(userAddress);
//        tvUserPassword.setText("••••••••"); // Không hiển thị mật khẩu

        // Sử dụng Picasso để tải hình ảnh từ URL
//        if (userAvatarUrl != null) {
//            Picasso.get()
//                    .load(userAvatarUrl) // Tải hình ảnh từ URL
//                    .placeholder(R.drawable.baseline_person_24) // Hình ảnh thay thế
//                    .error(R.drawable.baseline_person_24) // Hình ảnh lỗi
//                    .into(ivUserAvatar); // Đặt hình ảnh vào ImageView
//        } else {
//            ivUserAvatar.setImageResource(R.drawable.baseline_person_24); // Hình ảnh mặc định nếu không có URL
//        }
    }

    // Hiển thị hộp thoại xác nhận đăng xuất
    private void showLogoutConfirmation() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Đăng xuất")
                .setMessage("Bạn có chắc chắn muốn đăng xuất không?")
                .setPositiveButton("Có", (dialog, which) -> logOutUser())
                .setNegativeButton("Không", (dialog, which) -> dialog.dismiss())
                .show();
    }

    // Xử lý đăng xuất
    private void logOutUser() {
        // Xóa userId khỏi SharedPreferences
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("userId");
        editor.apply();

        // Chuyển sang màn hình đăng nhập
        Intent intent = new Intent(getActivity(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        requireActivity().finish(); // Đóng màn hình hiện tại
    }
}
