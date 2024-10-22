package com.duytai.cse441_project.fragment;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.icu.util.Calendar;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.duytai.cse441_project.R;
import com.duytai.cse441_project.adapter.TableBookingAdapter;
import com.duytai.cse441_project.model.Store;
import com.duytai.cse441_project.model.TableInfo;
import com.duytai.cse441_project.model.Reservation;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import android.widget.Toast;

import java.util.ArrayList;

public class TableBookingFragment extends Fragment {

    private TextView tvStoreName, tvSelectedDate, tvSelectedTime;
    private RecyclerView rcvTable;
    private EditText edtNote;
    private Button btnSubmit;
    private ImageView icDirection, icDatePicker, icTimePicker;

    private TableBookingAdapter tableBookingAdapter;
    private int selectedTableId, userId;
    private String locationLink;
    private DatabaseReference reservationRef;
    private DatabaseReference tableInfoRef;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_booking_table, container, false);

        tvStoreName = view.findViewById(R.id.tv_store_name);
        icDirection = view.findViewById(R.id.ic_direction);
        tvSelectedDate = view.findViewById(R.id.tv_selected_date);
        tvSelectedTime = view.findViewById(R.id.tv_selected_time);
        icDatePicker = view.findViewById(R.id.ic_date_picker);
        icTimePicker = view.findViewById(R.id.ic_time_picker);
        edtNote = view.findViewById(R.id.edt_note);
        btnSubmit = view.findViewById(R.id.btn_submit);

        rcvTable = view.findViewById(R.id.rcv_table);
        LinearLayoutManager tableLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        rcvTable.setLayoutManager(tableLayoutManager);

        Store storeData = null;
        ArrayList<TableInfo> availableTableList = null;
        // Nhận dữ liệu từ bundle
        if (getArguments() != null) {
            storeData = (Store) getArguments().getSerializable("storeData");
            availableTableList = (ArrayList<TableInfo>) getArguments().getSerializable("availableTableData");
        }
        tableBookingAdapter = new TableBookingAdapter(getContext(), availableTableList, this);
        rcvTable.setAdapter(tableBookingAdapter);

        tvStoreName.setText(storeData.getStoreName());
        icDatePicker.setOnClickListener(v -> showDatePickerDialog());
        icTimePicker.setOnClickListener(v -> showTimePickerDialog());

        locationLink = storeData.getLocationLink();
        icDirection.setOnClickListener(v -> goToMap(locationLink));

        reservationRef = FirebaseDatabase.getInstance().getReference("Reservation");
        tableInfoRef = FirebaseDatabase.getInstance().getReference("TableInfo");
        btnSubmit.setOnClickListener(v -> {
            String date = tvSelectedDate.getText().toString();
            String time = tvSelectedTime.getText().toString();
            String note = edtNote.getText().toString();

            if (date.isEmpty() || time.isEmpty()) {
                Toast.makeText(getContext(), "Vui lòng chọn ngày và giờ!", Toast.LENGTH_SHORT).show();
                return;
            }
            if (selectedTableId == 0) { // Kiểm tra xem có bàn nào được chọn hay không
                Toast.makeText(getContext(), "Vui lòng chọn bàn!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Lấy reservationId lớn nhất từ Firebase
            getMaxReservationId(maxId -> {
                int newId = maxId + 1; // Tăng ID lên 11
                userId = 0;

                // Sử dụng Push ID như là reservationId
                Reservation reservation = new Reservation(
                        newId,  // Sử dụng ID mới
                        userId,
                        selectedTableId,
                        date,
                        time,
                        note
                );

                // Lưu thông tin đặt bàn vào Firebase
                reservationRef.child(String.valueOf(newId)).setValue(reservation)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                // Khi đặt bàn thành công, cập nhật status của bàn
                                updateTableStatus(selectedTableId, "occupied");
                                requireActivity().getSupportFragmentManager().popBackStack();
                                Toast.makeText(getContext(), "Đặt bàn thành công!", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getContext(), "Đặt bàn thất bại!", Toast.LENGTH_SHORT).show();
                            }
                        });
            });
        });

        // Thực hiện các thao tác khác trong fragment
        return view;
    }

    private void getMaxReservationId(OnMaxIdReceivedListener listener) {
        reservationRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int maxId = 0;
                for (DataSnapshot reservationSnapshot : snapshot.getChildren()) {
                    Reservation reservation = reservationSnapshot.getValue(Reservation.class);
                    if (reservation != null && reservation.getReservationId() > maxId) {
                        maxId = reservation.getReservationId();
                    }
                }
                listener.onMaxIdReceived(maxId);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("TableBookingFragment", "Failed to read value.", error.toException());
                listener.onMaxIdReceived(0); // Trong trường hợp có lỗi, trả về 0
            }
        });
    }

    // Tạo một interface để nhận ID tối đa
    interface OnMaxIdReceivedListener {
        void onMaxIdReceived(int maxId);
    }

    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(), (view, selectedYear, selectedMonth, selectedDay) -> {
            String selectedDate = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
            tvSelectedDate.setText(selectedDate);
        }, year, month, day);
        datePickerDialog.show();
    }

    private void showTimePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(requireContext(), (view, selectedHour, selectedMinute) -> {
            String selectedTime = String.format("%02d:%02d", selectedHour, selectedMinute);
            tvSelectedTime.setText(selectedTime);
        }, hour, minute, true);
        timePickerDialog.show();
    }

    public void goToMap(String locationLink) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(locationLink));
        // Kiểm tra xem có ứng dụng nào có thể xử lý intent này không
        if (intent.resolveActivity(requireActivity().getPackageManager()) != null) {
            startActivity(intent);
        } else {
            // Xử lý khi không có ứng dụng nào phù hợp
            Toast.makeText(getActivity(), "Không tìm thấy ứng dụng bản đồ", Toast.LENGTH_SHORT).show();
        }
    }


    public void selectedTable(int tableId){
        selectedTableId = tableId;
    }

    private void updateTableStatus(int tableId, String newStatus) {
        tableInfoRef.child(String.valueOf(tableId)).child("status").setValue(newStatus)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("Reservation", "Trạng thái bàn đã được cập nhật.");
                    } else {
                        Log.d("Reservation", "Cap nhat trạng thái bàn that bai.");
                    }
                });
    }

}