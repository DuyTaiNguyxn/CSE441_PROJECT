package com.duytai.cse441_project.fragment;

import android.os.Bundle;
import android.util.Log;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class BookingFragment extends Fragment {

    private RecyclerView recyclerStore;
    private StoreAdapter storeAdapter;
    private List<Store> storeList;
    private DatabaseReference storeReference;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_booking, container, false);

        // Khởi tạo RecyclerView và Adapter
        recyclerStore = view.findViewById(R.id.rcv_store);
        recyclerStore.setLayoutManager(new GridLayoutManager(getContext(),2));

        storeList = new ArrayList<>();
        storeAdapter = new StoreAdapter(getContext(), storeList);
        recyclerStore.setAdapter(storeAdapter);

        // Tham chiếu tới Firebase Database
        storeReference = FirebaseDatabase.getInstance().getReference("Store");

        // Lấy dữ liệu từ Firebase
        storeReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                storeList.clear();
                for (DataSnapshot storeSnapshot : snapshot.getChildren()) {
                    Store store = storeSnapshot.getValue(Store.class);
                    if (store != null) {
                        storeList.add(store);
                    }
                }
                storeAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("StoreFragment", "Error fetching store data", error.toException());
            }
        });

        return view;
    }
}
