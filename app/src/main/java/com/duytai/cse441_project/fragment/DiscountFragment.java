package com.duytai.cse441_project.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.duytai.cse441_project.R;
import com.duytai.cse441_project.adapter.DiscountAdapter;
import com.duytai.cse441_project.model.Discount;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DiscountFragment extends Fragment {
    private RecyclerView rcvDiscount;
    private DiscountAdapter discountAdapter;
    private List<Discount> discountList = new ArrayList<>();
    private Button btnConfirmDiscount;
    private int selectedDiscountId;
    private SharedPreferences sharedPreferences;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_discount, container, false);

        rcvDiscount = view.findViewById(R.id.rcv_discount);
        rcvDiscount.setLayoutManager(new LinearLayoutManager(this.getContext(), LinearLayoutManager.VERTICAL, false));

        sharedPreferences = requireContext().getSharedPreferences("currentUserId", Context.MODE_PRIVATE);
        int userId = sharedPreferences.getInt("userId", -1);
        loadDiscountsForUser(userId);
        discountAdapter = new DiscountAdapter(this.getContext(),discountList, this);
        rcvDiscount.setAdapter(discountAdapter);

        btnConfirmDiscount = view.findViewById(R.id.btn_confirm_discount);
        btnConfirmDiscount.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putSerializable("selectedDiscount", getDiscountById(selectedDiscountId));
            getParentFragmentManager().setFragmentResult("requestKey", bundle);
            getParentFragmentManager().popBackStack();
        });

        return view;
    }

    public void setSelectedDiscountId(int discountId){
        selectedDiscountId = discountId;
    }

    private Discount getDiscountById(int discountId) {
        for (Discount discount : discountList) {
            if (discount.getDiscountId() == discountId) {
                return discount;  // Trả về Discount nếu tìm thấy
            }
        }
        return null;  // Trả về null nếu không tìm thấy
    }

    private void loadDiscountsForUser(int userId) {
        DatabaseReference userDiscountRef = FirebaseDatabase.getInstance().getReference("User-Discount");

        userDiscountRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                discountList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Long snapshotUserId = snapshot.child("userId").getValue(Long.class);
                    if (snapshotUserId != null && snapshotUserId.intValue() == userId) {  // Kiểm tra userId
                        Long discountIdLong = snapshot.child("discountId").getValue(Long.class);
                        if (discountIdLong != null) {  // Kiểm tra discountId không phải là null
                            int discountId = discountIdLong.intValue();  // Chuyển đổi Long thành int
                            loadDiscountDetails(discountId);  // Truyền discountId dưới dạng int
                        } else {
                            Log.e("DiscountFragment", "Discount ID is null for user " + userId);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("DiscountFragment", "Database error: " + databaseError.getMessage());
            }
        });
    }

    private void loadDiscountDetails(int discountId) {
        DatabaseReference discountRef = FirebaseDatabase.getInstance().getReference("Discount").child(String.valueOf(discountId));

        discountRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Discount discount = dataSnapshot.getValue(Discount.class);
                if (discount != null) {
                    discountList.add(discount);  // Thêm discount vào danh sách
                    discountAdapter.notifyDataSetChanged();  // Cập nhật adapter
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Xử lý lỗi nếu có
            }
        });
    }

}
