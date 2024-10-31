package com.duytai.cse441_project.adapter;

import android.app.AlertDialog;
import android.content.Context;
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
import com.duytai.cse441_project.fragment.CartFragment;
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
    private CartFragment cartFragment;

    public CartAdapter(Context context, List<CartItem> cartItemList, CartFragment cartFragment) {
        this.context = context;
        this.cartItemList = cartItemList;
        this.cartFragment = cartFragment;
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

        DatabaseReference foodRef = FirebaseDatabase.getInstance().getReference("Food")
                .child(String.valueOf(cartItem.getFoodId()));

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
                            .into(holder.imgFoodCart);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, "Lỗi khi lấy thông tin món ăn.", Toast.LENGTH_SHORT).show();
            }
        });

        holder.txtQuantityCart.setText(String.valueOf(cartItem.getQuantity()));

        holder.btn_add_quantity.setOnClickListener(v -> {
            updateCartItemQuantity(cartItem, cartItem.getQuantity() + 1);
        });

        holder.btn_minus_quantity.setOnClickListener(v -> {
            if (cartItem.getQuantity() > 1) {
                updateCartItemQuantity(cartItem, cartItem.getQuantity() - 1);
            } else {
                removeCartItem(cartItem);
            }
        });

        holder.btnRemoveItem.setOnClickListener(v -> removeCartItem(cartItem));
    }

    @Override
    public int getItemCount() {
        return cartItemList.size();
    }

    private void updateCartItemQuantity(CartItem cartItem, int newQuantity) {
        DatabaseReference cartItemRef = FirebaseDatabase.getInstance().getReference("CartItem")
                .child(String.valueOf(cartItem.getCartItemId()));
        cartItemRef.child("quantity").setValue(newQuantity).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                cartItem.setQuantity(newQuantity);
                //cartFragment.loadCartItems();
            }
        });
    }

    private void removeCartItem(CartItem cartItem) {
        DatabaseReference cartItemRef = FirebaseDatabase.getInstance().getReference("CartItem")
                .child(String.valueOf(cartItem.getCartItemId()));
        cartItemRef.removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                cartItemList.remove(cartItem);
                notifyDataSetChanged();
            }
        });
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
}
