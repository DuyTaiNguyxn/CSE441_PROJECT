package com.duytai.cse441_project.adapter;

import android.graphics.Color;
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

import java.util.ArrayList;

public class StoreAdapter extends RecyclerView.Adapter<StoreAdapter.StoreViewHolder> {

    private ArrayList<Store> storeList;
    private OnBookButtonClickListener onBookButtonClickListener;

    public StoreAdapter(ArrayList<Store> storeList, OnBookButtonClickListener listener) {
        this.storeList = storeList;
        this.onBookButtonClickListener = listener;
    }

    @NonNull
    @Override
    public StoreViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_store, parent, false);
        return new StoreViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StoreViewHolder holder, int position) {
        Store store = storeList.get(position);

        holder.storeName.setText(store.getStoreName());
        holder.openingHours.setText(store.getOpeningHours());
        holder.availableTables.setText(String.valueOf(store.getAvailableTables()));
        if(store.getAvailableTables() == 0){
            holder.availableTables.setTextColor(Color.RED);
        }
        // Thiết lập sự kiện cho nút đặt bàn
        holder.btnBook.setOnClickListener(v -> {
            if (onBookButtonClickListener != null) {
                onBookButtonClickListener.onBookButtonClick(store);
            }
        });


        Glide.with(holder.itemView.getContext())
                .load(store.getImgURL())
                .error(R.drawable.logo)
                .into(holder.storeImage);
    }

    @Override
    public int getItemCount() {
        return storeList.size();
    }

    public static class StoreViewHolder extends RecyclerView.ViewHolder {
        TextView storeName, openingHours, availableTables;
        ImageView storeImage;
        Button btnBook;

        public StoreViewHolder(@NonNull View itemView) {
            super(itemView);
            storeName = itemView.findViewById(R.id.tvStoreName);
            openingHours = itemView.findViewById(R.id.tv_opening_hours);
            availableTables = itemView.findViewById(R.id.tvTable);
            storeImage = itemView.findViewById(R.id.imgStore);
            btnBook = itemView.findViewById(R.id.btnBook);
        }
    }

    // Interface để xử lý sự kiện nhấn nút
    public interface OnBookButtonClickListener {
        void onBookButtonClick(Store store);
    }
}
