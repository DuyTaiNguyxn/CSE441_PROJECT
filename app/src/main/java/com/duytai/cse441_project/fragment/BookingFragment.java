package com.duytai.cse441_project.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.duytai.cse441_project.R;
import com.duytai.cse441_project.adapter.StoreAdapter;
import com.duytai.cse441_project.model.Store;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class BookingFragment extends Fragment {

    private RecyclerView recyclerView;
    private StoreAdapter storeAdapter;
    private DatabaseReference databaseReference;

    private ArrayList<Store> storeList = new ArrayList<>();
    private Map<Integer, Integer> availableTablesMap = new HashMap<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_booking, container, false);

        recyclerView = view.findViewById(R.id.rcv_store);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(),2));
        storeAdapter = new StoreAdapter(storeList, new StoreAdapter.OnBookButtonClickListener() {
            @Override
            public void onBookButtonClick(Store store) {
                if(store.getAvailableTables() == 0) {
                    Toast.makeText(getActivity(), "Cơ sở đã hết bàn trống.", Toast.LENGTH_LONG).show();
                } else {
                    // Tạo một instance của ReservationFragment
                    ReservationFragment reservationFragment = new ReservationFragment();

                    // Truyền dữ liệu cần thiết nếu có
                    Bundle bundle = new Bundle();
                    bundle.putInt("storeId", store.getStoreId()); // Thay thế với dữ liệu mà bạn cần
                    reservationFragment.setArguments(bundle);

                    // Thực hiện điều hướng đến ReservationFragment
                    requireActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragmentContainerView, reservationFragment) // Thay thế R.id.fragment_container với ID của container chứa fragments
                            .addToBackStack(null) // Thêm vào back stack để có thể quay lại
                            .commit();
                }
            }
        });
        recyclerView.setAdapter(storeAdapter);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        fetchStoreData();
        return view;
    }

    private void fetchStoreData() {
        // Lấy dữ liệu từ bảng `tables` để tính số bàn còn trống
        databaseReference.child("TableInfo").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot tableSnapshot : snapshot.getChildren()) {
                    int storeId = tableSnapshot.child("storeId").getValue(Integer.class);
                    String status = tableSnapshot.child("status").getValue(String.class);

                    if ("available".equals(status)) {
                        availableTablesMap.put(storeId, availableTablesMap.getOrDefault(storeId, 0) + 1);
                    }
                }
                fetchStores();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });
    }

    private void fetchStores() {
        databaseReference.child("Store").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                storeList.clear();
                for (DataSnapshot storeSnapshot : snapshot.getChildren()) {
                    Store store = storeSnapshot.getValue(Store.class);
                    int availableTables = availableTablesMap.getOrDefault(store.getStoreId(), 0);
                    store.setAvailableTables(availableTables);  // Gán số bàn còn lại

                    storeList.add(store);
                }
                storeAdapter.notifyDataSetChanged();  // Cập nhật RecyclerView
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });
    }
}
