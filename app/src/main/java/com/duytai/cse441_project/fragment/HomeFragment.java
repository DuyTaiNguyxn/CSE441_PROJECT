package com.duytai.cse441_project.fragment;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

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
    private EditText search_bar;
    private ImageButton btn_search;

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
        // Xu ly tim kiem
        search_bar = view.findViewById(R.id.search_edit_text);
        btn_search = view.findViewById(R.id.search_icon);
        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String search = search_bar.getText().toString();
                if (search.isEmpty()) {
                    search_bar.setError("Vui lòng nhập từ khóa");
                    search_bar.requestFocus();
                } else {
                    Log.d("HomeFragment", "Search button clicked!");
                    Bundle bundle = new Bundle();
                    bundle.putString("search", search);
                    Fragment fragment = new CategoryFragment();
                    fragment.setArguments(bundle);
                    FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.fragmentContainerView, fragment);
                    transaction.addToBackStack(null);
                    transaction.commit();
                }
            }
        });

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

                // Lấy dữ liệu danh mục
                DatabaseReference categoryReference = FirebaseDatabase.getInstance().getReference("Category");
                categoryReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot categorySnapshot) {
                        int comboCategoryId = -1;

                        // Duyệt qua danh sách danh mục để tìm categoryId của "Combo"
                        for (DataSnapshot category : categorySnapshot.getChildren()) {
                            String categoryName = category.child("categoryName").getValue(String.class);
                            if ("Combo".equals(categoryName)) {
                                comboCategoryId = category.child("categoryId").getValue(Integer.class);
                                break; // Đã tìm thấy, không cần tiếp tục
                            }
                        }

                        // Lấy dữ liệu món ăn
                        int finalComboCategoryId = comboCategoryId;
                        foodReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot foodSnapshot) {
                                topSaleList.clear(); // Xóa dữ liệu cũ
                                newFoodList.clear(); // Xóa danh sách món mới cũ
                                comboList.clear(); // Xóa danh sách combo cũ
                                int TopSaleCount = 0;
                                int newFoodCount = 0;
                                int ComboCount = 0;

                                for (DataSnapshot food : foodSnapshot.getChildren()) {
                                    Food foodItem = food.getValue(Food.class);
                                    int totalQuantity = foodSalesMap.getOrDefault(foodItem.getFoodId(), 0);

                                    // Cập nhật số lượng bán cho món ăn
                                    if (totalQuantity > 0 && TopSaleCount < 10) {
                                        foodItem.setQuantitySold(totalQuantity);
                                        TopSaleCount++;
                                        topSaleList.add(foodItem);
                                    }

                                    if (newFoodCount < 10) { // Chỉ thêm tối đa 10 món mới
                                        newFoodList.add(foodItem); // Thêm vào danh sách món mới
                                        newFoodCount++; // Tăng biến đếm
                                    }

                                    // Kiểm tra danh mục (categoryId) để thêm vào danh sách combo
                                    if (foodItem.getCategoryId() == finalComboCategoryId && ComboCount < 10) {
                                        ComboCount++;
                                        comboList.add(foodItem);// Thêm vào danh sách combo
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
                                            return Integer.compare(o2.getQuantitySold(), o1.getQuantitySold());
                                        }
                                    });

                                }

                                topSaleAdapter.notifyDataSetChanged(); // Cập nhật adapter bán chạy
                                newFoodAdapter.notifyDataSetChanged(); // Cập nhật adapter món mới
                                comboAdapter.notifyDataSetChanged(); // Cập nhật adapter combo
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Log.e("HomeFragment", "Lỗi tải dữ liệu món ăn", databaseError.toException());
                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.e("HomeFragment", "Lỗi tải dữ liệu danh mục", databaseError.toException());
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("HomeFragment", "Lỗi tải dữ liệu đơn hàng", databaseError.toException());
            }
        });

        return view;
    }
}
