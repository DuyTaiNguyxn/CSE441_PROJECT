package com.duytai.cse441_project.fragment;

import android.os.Bundle;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.duytai.cse441_project.R;
import com.duytai.cse441_project.adapter.CategoryAdapter;
import com.duytai.cse441_project.adapter.FoodAdapter;
import com.duytai.cse441_project.model.Category;
import com.duytai.cse441_project.model.Food;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class CategoryFragment extends Fragment {

    private RecyclerView recyclerCategory, recyclerFood; // RecyclerView cho danh mục và món ăn
    private CategoryAdapter categoryAdapter;
    private FoodAdapter foodAdapter; // Adapter cho món ăn
    private List<Category> categoryList;
    private List<Food> foodList; // Danh sách món ăn

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_category, container, false);

        // Khởi tạo RecyclerView cho danh mục
        recyclerCategory = view.findViewById(R.id.recyclerCategory);
        LinearLayoutManager categoryLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerCategory.setLayoutManager(categoryLayoutManager);

        // Khởi tạo danh sách và adapter cho danh mục
        categoryList = new ArrayList<>();
        categoryAdapter = new CategoryAdapter(getContext(), categoryList, this);
        recyclerCategory.setAdapter(categoryAdapter);

        // Khởi tạo RecyclerView cho món ăn
        recyclerFood = view.findViewById(R.id.recyclerFoodByCategory);
        GridLayoutManager foodLayoutManager = new GridLayoutManager(getContext(), 2, GridLayoutManager.VERTICAL, false);
        recyclerFood.setLayoutManager(foodLayoutManager);

        // Khởi tạo danh sách và adapter cho món ăn
        foodList = new ArrayList<>();
        foodAdapter = new FoodAdapter(getContext(), foodList);
        recyclerFood.setAdapter(foodAdapter);

        // Lấy dữ liệu danh mục từ Firebase
        DatabaseReference categoryReference = FirebaseDatabase.getInstance().getReference("Category");
        categoryReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot categorySnapshot) {
                categoryList.clear(); // Xóa danh sách cũ
                for (DataSnapshot category : categorySnapshot.getChildren()) {
                    if (category.exists()) {
                        Category categoryItem = category.getValue(Category.class);
                        if (categoryItem != null) {
                            categoryList.add(categoryItem); // Thêm danh mục vào danh sách
                        }
                    }
                }
                categoryAdapter.notifyDataSetChanged(); // Cập nhật adapter
                if (!categoryList.isEmpty()) {
                    loadFoodByCategory(categoryList.get(0).getCategoryId()); // Thay đổi tại đây
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("CategoryFragment", "Error fetching category data", databaseError.toException());
            }
        });

        return view;
    }

    // Hàm lấy sản phẩm dựa trên categoryId từ Firebase
    public void loadFoodByCategory(int categoryId) {
        DatabaseReference foodReference = FirebaseDatabase.getInstance().getReference("Food");
        foodReference.orderByChild("categoryId").equalTo(categoryId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot foodSnapshot) {
                foodList.clear(); // Xóa danh sách món cũ
                for (DataSnapshot food : foodSnapshot.getChildren()) {
                    Food foodItem = food.getValue(Food.class);
                    if (foodItem != null) {
                        foodList.add(foodItem); // Thêm món ăn vào danh sách
                    }
                }
                displayProducts(foodList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("CategoryFragment", "Error fetching food data", databaseError.toException());
            }
        });
    }

    // Hàm hiển thị sản phẩm lên giao diện
    private void displayProducts(List<Food> foodList) {
        foodAdapter.updateFoodList(foodList); // Cập nhật danh sách món ăn trong adapter
    }
}
