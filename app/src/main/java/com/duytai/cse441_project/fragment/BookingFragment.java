package com.duytai.cse441_project.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.duytai.cse441_project.R;
import com.duytai.cse441_project.adapter.StoreAdapter;
import com.duytai.cse441_project.model.Store;

import java.util.ArrayList;
import java.util.List;

public class BookingFragment extends Fragment {

    public BookingFragment() {
        // Bắt buộc phải có constructor rỗng
    }

    private RecyclerView recyclerView;
    private List<Store> storeList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate layout và lưu vào biến view
        View view = inflater.inflate(R.layout.fragment_booking, container, false);

        // Khởi tạo RecyclerView
        recyclerView = view.findViewById(R.id.rcv_store);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        // Khởi tạo danh sách cơ sở
        storeList = new ArrayList<>();
        storeList.add(new Store("ChickenGang Hoàng Mai", 3, R.drawable.logo));
        storeList.add(new Store("ChickenGang Cầu Giấy", 4, R.drawable.logo));
        storeList.add(new Store("ChickenGang Tây Hồ", 2, R.drawable.logo));

        // Thiết lập adapter cho RecyclerView
        StoreAdapter adapterStore = new StoreAdapter(getContext(), storeList, store -> {
            // Xử lý khi nhấn nút Đặt bàn
        });
        recyclerView.setAdapter(adapterStore);

        return view; // Trả về view sau khi đã thiết lập
    }
}
