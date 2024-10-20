package com.duytai.cse441_project.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.duytai.cse441_project.R;
import com.duytai.cse441_project.fragment.CategoryFragment;
import com.duytai.cse441_project.model.Category;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {
    private Context context;
    private List<Category> categoryList;
    private CategoryFragment categoryFragment;  // Thêm tham chiếu đến CategoryFragment
    private int selectedPosition = 0; // Vị trí item được chọn, mặc định là 0

    // Cập nhật constructor để nhận thêm CategoryFragment
    public CategoryAdapter(Context context, List<Category> categoryList, CategoryFragment categoryFragment) {
        this.context = context;
        this.categoryList = categoryList;
        this.categoryFragment = categoryFragment;  // Gán CategoryFragment
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_category, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Category category = categoryList.get(position);

        // Thiết lập dữ liệu cho TextView
        holder.categoryName.setText(category.getCategoryName());

        // Cập nhật trạng thái 'selected' cho item
        if (selectedPosition == position) {
            holder.itemView.setBackground(context.getDrawable(R.drawable.selector_item_category));
        } else {
            holder.itemView.setBackground(context.getDrawable(R.drawable.cusstom_search_bar));
        }

        // Thêm sự kiện click cho item
        holder.itemView.setOnClickListener(v -> {
            // Cập nhật vị trí được chọn
            notifyItemChanged(selectedPosition); // Bỏ chọn item trước đó
            selectedPosition = holder.getAdapterPosition();
            notifyItemChanged(selectedPosition); // Chọn item hiện tại

            // Gọi hàm trong CategoryFragment để load sản phẩm từ danh mục được chọn
            categoryFragment.loadFoodByCategory(category.getCategoryId());  // Sử dụng categoryFragment
        });
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView categoryName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryName = itemView.findViewById(R.id.txt_category_menu);
        }
    }
}
