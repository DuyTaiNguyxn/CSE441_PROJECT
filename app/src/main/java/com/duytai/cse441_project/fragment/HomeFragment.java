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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class HomeFragment extends Fragment {
    private RecyclerView recyclerTopSale, recyclerNewFood, recyclerCombo;
    private FoodAdapter topSaleAdapter, newFoodAdapter, comboAdapter;
    private List<Food> topSaleList, newFoodList, comboList;
    private DatabaseReference foodReference;
    private DatabaseReference orderItemReference;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Tạo view Bán chạy
        recyclerTopSale = view.findViewById(R.id.recyclerTopSale);
        recyclerTopSale.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        // Tạo view Món mới
        recyclerNewFood = view.findViewById(R.id.recyclerNewFood);
        recyclerNewFood.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        // Tạo view Combo
        recyclerCombo = view.findViewById(R.id.recyclerCombo);
        recyclerCombo.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        // Tạo list và Adapter cho Topsale
        topSaleList = new ArrayList<>();
        topSaleAdapter = new FoodAdapter(getContext(), topSaleList);
        recyclerTopSale.setAdapter(topSaleAdapter);

        // Tạo list và Adapter cho Món mới
        newFoodList = new ArrayList<>();
        newFoodAdapter = new FoodAdapter(getContext(), newFoodList);
        recyclerNewFood.setAdapter(newFoodAdapter);

        // Tạo list và Adapter cho Combo
        comboList = new ArrayList<>();
        comboAdapter = new FoodAdapter(getContext(), comboList);
        recyclerCombo.setAdapter(comboAdapter);

        // Lấy dữ liệu từ Firebase
        foodReference = FirebaseDatabase.getInstance().getReference("Food");
        orderItemReference = FirebaseDatabase.getInstance().getReference("OrderItem");

        // Lấy dữ liệu OrderItem
        orderItemReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot orderItemSnapshot) {
                // Tạo một map để lưu số lượng bán cho mỗi món ăn
                HashMap<Integer, Integer> foodSalesMap = new HashMap<>();

                // Tính tổng số lượng bán cho từng món ăn
                for (DataSnapshot orderItem : orderItemSnapshot.getChildren()) {
                    int foodId = orderItem.child("foodId").getValue(Integer.class);
                    int quantity = orderItem.child("quantity").getValue(Integer.class);
                    foodSalesMap.put(foodId, foodSalesMap.getOrDefault(foodId, 0) + quantity);
                }

                // Lấy dữ liệu món ăn
                foodReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot foodSnapshot) {
                        topSaleList.clear(); // Xóa dữ liệu cũ
                        newFoodList.clear(); // Xóa danh sách món mới cũ
                        comboList.clear(); // Xóa danh sách combo cũ

                        for (DataSnapshot food : foodSnapshot.getChildren()) {
                            Food foodItem = food.getValue(Food.class);
                            int totalQuantity = foodSalesMap.getOrDefault(foodItem.getFoodId(), 0);

                            // Cập nhật số lượng bán cho món ăn
                            if (totalQuantity > 0) {
                                foodItem.setQuantitySold(totalQuantity); // Cập nhật số lượng bán
                                topSaleList.add(foodItem); // Thêm vào danh sách món bán chạy
                            }

                            // Thêm vào danh sách món mới (10 món mới nhất)
                            newFoodList.add(foodItem); // Thêm vào danh sách món mới

                            // Kiểm tra danh mục để thêm vào danh sách combo
                            if (foodItem.getCategory().equals("Combo")) {
                                comboList.add(foodItem); // Thêm vào danh sách combo
                            }
                        }

                        // Nếu không có sản phẩm bán nào, lấy 10 sản phẩm đầu tiên từ Food
                        if (topSaleList.isEmpty()) {
                            for (int i = 0; i < Math.min(10, foodSnapshot.getChildrenCount()); i++) {
                                Food foodItem = foodSnapshot.child(String.valueOf(i)).getValue(Food.class);
                                topSaleList.add(foodItem); // Thêm món ăn mà không có số lượng
                            }
                        } else {
                            // Sắp xếp danh sách theo số lượng bán từ cao xuống thấp
                            Collections.sort(topSaleList, new Comparator<Food>() {
                                @Override
                                public int compare(Food o1, Food o2) {
                                    return Integer.compare(o2.getQuantitySold(), o1.getQuantitySold()); // Sắp xếp theo số lượng bán
                                }
                            });

                            // Giới hạn danh sách chỉ còn 10 sản phẩm
                            if (topSaleList.size() > 10) {
                                topSaleList = topSaleList.subList(0, 10);
                            }
                        }

                        // Giới hạn danh sách món mới chỉ còn 10 sản phẩm
                        if (newFoodList.size() > 10) {
                            newFoodList = newFoodList.subList(0, 10);
                        }

                        topSaleAdapter.notifyDataSetChanged(); // Cập nhật adapter bán chạy
                        newFoodAdapter.notifyDataSetChanged(); // Cập nhật adapter món mới
                        comboAdapter.notifyDataSetChanged(); // Cập nhật adapter combo
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.e("HomeFragment", "Error fetching food data", databaseError.toException());
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("HomeFragment", "Error fetching order item data", databaseError.toException());
            }
        });

        return view;
    }
}
