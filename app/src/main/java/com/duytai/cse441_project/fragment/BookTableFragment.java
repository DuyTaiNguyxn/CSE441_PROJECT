package com.duytai.cse441_project.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.duytai.cse441_project.R;
import com.duytai.cse441_project.adapter.TableBookingAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class BookTableFragment extends Fragment {

    public BookTableFragment() {
        // Bắt buộc phải có constructor rỗng
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_book_table, container, false);

        TabLayout tabLayout = view.findViewById(R.id.tabLayout);
        ViewPager2 viewPager = view.findViewById(R.id.viewPager);

        // Thiết lập adapter cho ViewPager2
        TableBookingAdapter adapterBooking = new TableBookingAdapter(this);
        viewPager.setAdapter(adapterBooking);

        // Đồng bộ TabLayout với ViewPager2
        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> {
                    if (position == 0) {
                        tab.setText("Đặt bàn");
                    } else {
                        tab.setText("Bàn đã đặt");
                    }
                }).attach();

        return view;
    }
}