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
    private final Context context;
    private final List<Category> categoryList;
    private final CategoryFragment categoryFragment;
    private int selectedPosition = 0;

    public CategoryAdapter(Context context, List<Category> categoryList, CategoryFragment categoryFragment) {
        this.context = context;
        this.categoryList = categoryList;
        this.categoryFragment = categoryFragment;
        notifyItemChanged(selectedPosition);
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
        holder.categoryName.setText(category.getCategoryName());

        // Đổi giao diện khi chọn item
        if (selectedPosition == position) {
            holder.itemView.setBackground(context.getDrawable(R.drawable.selector_item_category));
        } else {
            holder.itemView.setBackground(context.getDrawable(R.drawable.custom_search_bar));
        }

        // Thiết lập sự kiện click cho item
        holder.itemView.setOnClickListener(v -> {
            if (selectedPosition != holder.getAdapterPosition()) {
                notifyItemChanged(selectedPosition);
                selectedPosition = holder.getAdapterPosition();
                notifyItemChanged(selectedPosition);

                categoryFragment.loadFoodByCategory(category.getCategoryId()); // Lọc món ăn theo categoryId
            }
        });
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView categoryName;

        public ViewHolder(View itemView) {
            super(itemView);
            categoryName = itemView.findViewById(R.id.txt_category_menu);
        }
    }

    // Phương thức để bỏ chọn tất cả các danh mục
    public void clearSelection() {
        int previousPosition = selectedPosition;
        selectedPosition = -1; // Đặt lại giá trị không có danh mục nào được chọn
        notifyItemChanged(previousPosition); // Cập nhật lại danh mục đã bỏ chọn
    }

}