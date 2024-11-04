package com.duytai.cse441_project.fragment;

import static android.content.Context.MODE_PRIVATE;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.duytai.cse441_project.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EditProfileFragment extends Fragment {

    public EditText etUserEmail, etUserName, etUserAddress, etUserOldPassword, etUserNewPassword, etUserNewPasswordConfirm;
    private Button btnConfirm;
    private int userId;
    private SharedPreferences sharedPreferences;
    private TextView tvPhone;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_profile, container, false);

        // Khởi tạo các view
        etUserName = view.findViewById(R.id.et_user_name);
        tvPhone = view.findViewById(R.id.tv_user_phone);
        etUserAddress = view.findViewById(R.id.et_user_address);
        etUserOldPassword = view.findViewById(R.id.et_user_old_password);
        etUserNewPassword = view.findViewById(R.id.et_user_new_password);
        etUserNewPasswordConfirm = view.findViewById(R.id.et_user_new_password_confirm);
        btnConfirm = view.findViewById(R.id.btn_confirm);
        etUserEmail = view.findViewById(R.id.et_user_email);

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
                    String email = snapshot.child("email").getValue(String.class);

                    etUserName.setText(name);
                    tvPhone.setText(phone);
                    etUserAddress.setText(address);
                    etUserEmail.setText(email);
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
        // Gọi phương thức validateInput() để kiểm tra tính hợp lệ
        if (!validateInput()) {
            return; // Nếu không hợp lệ, dừng hàm
        }

        String newName = etUserName.getText().toString().trim();
        String newEmail = etUserEmail.getText().toString().trim();
        String newAddress = etUserAddress.getText().toString().trim();
        String oldPassword = etUserOldPassword.getText().toString().trim();
        String newPassword = etUserNewPassword.getText().toString().trim();
        String newPasswordConfirm = etUserNewPasswordConfirm.getText().toString().trim();

        // Nếu người dùng nhập mật khẩu mới
        if (!newPassword.isEmpty()) {
            // Hiển thị popup xác nhận đổi mật khẩu
            new AlertDialog.Builder(getActivity())
                    .setTitle("Xác nhận đổi mật khẩu")
                    .setMessage("Bạn có chắc chắn muốn thay đổi mật khẩu không?")
                    .setPositiveButton("Có", (dialog, which) -> updateUserProfile(newName, newEmail, newAddress, oldPassword, newPassword))
                    .setNegativeButton("Không", null)
                    .show();
        } else {
            // Nếu không nhập mật khẩu mới, chỉ cập nhật thông tin cá nhân
            updateUserProfile(newName, newEmail, newAddress, oldPassword, null);
        }
    }

    // Phương thức kiểm tra tính hợp lệ của các trường
    private boolean validateInput() {
        boolean isValid = true;

        // Kiểm tra họ tên
        String name = etUserName.getText().toString().trim();
        if (name.isEmpty()) {
            etUserName.setError("Vui lòng nhập họ tên");
            isValid = false;
        } else if (!name.matches("^[a-zA-Z\\s]+$")) {
            etUserName.setError("Họ tên không được chứa số hoặc ký tự đặc biệt");
            isValid = false;
        }

        // Kiểm tra email
        String email = etUserEmail.getText().toString().trim();
        if (email.isEmpty()) {
            etUserEmail.setError("Vui lòng nhập email");
            isValid = false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etUserEmail.setError("Định dạng email không hợp lệ");
            isValid = false;
        }

        // Kiểm tra địa chỉ
        String address = etUserAddress.getText().toString().trim();
        if (address.isEmpty()) {
            etUserAddress.setError("Vui lòng nhập địa chỉ");
            isValid = false;
        } else if (address.length() < 5) {
            etUserAddress.setError("Địa chỉ phải có ít nhất 5 ký tự");
            isValid = false;
        }

        // Kiểm tra mật khẩu mới
        String newPassword = etUserNewPassword.getText().toString().trim();
        if (!newPassword.isEmpty()) {
            if (newPassword.length() < 8) {
                etUserNewPassword.setError("Mật khẩu phải có ít nhất 8 ký tự");
                isValid = false;
            } else if (!newPassword.matches(".*[A-Z].*")) {
                etUserNewPassword.setError("Mật khẩu phải chứa ít nhất 1 ký tự in hoa");
                isValid = false;
            } else if (!newPassword.matches(".*[a-z].*")) {
                etUserNewPassword.setError("Mật khẩu phải chứa ít nhất 1 ký tự in thường");
                isValid = false;
            } else if (!newPassword.matches(".*\\d.*")) {
                etUserNewPassword.setError("Mật khẩu phải chứa ít nhất 1 chữ số");
                isValid = false;
            } else if (!newPassword.matches(".*[!@#\\$%\\^&*].*")) {
                etUserNewPassword.setError("Mật khẩu phải chứa ít nhất 1 ký tự đặc biệt (!@#$%^&*)");
                isValid = false;
            }

            // Kiểm tra xác nhận mật khẩu
            String newPasswordConfirm = etUserNewPasswordConfirm.getText().toString().trim();
            if (!newPasswordConfirm.equals(newPassword)) {
                etUserNewPasswordConfirm.setError("Xác nhận mật khẩu không khớp");
                isValid = false;
            }
        }

        return isValid;
    }


    private void updateUserProfile(String newName, String newEmail, String newAddress, String oldPassword, String newPassword) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("User").child(String.valueOf(userId));

        // Truy vấn mật khẩu cũ từ Firebase để kiểm tra
        userRef.child("password").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String currentPassword = snapshot.getValue(String.class);

                // Kiểm tra nếu mật khẩu cũ người dùng nhập vào là trống
                if (oldPassword == null || oldPassword.isEmpty()) {
                    Toast.makeText(getActivity(), "Mật khẩu hiện tại không được để trống.", Toast.LENGTH_SHORT).show();
                    return; // Dừng hàm nếu mật khẩu cũ trống
                }

                // So sánh mật khẩu cũ người dùng nhập vào với mật khẩu lưu trong Firebase
                if (currentPassword != null && currentPassword.equals(oldPassword)) {
                    // Nếu mật khẩu cũ khớp, cập nhật thông tin người dùng
                    userRef.child("name").setValue(newName);
                    userRef.child("email").setValue(newEmail);
                    userRef.child("address").setValue(newAddress);

                    // Chỉ cập nhật mật khẩu nếu người dùng nhập mật khẩu mới
                    if (newPassword != null && !newPassword.isEmpty()) {
                        userRef.child("password").setValue(newPassword); // Lưu mật khẩu mới vào Firebase
                    }

                    Toast.makeText(getActivity(), "Thông tin đã được cập nhật", Toast.LENGTH_SHORT).show();

                    // Quay lại ProfileFragment sau khi cập nhật thành công
                    requireActivity().getSupportFragmentManager().popBackStack();
                } else {
                    // Thông báo lỗi nếu mật khẩu cũ không khớp
                    Toast.makeText(getActivity(), "Mật khẩu hiện tại không đúng.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), "Lỗi kết nối đến Firebase", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
