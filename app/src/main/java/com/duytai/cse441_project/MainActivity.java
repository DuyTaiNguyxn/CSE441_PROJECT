package com.duytai.cse441_project;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {
    Button btn_login;
    EditText etPhoneNumber, etPassword;
    TextView tvPhoneError, tvPasswordError;
    private String phoneNumber, password;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        boolean isValid = true;

        // Lấy SharedPreferences để lưu userId của người dùng hiện tại
        sharedPreferences = getSharedPreferences("currentUserId", MODE_PRIVATE);

        // Đặt padding cho các view để tránh phần hệ thống (thanh trạng thái, thanh điều hướng)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Liên kết các view từ layout với các biến
        btn_login = findViewById(R.id.btn_login);
        etPhoneNumber = findViewById(R.id.et_phone_number);
        etPassword = findViewById(R.id.et_password);
        tvPhoneError = findViewById(R.id.tv_phone_error);
        tvPasswordError = findViewById(R.id.tv_password_error);
        Button btnRegister = findViewById(R.id.btn_register);

        //sự kiện khởi chạy RegisterActivity khi nhấn btn đăng kí
        btnRegister.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
            startActivity(intent);
        });



        // Thiết lập lắng nghe sự kiện nhấn nút đăng nhập
        btn_login.setOnClickListener(v -> {
            phoneNumber = etPhoneNumber.getText().toString().trim();
            password = etPassword.getText().toString().trim();

            // Kiểm tra nếu thông tin đăng nhập hợp lệ thì thực hiện xác minh với Firebase
            if (validateLogin()) {
                verifyCredentials(phoneNumber, password);
            }
        });

        // Đặt TextWatcher cho số điện thoại và mật khẩu để kiểm tra tính hợp lệ
        if (TextUtils.isEmpty(etPhoneNumber.getText().toString())) {
            tvPhoneError.setText("Vui lòng nhập số điện thoại");
            tvPhoneError.setVisibility(View.VISIBLE);
            isValid = false;
        } else {
            tvPhoneError.setVisibility(View.GONE);
        }

        etPhoneNumber.addTextChangedListener(new SimpleTextWatcher(etPhoneNumber, tvPhoneError));
        if (TextUtils.isEmpty(etPassword.getText().toString())) {
            tvPasswordError.setText("Vui lòng nhập mật khẩu");
            tvPasswordError.setVisibility(View.VISIBLE);
            isValid = false;
        } else {
            tvPasswordError.setVisibility(View.GONE);
        }
        etPassword.addTextChangedListener(new SimpleTextWatcher(etPassword, tvPasswordError));
    }

    // Phương thức xác minh tài khoản và mật khẩu từ Firebase
    private void verifyCredentials(String phoneNumber, String password) {
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("User");

        // Truy vấn Firebase dựa vào số điện thoại
        usersRef.orderByChild("phone").equalTo(phoneNumber).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                        String storedPassword = userSnapshot.child("password").getValue(String.class);
                        if (storedPassword != null && storedPassword.equals(password)) {
                            Integer userId = userSnapshot.child("userId").getValue(Integer.class);

                            // Kiểm tra userID không phải null trước khi sử dụng
                            if (userId != null) {
                                sharedPreferences.edit().putInt("userId", userId).apply();

                                // Chuyển sang HomeActivity sau khi đăng nhập thành công
                                Intent intent_login = new Intent(MainActivity.this, HomeActivity.class);
                                startActivity(intent_login);
                                finish();
                            } else {
                                Toast.makeText(MainActivity.this, "Không tìm thấy userID", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            // Thông báo lỗi nếu mật khẩu không đúng
                            tvPasswordError.setText("Sai mật khẩu");
                            tvPasswordError.setVisibility(View.VISIBLE);
                        }
                    }
                } else {
                    // Thông báo lỗi nếu số điện thoại không tồn tại
                    tvPhoneError.setText("Số điện thoại không tồn tại");
                    tvPhoneError.setVisibility(View.VISIBLE);
                }
            }



            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Thông báo nếu có lỗi kết nối đến Firebase
                Toast.makeText(MainActivity.this, "Kết nối tới cơ sở dữ liệu thất bại!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Phương thức kiểm tra nếu không có lỗi (tức là tất cả thông tin hợp lệ)
    private boolean validateLogin() {
        return tvPhoneError.getVisibility() == View.GONE && tvPasswordError.getVisibility() == View.GONE;
    }
}
