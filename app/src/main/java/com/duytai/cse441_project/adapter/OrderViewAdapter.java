package com.duytai.cse441_project.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.duytai.cse441_project.R;
import com.duytai.cse441_project.model.Order;
import java.util.List;

public class OrderViewAdapter extends RecyclerView.Adapter<OrderViewAdapter.OrderViewHolder> {

    private List<Order> orderList;

    public OrderViewAdapter(List<Order> orderList) {
        this.orderList = orderList;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order_detail, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orderList.get(position);
        holder.txtOrderId.setText(String.valueOf(order.getOrderId()));
        holder.txtOrderStatus.setText(order.getStatus());
        holder.txtOrderPrice.setText(String.format("%,.0f VND", order.getTotalPrice()));
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView txtOrderId, txtOrderStatus, txtOrderPrice;
        ImageView imgOrder;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);

            imgOrder = itemView.findViewById(R.id.img_food_cart);
            txtOrderId = itemView.findViewById(R.id.txt_order_detail_id);
            txtOrderStatus = itemView.findViewById(R.id.txt_order_detail_status);
            txtOrderPrice = itemView.findViewById(R.id.txt_order_detail_price);
            Glide.with(itemView.getContext()).load(R.drawable.logo).into(imgOrder);
        }
    }
}
