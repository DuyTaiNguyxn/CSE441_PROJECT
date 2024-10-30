package com.duytai.cse441_project.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.duytai.cse441_project.R;
import com.duytai.cse441_project.model.CartItem;
import com.duytai.cse441_project.model.Food;
import com.duytai.cse441_project.model.Order;
import com.duytai.cse441_project.model.OrderItem;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class OrderFragment extends Fragment {
    private ImageButton btnBackTopnav;
    private TextView txtAppName,txtSelectLocation;
    private Button btnPlaceOrder;
    private DatabaseReference orderRef;
    private EditText edtName, edtPhone, edtAddress;
    private String Orderphone, Orderaddress, ordername, discountCode;
    private double totalPrice, discountPrice;
    private ArrayList<CartItem> cartItems;
    private ArrayList<Food> foodList;
    private SharedPreferences sharedPreferences;
    private long OrderTime;
    private int maxOrderItemId, maxOrderId;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Nhận dữ liệu từ bundle
        Bundle bundle = getArguments();
        if (bundle != null) {
            discountCode = bundle.getString("foodId");
            discountPrice = bundle.getDouble("discountPrice");
            totalPrice = bundle.getDouble("totalPrice");
        }
        cartItems = new ArrayList<>();
        foodList = new ArrayList<>();

        return inflater.inflate(R.layout.fragment_comfirm_order, container, false);
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Hiển thị nút "btn_back_Topnav"
        FragmentActivity fragmentActivity = getActivity();
        ImageButton btnBackTopnav = fragmentActivity.findViewById(R.id.btn_back_Topnav);
        TextView txtAppName = fragmentActivity.findViewById(R.id.txt_app_name);
        if (fragmentActivity != null) {
            if (btnBackTopnav != null) {
                btnBackTopnav.setVisibility(View.VISIBLE);
                txtAppName.setText("Thanh toán");
            }
        }
        btnBackTopnav.setOnClickListener(v -> {
            // Xử lý sự kiện nhấn nút "btn_back_Topnav"
            FragmentManager fragmentManager = fragmentActivity.getSupportFragmentManager();
            fragmentManager.popBackStack(); // Quay lại Fragment trước đó
        });

        // Xử lý sự kiện nhấn nút "btn_PlaceOrder"
        btnPlaceOrder = view.findViewById(R.id.bt_comfirm_order);
        btnPlaceOrder.setOnClickListener(v -> {
            // Xử lý sự kiện nhấn nút "btn_PlaceOrder"
            CreateOrder();
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        FragmentActivity fragmentActivity = getActivity();
        if (fragmentActivity != null) {
            // Hiện lại nút "btn_back_Topnav"
            ImageButton btnBackTopnav = fragmentActivity.findViewById(R.id.btn_back_Topnav);
            if (btnBackTopnav != null) {
                btnBackTopnav.setVisibility(View.GONE);
            }

            // Khôi phục lại nội dung "txt_app_name"
            TextView txtAppName = fragmentActivity.findViewById(R.id.txt_app_name);
            if (txtAppName != null) {
                txtAppName.setText(R.string.app_name);
            }
        }
    }

    private void CreateOrder() {
        // Truy xuất giao diện
        orderRef = FirebaseDatabase.getInstance().getReference("Order");
        edtName = getView().findViewById(R.id.edt_name_comfirm_order);
        edtPhone = getView().findViewById(R.id.edt_phone_comfirm_order);
        edtAddress = getView().findViewById(R.id.edt_address_order);

        // Lấy thông tin người dùng
        ordername = edtName.getText().toString();
        Orderphone = edtPhone.getText().toString();
        Orderaddress = edtAddress.getText().toString();

        // Tạo thời gian đặt hàng
        OrderTime = System.currentTimeMillis();
        // Định dạng ngày tháng năm
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String formattedDate = sdf.format(new Date(OrderTime));

        // Kiểm tra thông tin
        if (ordername.isEmpty() || Orderphone.isEmpty() || Orderaddress.isEmpty()) {
            Toast.makeText(getContext(), "Vui lòng nhập đầy đủ thông tin.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Lấy thông tin người dùng
        sharedPreferences = requireContext().getSharedPreferences("currentUserId", Context.MODE_PRIVATE);
        int userId = sharedPreferences.getInt("userId", -1);
        String Status = "Đang xử lý";
        String PaymentMethod = "Tiền mặt";
        // Tìm ID lớn nhất
        orderRef.orderByKey().limitToLast(1).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                // Lấy ID lớn nhất từ Firebase
                maxOrderId = 0; // Khởi tạo ID lớn nhất
                for (DataSnapshot snapshot : task.getResult().getChildren()) {
                    int orderId = Integer.parseInt(snapshot.getKey());
                    if (orderId > maxOrderId) {
                        maxOrderId = orderId; // Cập nhật ID lớn nhất
                    }
                }
                // Tạo ID mới bằng cách cộng 1
                int orderId = maxOrderId + 1;
                // Tạo đơn hàng
                Order order = new Order(orderId, userId, ordername, Orderphone, Orderaddress, formattedDate, Status, totalPrice, PaymentMethod, discountCode);
                orderRef.child(String.valueOf(orderId)).setValue(order).addOnCompleteListener(orderTask -> {
                    if (orderTask.isSuccessful()) {
                        // Chỉ sau khi đơn hàng đã được lưu thành công mới bắt đầu tạo OrderItems
                        String strUserId = String.valueOf(userId);
                        createOrderItems(orderId, strUserId);
                    } else {
                        Toast.makeText(getContext(), "Không thể tạo đơn hàng", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(getContext(), "Không thể lấy ID từ Firebase", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void createOrderItems(int orderId, String strUserId){
        DatabaseReference orderItemRef = FirebaseDatabase.getInstance().getReference("OrderItem");
        // Tìm ID lớn nhất
        orderItemRef.orderByKey().limitToLast(1).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                // Lấy ID lớn nhất từ Firebase
                maxOrderItemId = 0; // Khởi tạo ID lớn nhất
                for (DataSnapshot snapshot : task.getResult().getChildren()) {
                    int orderItemId = Integer.parseInt(snapshot.getKey());
                    if (orderItemId > maxOrderItemId) {
                        maxOrderItemId = orderItemId; // Cập nhật ID lớn nhất
                    }
                }
            }

        });
        // Lấy danh sách món ăn trong giỏ hàng
        DatabaseReference cartItemRef = FirebaseDatabase.getInstance().getReference("CartItem");
        cartItemRef.orderByChild("cartId").equalTo(Integer.parseInt(strUserId)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot cartSnapshot : snapshot.getChildren()) {
                    CartItem cartItem = cartSnapshot.getValue(CartItem.class);
                    cartItems.add(cartItem);
                    // Lấy thông tin món ăn từ Firebase
                    DatabaseReference foodRef = FirebaseDatabase.getInstance().getReference("Food").child(String.valueOf(cartItem.getFoodId()));
                    foodRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot foodSnapshot) {
                            Food food = foodSnapshot.getValue(Food.class);
                            if (food != null) {
                                foodList.add(food);
                                // Tạo OrderItem
                                maxOrderItemId++;
                                OrderItem orderItem = new OrderItem(maxOrderItemId, orderId, food.getFoodId(), food.getPrice(), cartItem.getQuantity());
                                orderItemRef.child(String.valueOf(maxOrderItemId)).setValue(orderItem);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(getContext(), "Lỗi khi lấy thông tin món ăn", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                // Xóa giỏ hàng sau khi đã đặt hàng
                cartItemRef.orderByChild("cartId").equalTo(Integer.parseInt(strUserId)).get().addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        for (DataSnapshot cartSnapshot : task.getResult().getChildren()) {
                            cartSnapshot.getRef().removeValue();
                        }
                    }
                });
                // Hiển thị thông báo
                Toast.makeText(getContext(), "Đặt hàng thành công", Toast.LENGTH_SHORT).show();
                // Quay về Home Fragment
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainerView, new HomeFragment()) // Thay 'R.id.fragment_container' bằng ID container của bạn
                        .commit();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Lỗi khi lấy thông tin giỏ hàng", Toast.LENGTH_SHORT).show();
            }
        });

    }

}