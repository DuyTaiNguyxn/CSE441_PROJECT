package com.duytai.cse441_project.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.duytai.cse441_project.R;
import com.duytai.cse441_project.fragment.DiscountFragment;
import com.duytai.cse441_project.model.Discount;

import java.util.List;

public class DiscountAdapter extends RecyclerView.Adapter<DiscountAdapter.ViewHolder> {
    private final Context context;
    private final List<Discount> discountList;
    private final DiscountFragment discountFragment;
    private int selectedPosition = -1;

    public DiscountAdapter(Context context, List<Discount> discountList, DiscountFragment discountFragment) {
        this.context = context;
        this.discountList = discountList;
        this.discountFragment = discountFragment;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_discount, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Discount discount = discountList.get(position);
        holder.discountCode.setText(discount.getDiscountCode());
        holder.discountDescription.setText(discount.getDescription());

        // Cập nhật giao diện dựa trên trạng thái được chọn
        holder.itemView.setBackground(context.getDrawable(
                selectedPosition == position ? R.drawable.selector_item_category : R.drawable.custom_search_bar
        ));

        holder.itemView.setOnClickListener(v -> {
            int previousPosition = selectedPosition;
            selectedPosition = holder.getAdapterPosition();

            // Chỉ cập nhật nếu có sự thay đổi vị trí
            if (previousPosition != -1) notifyItemChanged(previousPosition);
            notifyItemChanged(selectedPosition);

            discountFragment.setSelectedDiscountId(discount.getDiscountId());
        });
    }


    @Override
    public int getItemCount() {
        return discountList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView discountCode, discountDescription;

        public ViewHolder(View itemView) {
            super(itemView);
            discountCode = itemView.findViewById(R.id.Txt_order1);
            discountDescription = itemView.findViewById(R.id.txt_discount_description);
        }
    }
}