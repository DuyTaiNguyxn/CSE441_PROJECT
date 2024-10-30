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
    import android.widget.EditText;
    import android.widget.ImageButton;

    import com.duytai.cse441_project.R;
    import com.duytai.cse441_project.StringUtils;
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

        private RecyclerView recyclerCategory, recyclerFood;
        private CategoryAdapter categoryAdapter;
        private FoodAdapter foodAdapter;
        private List<Category> categoryList;
        private List<Food> foodList;
        private boolean isSearching = false;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
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

            ImageButton imgBt_search = view.findViewById(R.id.search_icon);
            imgBt_search.setOnClickListener(v -> {
                EditText search = view.findViewById(R.id.search_edit_text);
                String keyword = search.getText().toString().trim();
                if (!keyword.isEmpty()) {
                    searchFoodByKeyword(keyword);
                }
            });

            // Kiểm tra Bundle nhận từ trước để xử lý tìm kiếm
            Bundle bundle = getArguments();
            if (bundle != null) {
                String search = bundle.getString("search");
                if (search != null && !search.isEmpty()) {
                    isSearching = true; // Đặt cờ tìm kiếm
                    searchFoodByKeyword(search); // Tìm kiếm món ăn theo từ khóa
                }
            }

            // Lấy dữ liệu danh mục từ Firebase
            DatabaseReference categoryReference = FirebaseDatabase.getInstance().getReference("Category");
            categoryReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot categorySnapshot) {
                    categoryList.clear();
                    for (DataSnapshot category : categorySnapshot.getChildren()) {
                        if (category.exists()) {
                            Category categoryItem = category.getValue(Category.class);
                            if (categoryItem != null) {
                                categoryList.add(categoryItem);
                            }
                        }
                    }
                    categoryAdapter.notifyDataSetChanged();

                    // Nếu không đang tìm kiếm thì tải món ăn theo danh mục đầu tiên
                    if (!isSearching && !categoryList.isEmpty()) {
                        loadFoodByCategory(categoryList.get(0).getCategoryId());
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e("CategoryFragment", "Lỗi khi tải các danh mục món ăn", databaseError.toException());
                }
            });

            return view;
        }

        // Hàm lấy sản phẩm dựa trên categoryId từ Firebase
        public void loadFoodByCategory(int categoryId) {
            isSearching = false; // Đặt lại cờ tìm kiếm khi chọn danh mục
            DatabaseReference foodReference = FirebaseDatabase.getInstance().getReference("Food");
            foodReference.orderByChild("categoryId").equalTo(categoryId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot foodSnapshot) {
                    foodList.clear();
                    for (DataSnapshot food : foodSnapshot.getChildren()) {
                        Food foodItem = food.getValue(Food.class);
                        if (foodItem != null) {
                            foodList.add(foodItem);
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

        // Hàm tìm kiếm món ăn theo từ khóa
        public void searchFoodByKeyword(String keyword) {
            isSearching = true; // Đặt trạng thái là đang tìm kiếm
            clearSelectedCategory(); // Bỏ chọn tất cả các danh mục khi tìm kiếm

            DatabaseReference foodReference = FirebaseDatabase.getInstance().getReference("Food");
            foodReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot foodSnapshot) {
                    foodList.clear();

                    for (DataSnapshot food : foodSnapshot.getChildren()) {
                        Food foodItem = food.getValue(Food.class);
                        if (foodItem != null && StringUtils.containsIgnoreCaseAndAccent(foodItem.getFoodName(), keyword)) {
                            foodList.add(foodItem); // Thêm món ăn vào danh sách nếu khớp từ khóa
                        }
                    }
                    displayProducts(foodList);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e("CategoryFragment", "Error searching food by keyword", databaseError.toException());
                }
            });
        }

        // Hàm hiển thị sản phẩm lên giao diện
        private void displayProducts(List<Food> foodList) {
            foodAdapter.updateFoodList(foodList);
        }

        // Thêm phương thức để xóa lựa chọn danh mục trong CategoryFragment
        public void clearSelectedCategory() {
            // Đặt lại trạng thái của adapter danh mục
            categoryAdapter.clearSelection();
        }

    }