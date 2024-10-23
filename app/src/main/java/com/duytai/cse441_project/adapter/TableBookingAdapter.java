package com.duytai.cse441_project.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.duytai.cse441_project.R;
import com.duytai.cse441_project.fragment.TableBookingFragment;
import com.duytai.cse441_project.model.TableInfo;

import java.util.ArrayList;

public class TableBookingAdapter extends RecyclerView.Adapter<TableBookingAdapter.ViewHolder> {
    private Context context;
    private ArrayList<TableInfo> availableTableList;
    private TableBookingFragment reservationFragment;
    private int selectedPosition = -1; // Vị trí item được chọn, mặc định là 0

    public TableBookingAdapter(Context context, ArrayList<TableInfo> availableTableList, TableBookingFragment reservationFragment) {
        this.context = context;
        this.availableTableList = availableTableList;
        this.reservationFragment = reservationFragment;  // Gán CategoryFragment
    }

    @NonNull
    @Override
    public TableBookingAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_category, parent, false);
        return new TableBookingAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TableBookingAdapter.ViewHolder holder, int position) {
        TableInfo table = availableTableList.get(position);
        String strTableId = String.valueOf(table.getTableId()+1);
        // Thiết lập dữ liệu cho TextView
        holder.availableTable.setText("Bàn " + strTableId + " (" + table.getSeats() + " chỗ)");

        // Cập nhật trạng thái 'selected' cho item
        if (selectedPosition == position) {
            holder.itemView.setBackground(context.getDrawable(R.drawable.selector_item_category));
        } else {
            holder.itemView.setBackground(context.getDrawable(R.drawable.custom_search_bar));
        }

        // Thêm sự kiện click cho item
        holder.itemView.setOnClickListener(v -> {
            // Cập nhật vị trí được chọn
            notifyItemChanged(selectedPosition); // Bỏ chọn item trước đó
            selectedPosition = holder.getAdapterPosition();
            notifyItemChanged(selectedPosition); // Chọn item hiện tại

            reservationFragment.selectedTable(table.getTableId());
        });
    }

    @Override
    public int getItemCount() {
        return availableTableList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView availableTable;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            availableTable = itemView.findViewById(R.id.txt_category_menu);
        }
    }
}