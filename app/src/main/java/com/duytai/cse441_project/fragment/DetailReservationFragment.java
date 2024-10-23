package com.duytai.cse441_project.fragment;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
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

        icDirection.setOnClickListener(v -> openMap());
        icCall.setOnClickListener(v -> makePhoneCall());

        btnCancelReservation.setOnClickListener(v -> {
            new AlertDialog.Builder(getContext())
                    .setTitle("Xác nhận huỷ")
                    .setMessage("Bạn có chắc muốn huỷ đặt bàn này không?")
                    .setPositiveButton("Đồng ý", (dialog, which) -> {
                        deleteReservationAndUpdateTableInfo(reservation);
                    })
                    .setNegativeButton("Hủy bỏ", (dialog, which) -> dialog.dismiss())
                    .show();
        });

        return view;
    }

    private void openMap() {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(reservation.getStoreLocation()));
        if (intent.resolveActivity(requireActivity().getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Toast.makeText(getActivity(), "Không tìm thấy ứng dụng bản đồ", Toast.LENGTH_SHORT).show();
        }
    }

    private void makePhoneCall() {
        if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.CALL_PHONE)
                == PackageManager.PERMISSION_GRANTED) {
            // Quyền đã được cấp, thực hiện cuộc gọi
            startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + reservation.getStorePhone())));
        } else {
            // Yêu cầu cấp quyền nếu chưa có
            requestPermissions(new String[]{android.Manifest.permission.CALL_PHONE}, 1);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) { // Mã yêu cầu CALL_PHONE
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Người dùng đã cấp quyền -> Thực hiện cuộc gọi
                makePhoneCall();
            } else {
                // Người dùng từ chối quyền
                Toast.makeText(getContext(), "Quyền gọi điện đã bị từ chối", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void deleteReservationAndUpdateTableInfo(Reservation reservation) {
        DatabaseReference reservationRef = FirebaseDatabase.getInstance().getReference("Reservation")
                .child(String.valueOf(reservation.getReservationId()));

        reservationRef.removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DatabaseReference tableInfoRef = FirebaseDatabase.getInstance().getReference("TableInfo")
                        .child(String.valueOf(reservation.getTableInfoId()));

                tableInfoRef.child("status").setValue("available")
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
