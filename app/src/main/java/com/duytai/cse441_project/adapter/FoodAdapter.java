package com.duytai.cse441_project.adapter;

// FoodAdapter.java
import android.content.Context;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide; // Sử dụng thư viện Glide để tải hình ảnh
import com.duytai.cse441_project.R;
import com.duytai.cse441_project.fragment.DetailFoodFragment;
import com.duytai.cse441_project.model.Food;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity; // Dùng để lấy Activity
import androidx.fragment.app.FragmentActivity; // Dùng để lấy FragmentManager

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
        holder.txtFoodName.setText(food.getFoodName());
        holder.txtFoodPrice.setText(String.format("%.2f VNĐ", food.getPrice()));

        // Sử dụng Glide để tải hình ảnh
        Glide.with(context)
                .load(food.getImgURL())
                .into(holder.imgFood);

        // Khi click vào tên hoặc ảnh sản phẩm
        View.OnClickListener onFoodClickListener = v -> {
            Bundle bundle = new Bundle();
            bundle.putSerializable("food", food);  // Truyền đối tượng Food qua Serializable

            DetailFoodFragment detailFoodFragment = new DetailFoodFragment();
            detailFoodFragment.setArguments(bundle);// Set dữ liệu vào Fragment

            // Chỉ thay đổi UI khi mở fragment chi tiết sản phẩm
            if (context instanceof FragmentActivity) {
                FragmentActivity fragmentActivity = (FragmentActivity) context;

                // Ẩn nút "btn_back_Topnav" và thay đổi nội dung "txt_app_name"
                ImageButton btnBackTopnav = fragmentActivity.findViewById(R.id.btn_back_Topnav);
                TextView txtAppName = fragmentActivity.findViewById(R.id.txt_app_name);

                if (btnBackTopnav != null) {
                    btnBackTopnav.setVisibility(View.VISIBLE);
                    btnBackTopnav.setOnClickListener(v1 -> {
                        fragmentActivity.getSupportFragmentManager().popBackStack();
                    });
                }

                if (txtAppName != null) {
                    txtAppName.setText("Chi tiết sản phẩm");  // Đặt tên ứng dụng khi vào fragment chi tiết sản phẩm
                }

                // Chuyển fragment
                fragmentActivity.getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragmentContainerView, detailFoodFragment)
                        .addToBackStack(null)
                        .commit();
            }
        };

        // Áp dụng sự kiện click cho tên và ảnh sản phẩm
        holder.txtFoodName.setOnClickListener(onFoodClickListener);
        holder.imgFood.setOnClickListener(onFoodClickListener);

        // Xử lý khi nhấn nút "Mua"
        holder.btnBuy.setOnClickListener(v -> {
            // Xử lý hành động khi nhấn nút "Mua"
        });
    }

    @Override
    public int getItemCount() {
        return foodList.size();
    }

    public void updateFoodList(List<Food> newFoodList) {
        this.foodList = newFoodList;
        notifyDataSetChanged();
    }

    public static class FoodViewHolder extends RecyclerView.ViewHolder {
        ImageView imgFood;
        TextView txtFoodName, txtFoodPrice;
        Button btnBuy;

        public FoodViewHolder(@NonNull View itemView) {
            super(itemView);
            imgFood = itemView.findViewById(R.id.imgFood);
            txtFoodName = itemView.findViewById(R.id.txtFoodName);
            txtFoodPrice = itemView.findViewById(R.id.txtFoodPrice);
            btnBuy = itemView.findViewById(R.id.btn_buy);
        }
    }
}