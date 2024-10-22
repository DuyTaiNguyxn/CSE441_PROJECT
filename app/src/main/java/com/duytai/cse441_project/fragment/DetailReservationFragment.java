package com.duytai.cse441_project.fragment;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.duytai.cse441_project.R;
import com.duytai.cse441_project.model.Reservation;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DetailReservationFragment extends Fragment {
    private Reservation reservation;
    ImageView icDirection, icCall, storeImage;
    TextView tvStoreName, tvStorePhone, tvDate, tvTime, tvTable, tvNote;
    Button btnCancelReservation;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reservation_detail, container, false);

        storeImage = view.findViewById(R.id.imgStore);
        tvStoreName = view.findViewById(R.id.tv_store);
        icDirection = view.findViewById(R.id.ic_direction);
        icCall = view.findViewById(R.id.ic_call);
        tvStorePhone = view.findViewById(R.id.tv_phone);
        tvDate = view.findViewById(R.id.tv_date);
        tvTime = view.findViewById(R.id.tv_time);
        tvTable = view.findViewById(R.id.tv_table);
        tvNote = view.findViewById(R.id.tv_note);
        btnCancelReservation = view.findViewById(R.id.btn_cancel);

        // Lấy dữ liệu từ Bundle
        if (getArguments() != null) {
            reservation = (Reservation) getArguments().getSerializable("reservation");
        }
        tvStoreName.setText(reservation.getStoreName());
        tvStorePhone.setText(reservation.getStorePhone());
        tvDate.setText(reservation.getReservationDate());
        tvTime.setText(reservation.getReservationTime());
        tvTable.setText(reservation.getTableSeats() + " chỗ");
        tvNote.setText(reservation.getNote());
        Glide.with(getContext())
                .load(reservation.getStoreImageUrl())
                .error(R.drawable.logo)
                .into(storeImage);

        icDirection.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(reservation.getStoreLocation()));
            // sử dụng getActivity() hoặc requireActivity() để lấy Context
            if (intent.resolveActivity(requireActivity().getPackageManager()) != null) {
                startActivity(intent);
            } else {
                // Xử lý khi không có ứng dụng nào phù hợp
                Toast.makeText(getActivity(), "Không tìm thấy ứng dụng bản đồ", Toast.LENGTH_SHORT).show();
            }
        });

        icCall.setOnClickListener( v -> {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + reservation.getStorePhone())); // Đặt dữ liệu là số điện thoại

            // sử dụng getActivity() hoặc requireActivity() để lấy Context
            if (intent.resolveActivity(requireActivity().getPackageManager()) != null) {
                startActivity(intent);
            } else {
                // Xử lý khi không có ứng dụng nào phù hợp
                Toast.makeText(getContext(), "Không tìm thấy ứng dụng gọi điện", Toast.LENGTH_SHORT).show();
            }
        });

        btnCancelReservation.setOnClickListener( v -> {
            new AlertDialog.Builder(getContext())
                    .setTitle("Xác nhận huỷ")
                    .setMessage("Bạn có chắc muốn huỷ đặt bàn này không?")
                    .setPositiveButton("Đồng ý", (dialog, which) -> {
                        deleteReservationAndUpdateTableInfo(reservation);
                    })
                    .setNegativeButton("Hủy bỏ", (dialog, which) -> {
                        dialog.dismiss();
                    })
                    .show();
        });

        return view;
    }

    private void deleteReservationAndUpdateTableInfo(Reservation reservation) {
        DatabaseReference reservationRef = FirebaseDatabase.getInstance().getReference("Reservation")
                .child(String.valueOf(reservation.getReservationId())); // Lấy ID của Reservation để xoá

        // Xóa reservation
        reservationRef.removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // Lấy thông tin TableInfo và cập nhật status
                DatabaseReference tableInfoRef = FirebaseDatabase.getInstance().getReference("TableInfo")
                        .child(String.valueOf(reservation.getTableInfoId()));

                tableInfoRef.child("status").setValue("available") // Đặt lại trạng thái bàn
                        .addOnCompleteListener(updateTask -> {
                            if (updateTask.isSuccessful()) {
                                requireActivity().getSupportFragmentManager().popBackStack();
                                Toast.makeText(getContext(), "Đặt bàn đã bị huỷ!", Toast.LENGTH_SHORT).show();
                            } else {
                                Log.e("ReservationFragment", "Lỗi khi cập nhật trạng thái bàn");
                            }
                        });
            } else {
                Log.e("ReservationFragment", "Lỗi khi xoá reservation", task.getException());
            }
        });
    }
}

