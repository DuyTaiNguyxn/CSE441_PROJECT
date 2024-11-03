package com.duytai.cse441_project.fragment;

import android.net.ParseException;
import android.os.Bundle;
import android.util.Log;
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
import com.duytai.cse441_project.model.TableInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class StoreFragment extends Fragment {

    private RecyclerView recyclerView;
    private StoreAdapter storeAdapter;
    private DatabaseReference databaseReference;

    private ArrayList<Store> storeList = new ArrayList<>();
    private Map<Integer, ArrayList<TableInfo>> availableTablesMap = new HashMap<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_store, container, false);

        recyclerView = view.findViewById(R.id.rcv_store);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(),2));
        storeAdapter = new StoreAdapter(storeList, new StoreAdapter.OnBookButtonClickListener() {
            @Override
            public void onBookButtonClick(Store store) {
                if (store.getAvailableTables() == 0) {
                    Toast.makeText(getActivity(), "Cơ sở đã hết bàn trống.", Toast.LENGTH_LONG).show();
                } else {
                    try {
                        // Lấy thời gian hiện tại dưới dạng chuỗi chỉ chứa giờ và phút
                        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                        String currentTimeString = sdf.format(Calendar.getInstance().getTime());
                        Date currentTime = sdf.parse(currentTimeString);

                        // Phân tích chuỗi giờ mở và đóng cửa thành Date
                        String openingHours = store.getOpeningHours();
                        String[] hours = openingHours.split(" - ");
                        Date openingTime = sdf.parse(hours[0]);
                        Date closingTime = sdf.parse(hours[1]);

                        // So sánh thời gian
                        if (currentTime.before(openingTime)) {
                            Toast.makeText(getActivity(), "Cơ sở hiện chưa mở cửa.", Toast.LENGTH_LONG).show();
                        } else if (currentTime.after(closingTime)) {
                            Toast.makeText(getActivity(), "Cơ sở hiện đã đóng cửa.", Toast.LENGTH_LONG).show();
                        } else {
                            // Tạo một instance của TableBookingFragment
                            TableBookingFragment tableBookingFragment = new TableBookingFragment();

                            // Truyền dữ liệu
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("storeData", (Serializable) store);
                            bundle.putSerializable("availableTableData", store.getAvailableTableInfoList());

                            tableBookingFragment.setArguments(bundle);

                            // Thực hiện điều hướng đến ReservationFragment
                            requireActivity().getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.fragmentContainerView, tableBookingFragment)
                                    .addToBackStack(null)
                                    .commit();
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                        Toast.makeText(getActivity(), "Lỗi phân tích giờ mở cửa.", Toast.LENGTH_SHORT).show();
                    } catch (java.text.ParseException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });
        recyclerView.setAdapter(storeAdapter);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        fetchStoreData();

        return view;
    }

    private void fetchStoreData() {
        availableTablesMap.clear();
        // Lấy dữ liệu từ bảng `tables` để tính số bàn còn trống
        databaseReference.child("TableInfo").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot tableSnapshot : snapshot.getChildren()) {
                    int storeId = tableSnapshot.child("storeId").getValue(Integer.class);
                    String status = tableSnapshot.child("status").getValue(String.class);

                    if ("available".equals(status)) {
                        // Thêm thông tin bàn vào danh sách bàn có sẵn
                        TableInfo tableInfo = tableSnapshot.getValue(TableInfo.class); // Giả sử TableInfo là một lớp với thông tin bàn
                        if (tableInfo != null) {
                            availableTablesMap.putIfAbsent(storeId, new ArrayList<>()); // Khởi tạo danh sách nếu chưa có
                            availableTablesMap.get(storeId).add(tableInfo); // Thêm thông tin bàn vào danh sách
                        }
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
                    int storeId = store.getStoreId();

                    // Lấy danh sách bàn có sẵn cho cửa hàng
                    ArrayList<TableInfo> availableTables = availableTablesMap.getOrDefault(storeId, new ArrayList<>());
                    store.setAvailableTableInfoList(availableTables);  // Gán danh sách bàn có sẵn cho Store

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
