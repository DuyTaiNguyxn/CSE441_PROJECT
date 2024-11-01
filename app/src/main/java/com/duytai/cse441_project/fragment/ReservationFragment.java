package com.duytai.cse441_project.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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
    private TextView txtNoReservation;

    private DatabaseReference reservationReference;
    private DatabaseReference tableInfoReference;
    private DatabaseReference storeReference;

    private SharedPreferences sharedPreferences;
    private int userId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate layout của Fragment
        View view = inflater.inflate(R.layout.fragment_reservation, container, false);

        txtNoReservation = view.findViewById(R.id.txt_no_reservation);
        rcvReservation = view.findViewById(R.id.rcv_booked_table);
        rcvReservation.setLayoutManager(new LinearLayoutManager(getContext()));

        // Khởi tạo Adapter và gán vào RecyclerView
        reservationAdapter = new ReservationAdapter(reservationList,
                new ReservationAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(Reservation reservation) {
                        // Xem chi tiết đặt chỗ
                        DetailReservationFragment detailReservationFragment = new DetailReservationFragment();
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("reservation", (Serializable) reservation);
                        detailReservationFragment.setArguments(bundle);
                        requireActivity().getSupportFragmentManager().beginTransaction()
                                .replace(R.id.fragmentContainerView, detailReservationFragment)
                                .addToBackStack(null)
                                .commit();
                    }

                    @Override
                    public void onCancelReservation(Reservation reservation) {
                        // Hiển thị hộp thoại xác nhận
                        new AlertDialog.Builder(getContext())
                                .setTitle("Bàn quá hạn")
                                .setMessage("Bàn của bạn đã quá hạn.")
                                .setPositiveButton("Đóng", null)
                                .setNegativeButton("Chi tiết", (dialog, which) -> {
                                    // Xem chi tiết đặt chỗ
                                    DetailReservationFragment detailReservationFragment = new DetailReservationFragment();
                                    Bundle bundle = new Bundle();
                                    bundle.putSerializable("reservation", reservation);
                                    detailReservationFragment.setArguments(bundle);

                                    requireActivity().getSupportFragmentManager().beginTransaction()
                                            .replace(R.id.fragmentContainerView, detailReservationFragment)
                                            .addToBackStack(null)
                                            .commit();
                                })
                                .show();
                    }

                }
        );

        rcvReservation.setAdapter(reservationAdapter);

        sharedPreferences = requireContext().getSharedPreferences("currentUserId", Context.MODE_PRIVATE);
        userId = sharedPreferences.getInt("userId", -1);
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
                // Kiểm tra sau khi vòng lặp
                if (reservationList.isEmpty()) {
                    txtNoReservation.setVisibility(View.VISIBLE); // Hiện thị thông báo không có đặt chỗ
                } else {
                    txtNoReservation.setVisibility(View.GONE); // Ẩn thông báo
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
                if (reservationList.isEmpty()) {
                    txtNoReservation.setVisibility(View.VISIBLE); // Hiện thị thông báo không có đặt chỗ
                } else {
                    txtNoReservation.setVisibility(View.GONE); // Ẩn thông báo
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

