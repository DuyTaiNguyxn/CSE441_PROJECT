package com.duytai.cse441_project.fragment;

// HomeFragment.java
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.duytai.cse441_project.R;
import com.duytai.cse441_project.adapter.FoodAdapter;
import com.duytai.cse441_project.model.Food;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    private RecyclerView recyclerTopSale;
    private FoodAdapter topSaleAdapter;
    private List<Food> topSaleList;
    private DatabaseReference databaseReference;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerTopSale = view.findViewById(R.id.recyclerTopSale);
        recyclerTopSale.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        topSaleList = new ArrayList<>();
        topSaleAdapter = new FoodAdapter(getContext(), topSaleList);
        recyclerTopSale.setAdapter(topSaleAdapter);

        // Lấy dữ liệu từ Firebase
        databaseReference = FirebaseDatabase.getInstance().getReference("Food");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                topSaleList.clear(); // Xóa dữ liệu cũ
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Food food = snapshot.getValue(Food.class);
                    topSaleList.add(food);
                }
                topSaleAdapter.notifyDataSetChanged(); // Cập nhật adapter
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("HomeFragment", "Error fetching data", databaseError.toException());
            }
        });

        return view;
    }
}
