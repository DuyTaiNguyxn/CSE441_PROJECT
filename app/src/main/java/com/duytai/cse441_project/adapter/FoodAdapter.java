package com.duytai.cse441_project.adapter;

// FoodAdapter.java
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide; // Sử dụng thư viện Glide để tải hình ảnh
import com.duytai.cse441_project.R;
import com.duytai.cse441_project.model.Food;

import java.util.List;

public class FoodAdapter extends RecyclerView.Adapter<FoodAdapter.FoodViewHolder> {
    private List<Food> foodList;
    private Context context;

    public FoodAdapter(Context context, List<Food> foodList) {
        this.context = context;
        this.foodList = foodList;
    }

    @NonNull
    @Override
    public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_food, parent, false);
        return new FoodViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FoodViewHolder holder, int position) {
        Food food = foodList.get(position);
        holder.textFoodName.setText(food.getFoodName());
        holder.textFoodPrice.setText(String.valueOf(food.getPrice()) + " VND");

        // Tải hình ảnh từ URL bằng Glide
        Glide.with(context).load(food.getImgURL()).into(holder.imageFood);
    }

    @Override
    public int getItemCount() {
        return foodList.size();
    }

    public static class FoodViewHolder extends RecyclerView.ViewHolder {
        ImageView imageFood;
        TextView textFoodName, textFoodPrice;
        Button btnBuy;

        public FoodViewHolder(@NonNull View itemView) {
            super(itemView);
            imageFood = itemView.findViewById(R.id.imgFood);
            textFoodName = itemView.findViewById(R.id.txtFoodName);
            textFoodPrice = itemView.findViewById(R.id.txtFoodPrice);
        }
    }
}
