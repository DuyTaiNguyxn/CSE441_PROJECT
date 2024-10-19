package com.duytai.cse441_project.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.duytai.cse441_project.R;

public class ReservationFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reservation, container, false);

        // Nhận dữ liệu từ bundle
        if (getArguments() != null) {
            int storeId = getArguments().getInt("storeId");
            // Sử dụng storeId nếu cần thiết
        }

        // Thực hiện các thao tác khác trong fragment
        return view;
    }
}