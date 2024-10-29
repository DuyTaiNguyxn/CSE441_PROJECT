package com.duytai.cse441_project.fragment;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.squareup.picasso.Picasso;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.duytai.cse441_project.R;
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Khởi tạo các view
        tvUserName = view.findViewById(R.id.tv_user_name);
        tvUserPhone = view.findViewById(R.id.tv_user_phone);
        tvUserAddress = view.findViewById(R.id.tv_user_address);
        tvUserPassword = view.findViewById(R.id.tv_user_password);
        ivUserAvatar = view.findViewById(R.id.iv_user_avatar);
        btnEditInfo = view.findViewById(R.id.btn_edit_info);
        btnLogOut = view.findViewById(R.id.btn_log_out);

        // Lấy SharedPreferences để truy xuất userId
        sharedPreferences = requireActivity().getSharedPreferences("currentUserId", MODE_PRIVATE);
        int userId = sharedPreferences.getInt("userId", -1); // Lấy userId, nếu không có thì là -1

        // Nếu userId hợp lệ, truy vấn dữ liệu từ Firebase
        if (userId != -1) {
            loadUserProfile(userId);
        } else {
            Toast.makeText(getActivity(), "Không tìm thấy userId", Toast.LENGTH_SHORT).show();
        }

        return view;
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
                    String userPassword = snapshot.child("password").getValue(String.class);
                    String userAvatarUrl = snapshot.child("avatar_img_url").getValue(String.class);

                    // Cập nhật thông tin lên giao diện
                    tvUserName.setText(userName);
                    tvUserPhone.setText(userPhone);
                    tvUserAddress.setText(userAddress);
                    tvUserPassword.setText("••••••••"); // Không hiển thị mật khẩu

                    // Sử dụng Picasso để tải hình ảnh từ URL
                    if (userAvatarUrl != null) {
                        Picasso.get()
                                .load(userAvatarUrl) // Tải hình ảnh từ URL
                                .placeholder(R.drawable.baseline_person_24) // Hình ảnh thay thế
                                .error(R.drawable.baseline_person_24) // Hình ảnh lỗi
                                .into(ivUserAvatar); // Đặt hình ảnh vào ImageView
                    } else {
                        ivUserAvatar.setImageResource(R.drawable.baseline_person_24); // Hình ảnh mặc định nếu không có URL
                    }

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
}
