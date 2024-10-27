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
    private TextView txtTotalPrice,txtemptyCart,txtSelectDisscount,txtDisscountPrice;
    private EditText edtDisscount;
    private Button btn_PlaceOrder;
    private double totalPrice = 0;
    private Context context;
    private SharedPreferences sharedPreferences;



    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cart, container, false);

        recyclerView = view.findViewById(R.id.rcl_cart);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        cartItemList = new ArrayList<>();
        cartAdapter = new CartAdapter(requireContext(),cartItemList);
        recyclerView.setAdapter(cartAdapter);
        txtTotalPrice = view.findViewById(R.id.txt_total_price);
        txtemptyCart = view.findViewById(R.id.txt_temp_price);
        txtDisscountPrice = view.findViewById(R.id.txt_discount_price);
        txtSelectDisscount = view.findViewById(R.id.txt_select_discount);
        edtDisscount = view.findViewById(R.id.edt_discount_code);
        btn_PlaceOrder = view.findViewById(R.id.btn_OrderFood);

        loadCartItems();
        btn_PlaceOrder.setOnClickListener(v -> {
            if (cartItemList.isEmpty()) {
                Toast.makeText(getContext(), "Giỏ hàng trống. Vui lòng thêm món ăn vào giỏ hàng.", Toast.LENGTH_SHORT).show();
            } else {
                OrderFragment orderFragment = new OrderFragment();
                FragmentManager fragmentManager = getParentFragmentManager(); // Hoặc getSupportFragmentManager() nếu trong Activity
                fragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainerView, orderFragment) //
                        .addToBackStack(null) // Thêm vào back stack nếu cần
                        .commit();
            }
        });


        return view;
    }

    private void loadCartItems() {
        sharedPreferences = requireContext().getSharedPreferences("currentUserId", Context.MODE_PRIVATE);
        int userId = sharedPreferences.getInt("userId", -1);
        DatabaseReference cartItemRef = FirebaseDatabase.getInstance().getReference("CartItem");

        cartItemRef.orderByChild("cartId").equalTo(userId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        cartItemList.clear();
                        totalPrice = 0; // Reset tổng giá trước khi tính lại

                        for (DataSnapshot cartSnapshot : snapshot.getChildren()) {
                            CartItem cartItem = cartSnapshot.getValue(CartItem.class);
                            cartItemList.add(cartItem);

                            // Lấy giá tiền của món ăn từ Firebase
                            DatabaseReference foodRef = FirebaseDatabase.getInstance().getReference("Food").child(String.valueOf(cartItem.getFoodId()));
                            foodRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot foodSnapshot) {
                                    Food food = foodSnapshot.getValue(Food.class);
                                    if (food != null) {
                                        double itemPrice = food.getPrice() * cartItem.getQuantity(); // Tính giá cho mỗi món
                                        totalPrice += itemPrice;
                                        txtemptyCart.setText(totalPrice+"");
                                        // Xu ly giam gia o day (Chua lam)
                                        txtDisscountPrice.setText("0");
                                        double disscountPrice = txtDisscountPrice.getText().toString().isEmpty() ? 0 : Double.parseDouble(txtDisscountPrice.getText().toString());
                                        txtTotalPrice.setText((totalPrice - disscountPrice)+""); // Cập nhật tổng giá

                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Toast.makeText(context, "Xảy ra lỗi khi tính tổng tiền", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                        cartAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(context, "Xảy ra lỗi khi tải giỏ hàng!", Toast.LENGTH_SHORT).show();
                    }
                });
    }




}
