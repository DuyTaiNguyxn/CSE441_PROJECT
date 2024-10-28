package com.duytai.cse441_project;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;

import android.util.Log;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    Button btn_login;
    EditText etPhoneNumber, etPassword;
    TextView tvPhoneError, tvPasswordError;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);



        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        // Find views
        btn_login = findViewById(R.id.btn_login);
        etPhoneNumber = findViewById(R.id.et_phone_number);
        etPassword = findViewById(R.id.et_password);
        tvPhoneError = findViewById(R.id.tv_phone_error);
        tvPasswordError = findViewById(R.id.tv_password_error);

        // Set up TextWatchers for validation
        etPhoneNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                String phoneNumber = s.toString().trim();
                if (TextUtils.isEmpty(phoneNumber)) {
                    tvPhoneError.setText("Vui lòng nhập số điện thoại");
                    tvPhoneError.setVisibility(View.VISIBLE);
                } else if (!phoneNumber.matches("^\\d{10}$")) {
                    tvPhoneError.setText("Số điện thoại không hợp lệ");
                    tvPhoneError.setVisibility(View.VISIBLE);
                } else {
                    tvPhoneError.setVisibility(View.GONE);
                }
            }
        });

        etPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                String password = s.toString().trim();
                if (TextUtils.isEmpty(password)) {
                    tvPasswordError.setText("Vui lòng nhập mật khẩu");
                    tvPasswordError.setVisibility(View.VISIBLE);
                } else if (password.length() < 8) {
                    tvPasswordError.setText("Mật khẩu phải có ít nhất 8 ký tự");
                    tvPasswordError.setVisibility(View.VISIBLE);
                } else {
                    tvPasswordError.setVisibility(View.GONE);
                }
            }
        });

        // Set up login button listener
        btn_login.setOnClickListener(v -> {
            String phoneNumber = etPhoneNumber.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (validateLogin()) {
                Intent intent_login = new Intent(MainActivity.this, HomeActivity.class);
                startActivity(intent_login);
            }
        });

        getFoodNameAndSetButton();
    }

    // Method to validate if the errors are hidden (valid inputs)
    private boolean validateLogin() {
        return tvPhoneError.getVisibility() == View.GONE && tvPasswordError.getVisibility() == View.GONE;
    }

    // Method to fetch food data from Firebase and update UI
    private void getFoodNameAndSetButton() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Food");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String foodName = snapshot.child("foodName").getValue(String.class);
                    if (foodName != null) {
                        //btn_login.setText(foodName); // Example of setting button text to foodName
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("FirebaseError", databaseError.getMessage());
            }
        });
    }


}
