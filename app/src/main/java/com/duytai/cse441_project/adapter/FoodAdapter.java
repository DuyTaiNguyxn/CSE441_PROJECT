package com.duytai.cse441_project.adapter;

import static java.security.AccessController.getContext;

import android.content.Context;
import android.util.Log;
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
import com.duytai.cse441_project.model.Cart;
import com.duytai.cse441_project.model.CartItem;
import com.duytai.cse441_project.model.Food;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import android.os.Bundle;
import android.widget.Toast;
import androidx.fragment.app.FragmentActivity; // Dùng để lấy FragmentManager
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        holder.txtFoodPrice.setText(String.format("%.2f VNĐ",food.getPrice()));

        // Sử dụng Glide để tải hình ảnh
        Glide.with(context)
                .load(food.getImgURL())
                .into(holder.imgFood);

        // Khi click vào tên hoặc ảnh sản phẩm
        View.OnClickListener onFoodClickListener = v -> {
            Bundle bundle = new Bundle();
            bundle.putSerializable("food", food);  //

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
            int foodId = food.getFoodId();  // foodId là thuộc tính trong đối tượng food
            int userId = 0;
//             Gọi hàm addToCart với foodId
            addToCart(foodId,userId);
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

    private void addToCart(int foodId, int userId) {
        // Khởi tạo Firebase Database Reference
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        // Tìm cartId của người dùng
        databaseReference.child("Cart").orderByChild("userId").equalTo(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int cartId;

                if (dataSnapshot.exists()) {
                    for (DataSnapshot cartSnapshot : dataSnapshot.getChildren()) {
                        cartId = cartSnapshot.child("cartId").getValue(Integer.class);
                        // Xử lý thêm sản phẩm vào CartItem
                        addFoodToCartItem(cartId, foodId);
                        return; // Đã xử lý xong
                    }
                } else {
                    // Nếu không tìm thấy cartId, tạo mới giỏ hàng
                    cartId = userId; // Sử dụng userId làm cartId
                    Cart newCart = new Cart(cartId, userId);

                    // Thêm giỏ hàng mới vào Firebase
                    databaseReference.child("Cart").child(String.valueOf(cartId)).setValue(newCart)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    // Gọi hàm thêm sản phẩm vào CartItem
                                    addFoodToCartItem(cartId, foodId);
                                } else {
                                    Log.w("TAG", "Tạo giỏ hàng mới thất bại.");
                                }
                            });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("TAG", "Error reading Cart: ", databaseError.toException());
            }
        });
    }

    // Phương thức để thêm món ăn vào CartItem
    private void addFoodToCartItem(int cartId, int foodId) {
        DatabaseReference cartItemRef = FirebaseDatabase.getInstance().getReference().child("CartItem");

        // Kiểm tra nếu món ăn đã có trong giỏ
        cartItemRef.orderByChild("cartId").equalTo(cartId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot cartItemSnapshot) {
                boolean itemExists = false;
                int cartItemId = 0;

                // Lấy cartItemId lớn nhất
                for (DataSnapshot itemSnapshot : cartItemSnapshot.getChildren()) {
                    int currentItemId = itemSnapshot.child("cartItemId").getValue(Integer.class);
                    if (currentItemId > cartItemId) {
                        cartItemId = currentItemId; //
                    }

                    // Kiểm tra nếu món ăn đã có
                    if (itemSnapshot.child("foodId").getValue(Integer.class) == foodId) {
                        itemExists = true;
                        Toast.makeText(context, "Đã có sản phẩm trong giỏ hàng.Đã cập nhật số lượng", Toast.LENGTH_SHORT).show();
                        int quantity = itemSnapshot.child("quantity").getValue(Integer.class);
                        // Tăng số lượng
                        itemSnapshot.getRef().child("quantity").setValue(quantity + 1);
                        break;
                    }
                }

                // Nếu món ăn chưa có trong giỏ, thêm mới
                if (!itemExists) {
                    cartItemId++; // Tăng cartItemId lên 1
                    // Tạo đối tượng CartItem mới
                    CartItem newItem = new CartItem(cartItemId, cartId, foodId, 1);
                    // Thêm mới vào CartItem
                    cartItemRef.child(String.valueOf(cartItemId)).setValue(newItem)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Toast.makeText(context, "Thêm món ăn vào giỏ thành công!", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(context, "Thêm món ăn vào giỏ thất bại!", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("TAG", "Error reading CartItem: ", databaseError.toException());
            }
        });
    }
}