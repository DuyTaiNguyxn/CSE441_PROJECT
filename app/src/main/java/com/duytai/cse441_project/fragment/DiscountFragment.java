package com.duytai.cse441_project.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.duytai.cse441_project.R;
import com.duytai.cse441_project.adapter.DiscountAdapter;
import com.duytai.cse441_project.model.Discount;

import java.util.ArrayList;
import java.util.List;

public class DiscountFragment extends Fragment {
    private RecyclerView rcvDiscount;
    private DiscountAdapter discountAdapter;
    private List<Discount> discountList = new ArrayList<>();
    private Button btnConfirmDiscount;
    private int selectedDiscountId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_discount, container, false);

        rcvDiscount = view.findViewById(R.id.rcv_discount);
        rcvDiscount.setLayoutManager(new LinearLayoutManager(this.getContext(), LinearLayoutManager.VERTICAL, false));

        discountList.add( new Discount(1,"NEW20","Giảm 20% cho đơn hàng mới", 0.2,"11/11/2024"));
        discountList.add( new Discount(2,"NEW30","Giảm 20% cho đơn hàng mới", 0.3,"11/11/2024"));
        discountList.add( new Discount(3,"NEW40","Giảm 20% cho đơn hàng mới", 0.4,"11/11/2024"));
        discountAdapter = new DiscountAdapter(this.getContext(),discountList, this);
        rcvDiscount.setAdapter(discountAdapter);

        btnConfirmDiscount = view.findViewById(R.id.btn_confirm_discount);
        btnConfirmDiscount.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putSerializable("selectedDiscount", getDiscountById(selectedDiscountId));
            getParentFragmentManager().setFragmentResult("requestKey", bundle);
            getParentFragmentManager().popBackStack();
        });

        return view;
    }

    public void setSelectedDiscountId(int discountId){
        selectedDiscountId = discountId;
    }

    private Discount getDiscountById(int discountId) {
        for (Discount discount : discountList) {
            if (discount.getDiscountId() == discountId) {
                return discount;  // Trả về Discount nếu tìm thấy
            }
        }
        return null;  // Trả về null nếu không tìm thấy
    }

}
