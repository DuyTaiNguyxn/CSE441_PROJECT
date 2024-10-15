package com.duytai.cse441_project.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.duytai.cse441_project.fragment.BookedFragment;
import com.duytai.cse441_project.fragment.BookingFragment;

public class TableBookingAdapter extends FragmentStateAdapter {

    public TableBookingAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0) {
            return new BookingFragment();  // Tab Đặt bàn
        } else {
            return new BookedFragment();   // Tab Bàn đã đặt
        }
    }

    @Override
    public int getItemCount() {
        return 2;  // Có 2 tab
    }
}

