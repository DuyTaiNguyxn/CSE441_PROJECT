package com.duytai.cse441_project.fragment;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.duytai.cse441_project.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EditProfileFragment extends Fragment {

    public EditText etUserName, etUserAddress,etUserOldPassword, etUserNewPassword, etUserNewPasswordConfirm; // Thêm etUserPasswordConfirm vào đây
    private Button btnConfirm;
    private int userId;
    private SharedPreferences sharedPreferences;
    private TextView hoten , tvPhone;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_profile, container, false);

        // Khởi tạo các view
        etUserName = view.findViewById(R.id.et_user_name);
        tvPhone = view.findViewById(R.id.tv_user_phone);
        etUserAddress = view.findViewById(R.id.et_user_address);
        etUserOldPassword = view.findViewById(R.id.et_user_old_password);
        etUserNewPassword = view.findViewById(R.id.et_user_new_password);
        etUserNewPasswordConfirm = view.findViewById(R.id.et_user_new_password_confirm); // Khởi tạo etUserPasswordConfirm
        btnConfirm = view.findViewById(R.id.btn_confirm);

        // Lấy userId từ SharedPreferences
        sharedPreferences = requireActivity().getSharedPreferences("currentUserId", MODE_PRIVATE);
        userId = sharedPreferences.getInt("userId", -1);

        // Nếu userId hợp lệ, truy vấn dữ liệu từ Firebase
        if (userId != -1) {
            loadUserProfile(userId);
        } else {
            Toast.makeText(getActivity(), "Không tìm thấy userId", Toast.LENGTH_SHORT).show();
        }

        // Xử lý sự kiện nút xác nhận
        btnConfirm.setOnClickListener(v -> saveChanges());

        return view;
    }

    private void loadUserProfile(int userId) {
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("User").child(String.valueOf(userId));

        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // Lấy thông tin từ snapshot
                    String name = snapshot.child("name").getValue(String.class);
                    String phone = snapshot.child("phone").getValue(String.class);
                    String address = snapshot.child("address").getValue(String.class);
                    String passwordold = snapshot.child("password").getValue(String.class);

                    etUserName.setText(name);
                    tvPhone.setText(phone);
                    etUserAddress.setText(address);

                    //hoten.setText(password);
                    // Không hiển thị mật khẩu
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

    // Phương thức lưu thay đổi
    private void saveChanges() {
        String newName = etUserName.getText().toString().trim();
        String newAddress = etUserAddress.getText().toString().trim();
        String oldPassword = etUserOldPassword.getText().toString().trim();
        String newPassword = etUserNewPassword.getText().toString().trim();
        String newPasswordConfirm = etUserNewPasswordConfirm.getText().toString().trim();

        // Kiểm tra nếu các trường không rỗng và userId hợp lệ
        if (newName.isEmpty() || newAddress.isEmpty() || oldPassword.isEmpty() || newPassword.isEmpty() || newPasswordConfirm.isEmpty() || userId == -1) {
            Toast.makeText(getActivity(), "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return; // Dừng hàm nếu thông tin không đủ
        }

        // Kiểm tra xem mật khẩu mới và mật khẩu hiện tại có trùng khớp không
        if (!newPassword.equals(newPasswordConfirm)) {
            Toast.makeText(getActivity(), "Mật khẩu mới và mật khẩu hiện tại không trùng khớp", Toast.LENGTH_SHORT).show();
            return; // Dừng hàm nếu mật khẩu xác nhận không khớp
        }

        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("User").child(String.valueOf(userId));

        // Truy vấn mật khẩu cũ từ Firebase để kiểm tra
        userRef.child("password").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String currentPassword = snapshot.getValue(String.class);

                // So sánh mật khẩu cũ người dùng nhập vào với mật khẩu lưu trong Firebase
                if (currentPassword != null && currentPassword.equals(oldPassword)) {
                    // Nếu mật khẩu cũ khớp, cập nhật thông tin người dùng
                    userRef.child("name").setValue(newName);
                    userRef.child("address").setValue(newAddress);
                    userRef.child("password").setValue(newPassword); // Lưu mật khẩu mới vào Firebase

                    Toast.makeText(getActivity(), "Thông tin đã được cập nhật", Toast.LENGTH_SHORT).show();

                    // Quay lại ProfileFragment sau khi cập nhật thành công
                    requireActivity().getSupportFragmentManager().popBackStack();
                } else {
                    // Thông báo lỗi nếu mật khẩu cũ không khớp
                    Toast.makeText(getActivity(), "Mật khẩu hiện tại không đúng", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), "Lỗi kết nối đến Firebase", Toast.LENGTH_SHORT).show();
            }
        });
    }



}
