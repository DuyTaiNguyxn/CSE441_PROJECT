package com.duytai.cse441_project;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
    private String phoneNumber, password;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Khởi tạo SharedPreferences
        sharedPreferences = getSharedPreferences("currentUserId", MODE_PRIVATE);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Tìm các view
        btn_login = findViewById(R.id.btn_login);
        etPhoneNumber = findViewById(R.id.et_phone_number);
        etPassword = findViewById(R.id.et_password);

        // Đặt lắng nghe cho nút đăng nhập
        btn_login.setOnClickListener(v -> {
            phoneNumber = etPhoneNumber.getText().toString().trim();
            password = etPassword.getText().toString().trim();
            // Lưu userId là 0 vào SharedPreferences
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt("userId", 2);
            editor.apply();

            // Chuyển đến HomeActivity
            Intent intent_login = new Intent(MainActivity.this, HomeActivity.class);
            startActivity(intent_login);
            finish(); // Đóng MainActivity

        });
    }
}
