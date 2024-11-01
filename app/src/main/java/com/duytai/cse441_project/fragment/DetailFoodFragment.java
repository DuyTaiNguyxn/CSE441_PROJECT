package com.duytai.cse441_project.fragment;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.bumptech.glide.Glide;
import com.duytai.cse441_project.R;
import com.duytai.cse441_project.model.Cart;
import com.duytai.cse441_project.model.CartItem;
import com.duytai.cse441_project.model.Food;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class DetailFoodFragment extends Fragment {


    private Food food;
    private Cart cart;
    private CartItem cartItem;
    private List<CartItem> cartItemList;
    TextView txtFoodName, txtFoodPrice, txtFoodDescription, txtQuantity;
    ImageView imgFood;
    ImageButton btn_minus_detai, btn_add_detail;
    Button btnAddToCart;
    private Context context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate layout fragment_detail_food
        return inflater.inflate(R.layout.fragment_food_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Nhận đối tượng Food từ Bundle
        if (getArguments() != null) {
            food = (Food) getArguments().getSerializable("food");
            cart = (Cart) getArguments().getSerializable("cart");
        }

        // Hiển thị thông tin chi tiết
        if (food != null) {
          txtFoodName = view.findViewById(R.id.txt_food_name_detail);
          txtFoodPrice = view.findViewById(R.id.txt_priceFood_detail);
          txtFoodDescription = view.findViewById(R.id.txt_food_description);
          imgFood = view.findViewById(R.id.img_food_detail);
          txtQuantity = view.findViewById(R.id.txt_quantity_detail);
          btn_minus_detai = view.findViewById(R.id.btn_minus_detail);
          btn_add_detail = view.findViewById(R.id.btn_add_detail);
          btnAddToCart = view.findViewById(R.id.btn_buy_detail);

            // Kiểm tra null trước khi set giá trị
            if (txtFoodName != null) {
                txtFoodName.setText(food.getFoodName());
            }
            if (txtFoodPrice != null) {
                txtFoodPrice.setText(String.format("%.2f VNĐ", food.getPrice()));
            }
            if (txtFoodDescription != null) {
                txtFoodDescription.setText(food.getDescription());
            }
            if (imgFood != null) {
                Glide.with(getContext())
                        .load(food.getImgURL())
                        .into(imgFood);
            }
            btn_add_detail.setOnClickListener(v -> {
                if (txtQuantity != null && !txtQuantity.getText().toString().isEmpty()) {
                    int quantity = Integer.parseInt(txtQuantity.getText().toString());
                    txtQuantity.setText(String.valueOf(quantity + 1));
                }
            });
            btn_minus_detai.setOnClickListener(v -> {
                if (txtQuantity != null && !txtQuantity.getText().toString().isEmpty()) {
                    int quantity = Integer.parseInt(txtQuantity.getText().toString());
                    if (quantity > 1) {
                        txtQuantity.setText(String.valueOf(quantity - 1));
                    } else{
                        Toast.makeText(getContext(), "Số lượng phải lớn hơn 0!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            btnAddToCart.setOnClickListener(v -> {
                if (txtQuantity != null && !txtQuantity.getText().toString().isEmpty()) {
                    int quantity = Integer.parseInt(txtQuantity.getText().toString());
                    if (quantity > 0) {
                        // Kiểm tra userId và foodId từ dữ liệu người dùng và sản phẩm hiện tại
                        int userId = 0; // Hàm giả lập lấy userId từ phiên đăng nhập
                        int foodId = food.getFoodId();

                        // Thêm vào giỏ hàng
                        addToCart(foodId, userId, quantity);
                    } else {
                        Toast.makeText(getContext(), "Số lượng phải lớn hơn 0!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), "Vui lòng nhập số lượng!", Toast.LENGTH_SHORT).show();
                }
            });

        }else{
            Toast.makeText(getContext(), "Không tìm thấy thông tin sản phẩm", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        FragmentActivity fragmentActivity = getActivity();
        if (fragmentActivity != null) {
            // Hiện lại nút "btn_back_Topnav"
            ImageButton btnBackTopnav = fragmentActivity.findViewById(R.id.btn_back_Topnav);
            if (btnBackTopnav != null) {
                btnBackTopnav.setVisibility(View.GONE);  // Ẩn nút
            }

            // Khôi phục lại nội dung "txt_app_name"
            TextView txtAppName = fragmentActivity.findViewById(R.id.txt_app_name);
            if (txtAppName != null) {
                txtAppName.setText(R.string.app_name);  // Khôi phục về tên app gốc
            }
        }
    }
    private void addToCart(int foodId, int userId, int quantity) {
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
                        addFoodToCartItem(cartId, foodId, quantity);
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
                                    addFoodToCartItem(cartId, foodId, quantity);
                                } else {
                                    Toast.makeText(getContext(), "Tạo mới giỏ hàng thất bại", Toast.LENGTH_SHORT).show();
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
    private void addFoodToCartItem(int cartId, int foodId, int quantity) {
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
                        cartItemId = currentItemId;
                    }

                    // Kiểm tra nếu món ăn đã có
                    if (itemSnapshot.child("foodId").getValue(Integer.class) == foodId) {
                        itemExists = true;
                        Toast.makeText(requireContext(), "Đã có sản phẩm trong giỏ hàng. Đã cập nhật số lượng", Toast.LENGTH_SHORT).show();
                        int existingQuantity = itemSnapshot.child("quantity").getValue(Integer.class);
                        // Tăng số lượng
                        itemSnapshot.getRef().child("quantity").setValue(existingQuantity + quantity);
                        return;
                    }
                }

                // Nếu món ăn chưa có trong giỏ, thêm mới
                if (!itemExists) {
                    cartItemId++; // Tăng cartItemId lên 1
                    // Tạo đối tượng CartItem mới
                    CartItem newItem = new CartItem(cartItemId, cartId, foodId, quantity);
                    // Thêm mới vào CartItem
                    cartItemRef.child(String.valueOf(cartItemId)).setValue(newItem)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Toast.makeText(requireContext(), "Thêm món ăn vào giỏ thành công!", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(requireContext(), "Thêm món ăn vào giỏ thất bại!", Toast.LENGTH_SHORT).show();
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