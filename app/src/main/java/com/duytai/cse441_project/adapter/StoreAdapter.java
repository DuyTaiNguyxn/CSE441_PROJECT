package com.duytai.cse441_project.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.duytai.cse441_project.R;
import com.duytai.cse441_project.model.Store;

import java.util.List;

public class StoreAdapter extends RecyclerView.Adapter<StoreAdapter.StoreViewHolder> {

    private Context context;
    private List<Store> storeList;
    private OnLocationClickListener listener;

    public StoreAdapter(Context context, List<Store> storeList, OnLocationClickListener listener) {
        this.context = context;
        this.storeList = storeList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public StoreViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_store, parent, false);
        return new StoreViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StoreAdapter.StoreViewHolder holder, int position) {
        Store store = storeList.get(position);
        holder.tvStoreName.setText(store.getName());
        holder.tvTable.setText(String.valueOf(store.getTable()));
        holder.imgStore.setImageResource(store.getImageResId());

        holder.btnBook.setOnClickListener(v -> listener.onBookClick(store));
    }


    @Override
    public int getItemCount() {
        return storeList.size();
    }

    public static class StoreViewHolder extends RecyclerView.ViewHolder {
        ImageView imgStore;
        TextView tvStoreName, tvTable;
        Button btnBook;

        public StoreViewHolder(@NonNull View itemView) {
            super(itemView);
            imgStore = itemView.findViewById(R.id.imgStore);
            tvStoreName = itemView.findViewById(R.id.tvStoreName);
            tvTable = itemView.findViewById(R.id.tvTable);
            btnBook = itemView.findViewById(R.id.btnBook);
        }
    }

    public interface OnLocationClickListener {
        void onBookClick(Store store);
    }
}
