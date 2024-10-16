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

public class MainActivity extends AppCompatActivity {
    Button btn_login;
    ImageView imageView;

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
        // Khai báo đường dẫn đến Realtime Database
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("Food");

        // Lấy dữ liệu từ Realtime Database
        databaseRef.child("1").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Kiểm tra xem dữ liệu có tồn tại hay không
                if (dataSnapshot.exists()) {
                    // Lấy tên của food với foodId là 1
                    String foodName = dataSnapshot.child("foodName").getValue(String.class);
                    // Gán tên thực phẩm vào text của btn_login
                    btn_login.setText(foodName);
                } else {
                    Toast.makeText(MainActivity.this, "Không tìm thấy thực phẩm với foodId = 1", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(MainActivity.this, "Lỗi khi lấy dữ liệu: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
