package com.duytai.cse441_project;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import android.util.Log;
import android.widget.ImageView;
import com.squareup.picasso.Picasso;
import androidx.annotation.NonNull;


public class MainActivity extends AppCompatActivity {
    Button btn_login;
    ImageView logoview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btn_login = findViewById(R.id.btn_login);
        logoview = findViewById(R.id.main_img_logo);

        // Lấy tên của food với foodId là 1 và gán cho btn_login
        getFoodNameAndSetButton();

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_login = new Intent(MainActivity.this, HomeActivity.class);
                startActivity(intent_login);
            }
        });
    }


    // Test CSDL
    private void getFoodNameAndSetButton() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Food");

        // Lấy dữ liệu
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Duyệt qua tất cả các sản phẩm trong danh sách
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    // Lấy thông tin của sản phẩm
                    String foodName = snapshot.child("foodName").getValue(String.class);
                    String imgURL = snapshot.child("imgURL").getValue(String.class);
                    // Thiết lập văn bản cho Button


                    btn_login.setText(foodName);

                    // Tải và thiết lập hình ảnh cho ImageView
                    Picasso.get().load(imgURL).into(logoview);


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Xử lý lỗi nếu có
                Log.e("FirebaseError", databaseError.getMessage());
            }
        });
    }
}
