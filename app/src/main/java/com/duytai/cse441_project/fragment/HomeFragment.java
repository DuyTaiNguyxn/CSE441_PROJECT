package com.duytai.cse441_project.fragment;

// HomeFragment.java
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.duytai.cse441_project.R;
import com.duytai.cse441_project.adapter.FoodAdapter;
import com.duytai.cse441_project.model.Food;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private RecyclerView recyclerTopSale, recyclerNewFood, recyclerCombo;
    private FoodAdapter topSaleAdapter, newFoodAdapter, comboAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerTopSale = view.findViewById(R.id.recyclerTopSale);
        recyclerNewFood = view.findViewById(R.id.recyclerNewFood);
        recyclerCombo = view.findViewById(R.id.recyclerCombo);

        // Giả lập dữ liệu
        List<Food> topSaleList = new ArrayList<>();
        topSaleList.add(new Food("1", "Cánh gà rán", "Ngon tuyệt", "https://storage.googleapis.com/chickengangapp.appspot.com/images/C%C3%A1nh%20g%C3%A0%20r%C3%A1n.jpg", 20000));
        topSaleList.add(new Food("2", "Cánh gà rán", "Ngon tuyệt", "https://storage.googleapis.com/chickengangapp.appspot.com/images/C%C3%A1nh%20g%C3%A0%20r%C3%A1n.jpg", 20000));
        topSaleList.add(new Food("3", "Cánh gà rán", "Ngon tuyệt", "https://storage.googleapis.com/chickengangapp.appspot.com/images/C%C3%A1nh%20g%C3%A0%20r%C3%A1n.jpg", 20000));
        topSaleList.add(new Food("4", "Cánh gà rán", "Ngon tuyệt", "https://storage.googleapis.com/chickengangapp.appspot.com/images/C%C3%A1nh%20g%C3%A0%20r%C3%A1n.jpg", 20000));

        // Cài đặt Adapter cho RecyclerView
        topSaleAdapter = new FoodAdapter(getContext(), topSaleList);
        recyclerTopSale.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerTopSale.setAdapter(topSaleAdapter);

        // Tương tự cho newFoodAdapter và comboAdapter...

        return view;
    }
}
