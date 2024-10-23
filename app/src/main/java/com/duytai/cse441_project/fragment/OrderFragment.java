package com.duytai.cse441_project.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.duytai.cse441_project.R;

public class OrderFragment extends Fragment {
    private ImageButton btnBackTopnav;
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_comfirm_order, container, false);
    }
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Hiện nút "btn_back_Topnav"
        FragmentActivity fragmentActivity = getActivity();
        ImageButton btnBackTopnav = fragmentActivity.findViewById(R.id.btn_back_Topnav);
        TextView txtAppName = fragmentActivity.findViewById(R.id.txt_app_name);
        if (fragmentActivity != null) {
            if (btnBackTopnav != null) {
                btnBackTopnav.setVisibility(View.VISIBLE);
                txtAppName.setText("Thanh toán");
            }
        }
        btnBackTopnav.setOnClickListener(v -> {
            // Xử lý sự kiện nhấn nút "btn_back_Topnav"
            FragmentManager fragmentManager = fragmentActivity.getSupportFragmentManager();
            fragmentManager.popBackStack(); // Quay lại Fragment trước đó
        });
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        FragmentActivity fragmentActivity = getActivity();
        if (fragmentActivity != null) {
            // Hiện lại nút "btn_back_Topnav"
            ImageButton btnBackTopnav = fragmentActivity.findViewById(R.id.btn_back_Topnav);
            if (btnBackTopnav != null) {
                btnBackTopnav.setVisibility(View.GONE);
            }

            // Khôi phục lại nội dung "txt_app_name"
            TextView txtAppName = fragmentActivity.findViewById(R.id.txt_app_name);
            if (txtAppName != null) {
                txtAppName.setText(R.string.app_name);
            }
        }
    }
}
