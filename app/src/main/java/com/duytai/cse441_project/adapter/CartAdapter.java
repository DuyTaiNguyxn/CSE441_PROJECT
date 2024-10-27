package com.duytai.cse441_project.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.duytai.cse441_project.R;
import com.duytai.cse441_project.model.CartItem;
import com.duytai.cse441_project.model.Food;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {
    private List<CartItem> cartItemList;
    private Context context;
    private SharedPreferences sharedPreferences;
    private int currentUserId;

    // Constructor
    public CartAdapter(Context context, List<CartItem> cartItemList) {
        this.context = context;
        this.cartItemList = cartItemList;
        // Lấy SharedPreferences và currentUserId
        sharedPreferences = context.getSharedPreferences("currentUserId", Context.MODE_PRIVATE);
        currentUserId = sharedPreferences.getInt("userId", -1);
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_food_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItem cartItem = cartItemList.get(position);
        String foodIdStr = String.valueOf(cartItem.getFoodId());

        DatabaseReference foodRef = FirebaseDatabase.getInstance().getReference("Food").child(foodIdStr);
        foodRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Food food = snapshot.getValue(Food.class);
                if (food != null) {
                    holder.txtFoodNameCart.setText(food.getFoodName());
                    holder.txtFoodPriceCart.setText(String.valueOf(food.getPrice()));
                    Glide.with(holder.itemView.getContext())
                            .load(food.getImgURL())
                            .placeholder(R.drawable.logo)
                            .error(R.drawable.logo)
                            .into(holder.imgFoodCart);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, "Lỗi khi lấy thông tin món ăn.", Toast.LENGTH_SHORT).show();
            }
        });

        holder.txtQuantityCart.setText(String.valueOf(cartItem.getQuantity()));

        // Xử lý nút tăng số lượng
        holder.btn_add_quantity.setOnClickListener(v -> {
            int newQuantity = cartItem.getQuantity() + 1;
            updateCartItemQuantity(cartItem.getCartItemId(), newQuantity);
        });

        // Xử lý nút giảm số lượng
        holder.btn_minus_quantity.setOnClickListener(v -> {
            int newQuantity = cartItem.getQuantity() - 1;
            if (newQuantity > 0) {
                updateCartItemQuantity(cartItem.getCartItemId(), newQuantity);
            } else {
                new AlertDialog.Builder(context)
                        .setTitle("Xác nhận xóa")
                        .setMessage("Số lượng đã về 0. Bạn có muốn xóa sản phẩm này khỏi giỏ hàng?")
                        .setPositiveButton("Có", (dialog, which) -> {
                            removeCartItem(cartItem.getCartItemId());
                            Toast.makeText(context, "Đã xóa sản phẩm khỏi giỏ hàng.", Toast.LENGTH_SHORT).show();
                        })
                        .setNegativeButton("Không", (dialog, which) -> dialog.dismiss())
                        .show();
            }
        });

        holder.btnRemoveItem.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Xác nhận xóa")
                    .setMessage("Bạn có chắc chắn muốn xóa sản phẩm này khỏi giỏ hàng?")
                    .setPositiveButton("Có", (dialog, which) -> {
                        removeCartItem(cartItem.getCartItemId());
                        Toast.makeText(context, "Xóa sản phẩm thành công.", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("Không", (dialog, which) -> dialog.dismiss())
                    .show();
        });
    }

    @Override
    public int getItemCount() {
        return cartItemList.size();
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder {
        TextView txtFoodNameCart, txtFoodPriceCart, txtQuantityCart;
        ImageView imgFoodCart;
        ImageButton btn_add_quantity, btn_minus_quantity, btnRemoveItem;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            imgFoodCart = itemView.findViewById(R.id.img_food_cart);
            txtFoodNameCart = itemView.findViewById(R.id.txt_foodName_cart);
            txtFoodPriceCart = itemView.findViewById(R.id.txt_foodPrice_cart);
            txtQuantityCart = itemView.findViewById(R.id.txt_quanty_cart);
            btn_add_quantity = itemView.findViewById(R.id.btn_add_cart);
            btn_minus_quantity = itemView.findViewById(R.id.btn_minus_cart);
            btnRemoveItem = itemView.findViewById(R.id.btn_remove_food_cart);
        }
    }

    private void updateCartItemQuantity(int cartItemId, int quantity) {
        // Lấy currentUserId từ SharedPreferences
        SharedPreferences sharedPreferences = context.getSharedPreferences("currentUserId", Context.MODE_PRIVATE);
        int currentUserId = sharedPreferences.getInt("userId", -1);

        // Tham chiếu đến nhánh CartItem theo cartItemId
        DatabaseReference cartItemRef = FirebaseDatabase.getInstance().getReference("CartItem").child(String.valueOf(cartItemId));

        // Cập nhật số lượng
        cartItemRef.child("quantity").setValue(quantity)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(context, "Cập nhật số lượng thành công.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "Cập nhật số lượng thất bại.", Toast.LENGTH_SHORT).show();
                    }
                });
    }




    private void removeCartItem(int cartItemId) {
        // Tham chiếu đến nhánh CartItem theo cartItemId
        DatabaseReference cartItemRef = FirebaseDatabase.getInstance().getReference("CartItem").child(String.valueOf(cartItemId));

        // Xóa mục CartItem
        cartItemRef.removeValue()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Xóa thành công
                        cartItemList.removeIf(item -> item.getCartItemId() == cartItemId);
                        notifyDataSetChanged();
                        Toast.makeText(context, "Xóa sản phẩm thành công.", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.w("TAG", "Xóa sản phẩm thất bại.");
                        Toast.makeText(context, "Xóa sản phẩm thất bại.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

}
