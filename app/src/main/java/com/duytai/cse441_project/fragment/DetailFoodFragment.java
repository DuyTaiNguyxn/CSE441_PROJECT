package com.duytai.cse441_project.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.bumptech.glide.Glide;
import com.duytai.cse441_project.R;
import com.duytai.cse441_project.model.Food;

public class DetailFoodFragment extends Fragment {


    private Food food;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate layout fragment_detail_food
        return inflater.inflate(R.layout.fragment_food_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Nhận đối tượng Food từ Bundle
        if (getArguments() != null) {
            food = (Food) getArguments().getSerializable("food");
        }

        // Hiển thị thông tin chi tiết
        if (food != null) {
            TextView txtFoodName = view.findViewById(R.id.txt_food_name_detail);
            TextView txtFoodPrice = view.findViewById(R.id.txt_priceFood_detail);
            TextView txtFoodDescription = view.findViewById(R.id.txt_food_description);
            ImageView imgFood = view.findViewById(R.id.img_food_detail);

            // Kiểm tra null trước khi set giá trị
            if (txtFoodName != null) {
                txtFoodName.setText(food.getFoodName());
            }
            if (txtFoodPrice != null) {
                txtFoodPrice.setText(String.format("%.2f VNĐ", food.getPrice()));
            }
            if (txtFoodDescription != null) {
                txtFoodDescription.setText(food.getDescription());
            }
            if (imgFood != null) {
                Glide.with(getContext())
                        .load(food.getImgURL())
                        .into(imgFood);
            }
        }
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        FragmentActivity fragmentActivity = getActivity();
        if (fragmentActivity != null) {
            // Hiện lại nút "btn_back_Topnav"
            ImageButton btnBackTopnav = fragmentActivity.findViewById(R.id.btn_back_Topnav);
            if (btnBackTopnav != null) {
                btnBackTopnav.setVisibility(View.GONE);  // Ẩn nút
            }

            // Khôi phục lại nội dung "txt_app_name"
            TextView txtAppName = fragmentActivity.findViewById(R.id.txt_app_name);
            if (txtAppName != null) {
                txtAppName.setText(R.string.app_name);  // Khôi phục về tên app gốc
            }
        }
    }

}