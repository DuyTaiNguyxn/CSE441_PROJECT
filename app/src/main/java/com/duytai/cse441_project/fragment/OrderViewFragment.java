package com.duytai.cse441_project.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.duytai.cse441_project.R;
import com.duytai.cse441_project.adapter.OrderViewAdapter;
import com.duytai.cse441_project.model.Order;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class OrderViewFragment extends Fragment {

    private RecyclerView recyclerView;
    private OrderViewAdapter orderViewAdapter;
    private List<Order> orderList; // Khai báo biến danh sách đơn hàng
    private DatabaseReference orderRef;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order_detail, container, false);
        // Hiển thị nút back
        FragmentActivity activity = getActivity();
        if (activity != null) {
            activity.findViewById(R.id.btn_back_Topnav).setVisibility(View.VISIBLE);
        }else {
            Log.e("OrderViewFragment", "onCreateView: activity is null");
        }
        // Xử lý nút back
        activity.findViewById(R.id.btn_back_Topnav).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getParentFragmentManager();
                fragmentManager.popBackStack();
                // Ẩn nút back
                activity.findViewById(R.id.btn_back_Topnav).setVisibility(View.GONE);
            }
        });

        // Ánh xạ RecyclerView
        recyclerView = view.findViewById(R.id.rcv_order_detail);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Khởi tạo danh sách đơn hàng
        orderList = new ArrayList<>(); // Thêm dòng này để khởi tạo orderList

        // Khởi tạo adapter
        orderViewAdapter = new OrderViewAdapter(orderList); // Khởi tạo adapter với orderList
        recyclerView.setAdapter(orderViewAdapter); // Gán adapter cho RecyclerView

        // Lấy ra Userid
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("currentUserId", requireActivity().MODE_PRIVATE);
        Integer userId = sharedPreferences.getInt("userId", -1);

        // Lấy dữ liệu từ Firebase
        orderRef = FirebaseDatabase.getInstance().getReference("Order");
        orderRef.orderByChild("userId").equalTo(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                orderList.clear(); // Xóa dữ liệu cũ
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Order order = dataSnapshot.getValue(Order.class);
                    orderList.add(order); // Thêm đơn hàng vào danh sách
                }
                orderViewAdapter.notifyDataSetChanged(); // Cập nhật lại RecyclerView
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("OrderViewFragment", "onCancelled: " + error.getMessage());
            }
        });

        return view;
    }

}
