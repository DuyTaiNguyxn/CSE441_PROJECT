package com.duytai.cse441_project.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.duytai.cse441_project.R;
import com.duytai.cse441_project.adapter.ReservationAdapter;
import com.duytai.cse441_project.model.Reservation;
import com.duytai.cse441_project.model.Store;
import com.duytai.cse441_project.model.TableInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.ArrayList;

public class ReservationFragment extends Fragment {
    private RecyclerView rcvReservation;
    private ReservationAdapter reservationAdapter;
    private ArrayList<Reservation> reservationList = new ArrayList<>();

    private DatabaseReference reservationReference;
    private DatabaseReference tableInfoReference;
    private DatabaseReference storeReference;
    private int userId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate layout của Fragment
        View view = inflater.inflate(R.layout.fragment_booked, container, false);

        rcvReservation = view.findViewById(R.id.rcv_booked_table);
        rcvReservation.setLayoutManager(new LinearLayoutManager(getContext()));

        // Khởi tạo Adapter và gán vào RecyclerView
        reservationAdapter = new ReservationAdapter(reservationList,
                new ReservationAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(Reservation reservation) {
                        // Tạo instance của DetailReservationFragment
                        DetailReservationFragment detailReservationFragment = new DetailReservationFragment();

                        // Tạo Bundle và truyền đối tượng Reservation
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("reservation", (Serializable) reservation);

                        // Gán Bundle vào Fragment
                        detailReservationFragment.setArguments(bundle);

                        // Thực hiện chuyển Fragment
                        requireActivity().getSupportFragmentManager().beginTransaction()
                                .replace(R.id.fragmentContainerView, detailReservationFragment)
                                .addToBackStack(null)
                                .commit();
                    }
                }
        );

        rcvReservation.setAdapter(reservationAdapter);

        userId = 0; // Hoặc gán userId phù hợp nếu cần
        reservationReference = FirebaseDatabase.getInstance().getReference("Reservation");
        tableInfoReference = FirebaseDatabase.getInstance().getReference("TableInfo");
        storeReference = FirebaseDatabase.getInstance().getReference("Store");
        reservationReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                reservationList.clear(); // Xóa danh sách cũ

                for (DataSnapshot reservationSnapshot : snapshot.getChildren()) {
                    // Kiểm tra nếu reservationSnapshot không phải là null
                    if (reservationSnapshot.exists() && reservationSnapshot.getValue() != null) {
                        Reservation reservationItem = reservationSnapshot.getValue(Reservation.class);
                        if (reservationItem != null && reservationItem.getUserId() == userId) {
                            fetchTableInfo(reservationItem);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("ReservationFragment", "Error fetching reservation data", databaseError.toException());
            }
        });

        return view;
    }

    private void fetchTableInfo(Reservation reservationItem) {
        int tableInfoId = reservationItem.getTableInfoId();
        tableInfoReference.child(String.valueOf(tableInfoId)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot tableInfoSnapshot) {
                if (tableInfoSnapshot.exists()) {
                    TableInfo tableInfo = tableInfoSnapshot.getValue(TableInfo.class);
                    if (tableInfo != null) {
                        fetchStoreInfo(reservationItem, tableInfo);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("TableInfo", "Error fetching table info", databaseError.toException());
            }
        });
    }

    private void fetchStoreInfo(Reservation reservationItem, TableInfo tableInfo) {
        int storeId = tableInfo.getStoreId();
        storeReference.child(String.valueOf(storeId)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot storeSnapshot) {
                if (storeSnapshot.exists()) {
                    Store store = storeSnapshot.getValue(Store.class);
                    if (store != null) {
                        reservationItem.setStoreName(store.getStoreName());
                        reservationItem.setStoreLocation(store.getLocationLink());
                        reservationItem.setStorePhone(store.getPhone());
                        reservationItem.setTableSeats(tableInfo.getSeats());
                        reservationItem.setStoreImageUrl(store.getImgURL());
                        reservationList.add(reservationItem); // Thêm mục vào danh sách
                    }
                }
                reservationAdapter.notifyDataSetChanged(); // Cập nhật Adapter
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("StoreInfo", "Error fetching store data", databaseError.toException());
            }
        });
    }

}

