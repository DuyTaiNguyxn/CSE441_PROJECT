package com.duytai.cse441_project;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
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

    EditText etEmail, etFullName, etPhoneNumber, etAddress, etPassword, etConfirmPassword;
    TextView tvEmailError, tvFullNameError, tvPhoneError, tvAddressError, tvPasswordError, tvConfirmPasswordError;
    Button btnRegister;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Link views to variables
        etFullName = findViewById(R.id.et_full_name);
        etPhoneNumber = findViewById(R.id.et_phone_number);
        etAddress = findViewById(R.id.et_address);
        etPassword = findViewById(R.id.et_password);
        etConfirmPassword = findViewById(R.id.et_confirm_password);
        etEmail = findViewById(R.id.et_email);

        tvFullNameError = findViewById(R.id.tv_full_name_error);
        tvPhoneError = findViewById(R.id.tv_phone_error);
        tvAddressError = findViewById(R.id.tv_address_error);
        tvPasswordError = findViewById(R.id.tv_password_error);
        tvConfirmPasswordError = findViewById(R.id.tv_confirm_password_error);
        tvEmailError = findViewById(R.id.tv_email_error);

        btnRegister = findViewById(R.id.btn_register);
        databaseReference = FirebaseDatabase.getInstance().getReference("User");

        btnRegister.setOnClickListener(v -> {
            if (validateRegister()) {
                // Move the phone number check to validateRegister() for async handling
                String phoneNumber = etPhoneNumber.getText().toString().trim();
                checkPhoneNumberInDatabase(phoneNumber);
            }
        });

        Button btnLogin = findViewById(R.id.btn_login);
        btnLogin.setOnClickListener(v -> finish());
    }

    private boolean validateRegister() {
        boolean isValid = true;
        // Validate full name
        String fullName = etFullName.getText().toString().trim();
        if (TextUtils.isEmpty(fullName)) {
            tvFullNameError.setText("Vui lòng nhập họ tên");
            tvFullNameError.setVisibility(View.VISIBLE);
            isValid = false;
        } else if (!fullName.matches("^[a-zA-Z\\s]+$")) {
            tvFullNameError.setText("Họ tên không được chứa số hoặc ký tự đặc biệt");
            tvFullNameError.setVisibility(View.VISIBLE);
            isValid = false;
        } else {
            tvFullNameError.setVisibility(View.INVISIBLE);
        }

        // Validate phone number
        String phoneNumber = etPhoneNumber.getText().toString().trim();
        if (TextUtils.isEmpty(phoneNumber)) {
            tvPhoneError.setText("Vui lòng nhập số điện thoại");
            tvPhoneError.setVisibility(View.VISIBLE);
            isValid = false;
        } else if (!phoneNumber.matches("^\\d{10}$")) {
            tvPhoneError.setText("Số điện thoại chỉ chứa số và phải đủ 10 số");
            tvPhoneError.setVisibility(View.VISIBLE);
            isValid = false;
        } else {
            tvPhoneError.setVisibility(View.GONE);
           // checkPhoneNumberInDatabase(phoneNumber); // Call the method without passing isValid
        }

        // Validate email
        String email = etEmail.getText().toString().trim();
        if (TextUtils.isEmpty(email)) {
            tvEmailError.setText("Vui lòng nhập email");
            tvEmailError.setVisibility(View.VISIBLE);
            isValid = false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tvEmailError.setText("Định dạng email không hợp lệ");
            tvEmailError.setVisibility(View.VISIBLE);
            isValid = false;
        } else {
            tvEmailError.setVisibility(View.GONE);
        }

        // Validate password
        String password = etPassword.getText().toString().trim();
        if (TextUtils.isEmpty(password)) {
            tvPasswordError.setText("Vui lòng nhập mật khẩu");
            tvPasswordError.setVisibility(View.VISIBLE);
            isValid = false;
        } else if (password.length() < 8) {
            tvPasswordError.setText("Mật khẩu phải có ít nhất 8 ký tự");
            tvPasswordError.setVisibility(View.VISIBLE);
            isValid = false;
        } else if (!password.matches(".*[A-Z].*")) {
            tvPasswordError.setText("Mật khẩu phải chứa ít nhất 1 ký tự in hoa");
            tvPasswordError.setVisibility(View.VISIBLE);
            isValid = false;
        } else if (!password.matches(".*[a-z].*")) {
            tvPasswordError.setText("Mật khẩu phải chứa ít nhất 1 ký tự in thường");
            tvPasswordError.setVisibility(View.VISIBLE);
            isValid = false;
        } else if (!password.matches(".*\\d.*")) {
            tvPasswordError.setText("Mật khẩu phải chứa ít nhất 1 chữ số");
            tvPasswordError.setVisibility(View.VISIBLE);
            isValid = false;
        } else if (!password.matches(".*[!@#\\$%\\^&*].*")) {
            tvPasswordError.setText("Mật khẩu phải chứa ít nhất 1 ký tự đặc biệt (!@#$%^&*)");
            tvPasswordError.setVisibility(View.VISIBLE);
            isValid = false;
        } else {
            tvPasswordError.setVisibility(View.GONE);
        }

        // Confirm password match
        String confirmPassword = etConfirmPassword.getText().toString().trim();
        if (!confirmPassword.equals(password)) {
            tvConfirmPasswordError.setText("Xác nhận mật khẩu không khớp");
            tvConfirmPasswordError.setVisibility(View.VISIBLE);
            isValid = false;
        } else {
            tvConfirmPasswordError.setVisibility(View.GONE);
        }

// Validate address
        String address = etAddress.getText().toString().trim();
        if (TextUtils.isEmpty(address)) {
            tvAddressError.setText("Vui lòng nhập địa chỉ");
            tvAddressError.setVisibility(View.VISIBLE);
            isValid = false;
        } else if (address.length() < 5) {
            tvAddressError.setText("Địa chỉ phải có ít nhất 5 ký tự");
            tvAddressError.setVisibility(View.VISIBLE);
            isValid = false;
        } else {
            tvAddressError.setVisibility(View.GONE);
        }

        return isValid;
    }

    private void checkPhoneNumberInDatabase(String phoneNumber) {
        databaseReference.orderByChild("phone").equalTo(phoneNumber).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    tvPhoneError.setText("Số điện thoại này đã được đăng ký. Vui lòng sử dụng số điện thoại khác");
                    tvPhoneError.setVisibility(View.VISIBLE);
                } else {
                    tvPhoneError.setVisibility(View.GONE);
                    registerUser(); // Proceed with registration
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(RegisterActivity.this, "Lỗi kiểm tra số điện thoại: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void registerUser() {
        // Fetch all users from Firebase to find maxUserId...
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int maxUserId = 0;

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    int currentUserId = snapshot.child("userId").getValue(Integer.class);
                    if (currentUserId > maxUserId) {
                        maxUserId = currentUserId;
                    }
                }

                int newUserId = maxUserId + 1;

                String fullName = etFullName.getText().toString().trim();
                String phoneNumber = etPhoneNumber.getText().toString().trim();
                String address = etAddress.getText().toString().trim();
                String password = etPassword.getText().toString().trim();
                String email = etEmail.getText().toString().trim();

                User newUser = new User(address, "https://storage.googleapis.com/chickengangapp.appspot.com/images/1729358813_gumball.jpg", email, fullName, password, phoneNumber, 0, "customer", newUserId);

                databaseReference.child(String.valueOf(newUserId)).setValue(newUser)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(RegisterActivity.this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
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
