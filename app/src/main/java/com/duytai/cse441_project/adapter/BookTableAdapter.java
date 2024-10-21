package com.duytai.cse441_project.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.duytai.cse441_project.fragment.ReservationFragment;
import com.duytai.cse441_project.fragment.StoreFragment;

public class BookTableAdapter extends FragmentStateAdapter {

    public BookTableAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0) {
            return new StoreFragment();  // Tab Đặt bàn
        } else {
            return new ReservationFragment();   // Tab Bàn đã đặt
        }
    }

    @Override
    public int getItemCount() {
        return 2;  // Có 2 tab
    }
}

