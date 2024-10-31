package com.duytai.cse441_project.fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.duytai.cse441_project.R;
import com.duytai.cse441_project.adapter.CartAdapter;
import com.duytai.cse441_project.model.CartItem;
import com.duytai.cse441_project.model.Discount;
import com.duytai.cse441_project.model.Food;
import com.duytai.cse441_project.model.Order;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class CartFragment extends Fragment {
    private RecyclerView recyclerView;
    private CartAdapter cartAdapter;
    private List<CartItem> cartItemList;
    private TextView txtTotalPrice, txtTempPrice, txtSelectDisscount, txtDiscountPrice;
    private EditText edtDisscount;
    private Button btn_PlaceOrder;
    private double totalPrice = 0;
    private double discountPercentage = 0;  // Phần trăm giảm giá
    private Context context;
    private SharedPreferences sharedPreferences;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cart, container, false);

        recyclerView = view.findViewById(R.id.rcl_cart);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        cartItemList = new ArrayList<>();
        cartAdapter = new CartAdapter(requireContext(), cartItemList, this::updateCart);  // Truyền callback
        recyclerView.setAdapter(cartAdapter);

        txtTotalPrice = view.findViewById(R.id.txt_total_price);
        txtTempPrice = view.findViewById(R.id.txt_temp_price);
        txtDiscountPrice = view.findViewById(R.id.txt_discount_price);
        txtSelectDisscount = view.findViewById(R.id.txt_select_discount);
        edtDisscount = view.findViewById(R.id.edt_discount_code);
        btn_PlaceOrder = view.findViewById(R.id.btn_OrderFood);

        loadCartItems();

        // Xử lý chọn mã giảm giá
        txtSelectDisscount.setOnClickListener(v -> {
            DiscountFragment discountFragment = new DiscountFragment();
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainerView, discountFragment)
                    .addToBackStack(null)
                    .commit();
        });

        // Nhận kết quả từ DiscountFragment
        getParentFragmentManager().setFragmentResultListener("requestKey", this, (requestKey, result) -> {
            Discount discount = (Discount) result.getSerializable("selectedDiscount");
            if (discount != null) {
                edtDisscount.setText(discount.getDiscountCode());
                discountPercentage = discount.getDiscountPercentage();
                applyDiscount();  // Áp dụng giảm giá ngay
            }
        });

        // Xử lý nút đặt hàng
        btn_PlaceOrder.setOnClickListener(v -> {
            if (cartItemList.isEmpty()) {
                Toast.makeText(getContext(), "Giỏ hàng trống. Vui lòng thêm món ăn vào giỏ hàng.", Toast.LENGTH_SHORT).show();
            } else {
                navigateToConfirmOrderFragment();
            }
        });

        return view;
    }

    private void applyDiscount() {
        double discountPrice = totalPrice * discountPercentage;
        double finalPrice = totalPrice - discountPrice;
        txtDiscountPrice.setText(String.valueOf(discountPrice));
        txtTotalPrice.setText(String.valueOf(finalPrice));
    }

    private void updateCart() {
        totalPrice = 0;  // Reset tổng giá
        for (CartItem item : cartItemList) {
            DatabaseReference foodRef = FirebaseDatabase.getInstance().getReference("Food")
                    .child(String.valueOf(item.getFoodId()));

            foodRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Food food = snapshot.getValue(Food.class);
                    if (food != null) {
                        totalPrice += food.getPrice() * item.getQuantity();
                        txtTempPrice.setText(String.valueOf(totalPrice));
                        applyDiscount();  // Áp dụng giảm giá sau khi cập nhật giá
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(context, "Lỗi khi tính tổng tiền.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
    // Hàm chuyển dữ liệu từ Cart này sang ComfirmOrder
    private void navigateToConfirmOrderFragment() {
        OrderFragment confirmOrderFragment = new OrderFragment();
        //Chuan hoa du lieu
        String discountCode = edtDisscount.getText().toString();
        double discountPrice = Double.parseDouble(txtDiscountPrice.getText().toString());
        double totalPrice = Double.parseDouble(txtTotalPrice.getText().toString());

        // Tạo Bundle và thêm dữ liệu vào đó
        Bundle bundle = new Bundle();
        bundle.putString("discountCode",discountCode );
        bundle.putDouble("discountPrice", discountPrice);
        bundle.putDouble("totalPrice", totalPrice);
        // Gán bundle vào fragment
        confirmOrderFragment.setArguments(bundle);

        // Thực hiện chuyển đổi fragment
        FragmentManager fragmentManager = getParentFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerView, confirmOrderFragment)
                .addToBackStack(null)
                .commit();
    }

    private void loadCartItems() {
        sharedPreferences = requireContext().getSharedPreferences("currentUserId", Context.MODE_PRIVATE);
        int userId = sharedPreferences.getInt("userId", -1);
        DatabaseReference cartItemRef = FirebaseDatabase.getInstance().getReference("CartItem");

        cartItemRef.orderByChild("cartId").equalTo(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                cartItemList.clear();
                for (DataSnapshot cartSnapshot : snapshot.getChildren()) {
                    CartItem cartItem = cartSnapshot.getValue(CartItem.class);
                    if (cartItem != null) {
                        cartItemList.add(cartItem);
                    }
                }
                cartAdapter.notifyDataSetChanged();
                updateCart();  // Cập nhật giá sau khi load dữ liệu
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, "Lỗi khi tải giỏ hàng.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
