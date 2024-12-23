package com.duytai.cse441_project.adapter;

import android.content.Context;
import android.content.SharedPreferences;
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
import com.google.firebase.database.ValueEventListener;
import android.os.Bundle;
import android.widget.Toast;
import androidx.fragment.app.FragmentActivity; // Dùng để lấy FragmentManager

import java.util.List;
import java.util.Locale;

public class FoodAdapter extends RecyclerView.Adapter<FoodAdapter.FoodViewHolder> {

    private List<Food> foodList;
    private Context context;
    private SharedPreferences sharedPreferences;

    public FoodAdapter(Context context, List<Food> foodList) {
        this.context = context;
        this.foodList = foodList;
        this.sharedPreferences = context.getSharedPreferences("currentUserId", Context.MODE_PRIVATE);
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
        holder.txtFoodPrice.setText(String.format(Locale.getDefault(), "%.2f VNĐ", food.getPrice()));

        // Sử dụng Glide để tải hình ảnh
        Glide.with(context)
                .load(food.getImgURL())
                .into(holder.imgFood);

        // Khi click vào tên hoặc ảnh sản phẩm
        View.OnClickListener onFoodClickListener = v -> {
            Bundle bundle = new Bundle();
            bundle.putSerializable("food", food);

            DetailFoodFragment detailFoodFragment = new DetailFoodFragment();
            detailFoodFragment.setArguments(bundle);

            if (context instanceof FragmentActivity) {
                FragmentActivity fragmentActivity = (FragmentActivity) context;

                // Ẩn nút "btn_back_Topnav" và thay đổi nội dung "txt_app_name"
                ImageButton btnBackTopnav = fragmentActivity.findViewById(R.id.btn_back_Topnav);
                TextView txtAppName = fragmentActivity.findViewById(R.id.txt_app_name);

                if (btnBackTopnav != null) {
                    btnBackTopnav.setVisibility(View.VISIBLE);
                    btnBackTopnav.setOnClickListener(v1 -> fragmentActivity.getSupportFragmentManager().popBackStack());
                }

                if (txtAppName != null) {
                    txtAppName.setText("Chi tiết sản phẩm");
                }

                // Chuyển fragment
                fragmentActivity.getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragmentContainerView, detailFoodFragment)
                        .addToBackStack(null)
                        .commit();
            }
        };

        holder.txtFoodName.setOnClickListener(onFoodClickListener);
        holder.imgFood.setOnClickListener(onFoodClickListener);

        // Xử lý khi nhấn nút "Mua"
        holder.btnBuy.setOnClickListener(v -> {
            int foodId = food.getFoodId();
            int userId = sharedPreferences.getInt("userId", -1); // Lấy userId

            if (userId != -1) {
                addToCart(foodId, userId);
            } else {
                Toast.makeText(context, "Lỗi: Không xác định người dùng. Vui lòng đăng nhập.", Toast.LENGTH_SHORT).show();
            }
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
            btnBuy = itemView.findViewById(R.id.btn_buy_detail);
        }
    }

    private void addToCart(int foodId, int userId) {
        // Khởi tạo Firebase Database Reference
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        // Tìm cartId của người dùng
        databaseReference.child("Cart").orderByChild("userId").equalTo(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Nếu đã có giỏ hàng của người dùng, lấy cartId
                    for (DataSnapshot cartSnapshot : dataSnapshot.getChildren()) {
                        int cartId = cartSnapshot.child("cartId").getValue(Integer.class);
                        // Xử lý thêm sản phẩm vào CartItem
                        addFoodToCartItem(cartId, foodId);
                    }
                } else {
                    // Nếu không tìm thấy cartId, tạo mới giỏ hàng
                    int cartId = userId; // Sử dụng userId làm cartId
                    Cart newCart = new Cart(cartId, userId);

                    // Thêm giỏ hàng mới vào Firebase
                    databaseReference.child("Cart").child(String.valueOf(cartId)).setValue(newCart)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    // Gọi hàm thêm sản phẩm vào CartItem
                                    addFoodToCartItem(cartId, foodId);
                                } else {
                                    Toast.makeText(context, "Tạo mới giỏ hàng thất bại", Toast.LENGTH_SHORT).show();
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


    private void addFoodToCartItem(int cartId, int foodId) {
        DatabaseReference cartItemRef = FirebaseDatabase.getInstance().getReference().child("CartItem");

        // Kiểm tra nếu CartItem đã tồn tại dựa trên cartId và foodId
        cartItemRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot cartItemSnapshot) {
                boolean itemExists = false;
                int maxCartItemId = 0;

                // Duyệt qua tất cả các mục trong CartItem để tìm maxCartItemId và kiểm tra tồn tại
                for (DataSnapshot itemSnapshot : cartItemSnapshot.getChildren()) {
                    Integer currentCartId = itemSnapshot.child("cartId").getValue(Integer.class);
                    Integer currentFoodId = itemSnapshot.child("foodId").getValue(Integer.class);
                    Integer currentCartItemId = itemSnapshot.child("cartItemId").getValue(Integer.class);

                    // Cập nhật maxCartItemId để tạo ID mới nếu cần
                    if (currentCartItemId != null && currentCartItemId > maxCartItemId) {
                        maxCartItemId = currentCartItemId;
                    }

                    // Kiểm tra nếu cả cartId và foodId trùng nhau trong CartItem thì tăng số lượng
                    if (currentCartId != null && currentFoodId != null && currentCartId == cartId && currentFoodId == foodId) {
                        itemExists = true;
                        int currentQuantity = itemSnapshot.child("quantity").getValue(Integer.class);
                        int newQuantity = currentQuantity + 1;

                        // Cập nhật số lượng mới vào database
                        cartItemRef.child(String.valueOf(currentCartItemId)).child("quantity").setValue(newQuantity)
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(context, "Thêm sản phẩm vào giỏ hàng thành công", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(context, "Thêm sản phẩm vào giỏ hàng thất bại", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                }

                // Nếu không tồn tại item với cartId và foodId, tạo CartItem mới
                if (!itemExists) {
                    int newCartItemId = maxCartItemId + 1;  // Tạo ID mới cho CartItem
                    CartItem newItem = new CartItem(newCartItemId, cartId, foodId, 1); // Số lượng bắt đầu là 1

                    // Thêm CartItem mới vào database
                    cartItemRef.child(String.valueOf(newCartItemId)).setValue(newItem)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Toast.makeText(context, "Thêm sản phẩm vào giỏ hàng thành công", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(context, "Thêm sản phẩm vào giỏ hàng thất bại", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.w("TAG", "Error reading CartItem: ", error.toException());
            }
        });
    }


}