package com.duytai.cse441_project.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.duytai.cse441_project.R;
import com.duytai.cse441_project.model.Store;
import java.util.List;

public class StoreAdapter extends RecyclerView.Adapter<StoreAdapter.StoreViewHolder> {

    private List<Store> storeList;
    private Context context;

    public StoreAdapter(Context context, List<Store> storeList) {
        this.context = context;
        this.storeList = storeList;
    }

    @NonNull
    @Override
    public StoreViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_store, parent, false);
        return new StoreViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StoreViewHolder holder, int position) {
        Store store = storeList.get(position);

        if (store == null) return;  // Kiểm tra dữ liệu hợp lệ

        holder.txtStoreName.setText(store.getStoreName());
        holder.txtOpeningHours.setText(store.getOpeningHours());

        // Sử dụng Glide để tải ảnh của cửa hàng
        Glide.with(context)
                .load(store.getImgURL())
                .placeholder(R.drawable.logo) // Ảnh chờ trong lúc tải
                .error(R.drawable.logo) // Ảnh lỗi nếu tải không thành công
                .into(holder.imgStore);

        holder.btnBook.setOnClickListener(v -> {
            // Xử lý khi nhấn nút "Đặt bàn"
            Log.d("StoreAdapter", "Đã nhấn nút đặt bàn cho cửa hàng: " + store.getStoreName());
        });
    }


    @Override
    public int getItemCount() {
        return storeList.size();
    }

    public static class StoreViewHolder extends RecyclerView.ViewHolder {
        ImageView imgStore;
        TextView txtStoreName, txtOpeningHours;
        Button btnBook;

        public StoreViewHolder(@NonNull View itemView) {
            super(itemView);
            imgStore = itemView.findViewById(R.id.imgStore);
            txtStoreName = itemView.findViewById(R.id.tvStoreName);
            txtOpeningHours = itemView.findViewById(R.id.tv_opening_hours);
            btnBook = itemView.findViewById(R.id.btnBook);
        }
    }
}
