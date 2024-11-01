package com.duytai.cse441_project;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.duytai.cse441_project.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class RegisterActivity extends AppCompatActivity {

    EditText etFullName, etPhoneNumber, etAddress, etPassword, etConfirmPassword;
    TextView tvFullNameError, tvPhoneError, tvAddressError, tvPasswordError, tvConfirmPasswordError;
    Button btnRegister;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Liên kết các view với biến
        etFullName = findViewById(R.id.et_full_name);
        etPhoneNumber = findViewById(R.id.et_phone_number);
        etAddress = findViewById(R.id.et_address);
        etPassword = findViewById(R.id.et_password);
        etConfirmPassword = findViewById(R.id.et_confirm_password);

        tvFullNameError = findViewById(R.id.tv_full_name_error);
        tvPhoneError = findViewById(R.id.tv_phone_error);
        tvAddressError = findViewById(R.id.tv_address_error);
        tvPasswordError = findViewById(R.id.tv_password_error);
        tvConfirmPasswordError = findViewById(R.id.tv_confirm_password_error);

        btnRegister = findViewById(R.id.btn_register);
        databaseReference = FirebaseDatabase.getInstance().getReference("User");

        btnRegister.setOnClickListener(v -> {
            if (validateRegister()) {
                registerUser();
            }
        });

        Button btnLogin = findViewById(R.id.btn_login);
        btnLogin.setOnClickListener(v -> finish());

        etPhoneNumber.addTextChangedListener(new SimpleTextWatcher(etPhoneNumber, tvPhoneError));
        etPassword.addTextChangedListener(new SimpleTextWatcher(etPassword, tvPasswordError));
    }

    private boolean validateRegister() {
        boolean isValid = true;

        // Kiểm tra họ tên
        if (TextUtils.isEmpty(etFullName.getText().toString())) {
            tvFullNameError.setText("Vui lòng nhập họ tên");
            tvFullNameError.setVisibility(View.VISIBLE);
            isValid = false;
        } else {
            tvFullNameError.setVisibility(View.GONE);
        }

        // Kiểm tra số điện thoại
        if (TextUtils.isEmpty(etPhoneNumber.getText().toString())) {
            tvPhoneError.setText("Vui lòng nhập số điện thoại");
            tvPhoneError.setVisibility(View.VISIBLE);
            isValid = false;
        } else {
            tvPhoneError.setVisibility(View.GONE);
        }
        etPhoneNumber.addTextChangedListener(new SimpleTextWatcher(etPhoneNumber, tvPhoneError));


        // Kiểm tra địa chỉ
        if (TextUtils.isEmpty(etAddress.getText().toString())) {
            tvAddressError.setText("Vui lòng nhập địa chỉ");
            tvAddressError.setVisibility(View.VISIBLE);
            isValid = false;
        } else {
            tvAddressError.setVisibility(View.GONE);
        }

        // Kiểm tra mật khẩu
        if (TextUtils.isEmpty(etPassword.getText().toString())) {
            tvPasswordError.setText("Vui lòng nhập mật khẩu");
            tvPasswordError.setVisibility(View.VISIBLE);
            isValid = false;
        } else {
            tvPasswordError.setVisibility(View.GONE);
        }
        etPassword.addTextChangedListener(new SimpleTextWatcher(etPassword, tvPasswordError));

        // Kiểm tra xác nhận mật khẩu
        if (!etPassword.getText().toString().equals(etConfirmPassword.getText().toString())) {
            tvConfirmPasswordError.setText("Mật khẩu xác nhận không khớp");
            tvConfirmPasswordError.setVisibility(View.VISIBLE);
            isValid = false;
        } else {
            tvConfirmPasswordError.setVisibility(View.GONE);
        }

        return isValid;
    }

    private void registerUser() {
        // Lấy tất cả người dùng từ Firebase
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int maxUserId = 0; // Khởi tạo maxUserId với 0

                // Duyệt qua tất cả người dùng để tìm userId lớn nhất
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    int currentUserId = snapshot.child("userId").getValue(Integer.class);
                    if (currentUserId > maxUserId) {
                        maxUserId = currentUserId;
                    }
                }

                // Tạo ID người dùng mới
                int newUserId = maxUserId + 1;

                // Lấy dữ liệu từ EditText
                String fullName = etFullName.getText().toString().trim();
                String phoneNumber = etPhoneNumber.getText().toString().trim();
                String address = etAddress.getText().toString().trim();
                String password = etPassword.getText().toString().trim();

                // Tạo đối tượng User
               // User newUser = new User(newUserId, fullName, phoneNumber, password, address, "", "customer", "https://storage.googleapis.com/chickengangapp.appspot.com/images/1729358813_gumball.jpg", 0);
                    User newUser = new User(address, "https://storage.googleapis.com/chickengangapp.appspot.com/images/1729358813_gumball.jpg", "",fullName,password, phoneNumber, 0, "customer", newUserId);
                // Lưu người dùng mới vào Firebase
                databaseReference.child(String.valueOf(newUserId)).setValue(newUser)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(RegisterActivity.this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();
                                //Chuyển tới màn hình chính hoặc màn hình đăng nhập
                                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish(); // Đóng activity hiện tại
                            } else {
                                Toast.makeText(RegisterActivity.this, "Đăng ký thất bại. Vui lòng thử lại.", Toast.LENGTH_SHORT).show();
                            }
                        });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(RegisterActivity.this, "Đã xảy ra lỗi: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }




}
