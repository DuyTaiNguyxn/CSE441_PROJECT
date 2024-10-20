package com.duytai.cse441_project.fragment;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.duytai.cse441_project.R;
import com.duytai.cse441_project.model.Store;
import com.duytai.cse441_project.model.TableInfo;

import java.io.Serializable;
import java.util.ArrayList;

public class ReservationFragment extends Fragment {

    TextView tvStoreName, tvStoreLocation, tvSelectedDate, tvSelectedTime, tvDatePicker, tvTimePicker;
    EditText edtUserName, edtUserPhone;
    Button btnSubmit;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reservation, container, false);

        tvStoreName = view.findViewById(R.id.tv_store_name);
        tvStoreLocation = view.findViewById(R.id.tv_store_location);
        tvSelectedDate = view.findViewById(R.id.tv_selected_date);
        tvSelectedTime = view.findViewById(R.id.tv_selected_time);
        edtUserName = view.findViewById(R.id.edt_user_name);
        edtUserPhone = view.findViewById(R.id.edt_user_phone);
        tvDatePicker = view.findViewById(R.id.btn_date_picker);
        tvTimePicker = view.findViewById(R.id.btn_time_picker);
        btnSubmit = view.findViewById(R.id.btn_submit);

        Store storeData = null;
        ArrayList<TableInfo> availableTabelList;

        // Nhận dữ liệu từ bundle
        if (getArguments() != null) {
            storeData = (Store) getArguments().getSerializable("storeData");
            availableTabelList = (ArrayList<TableInfo>) getArguments().getSerializable("availableTableData");
        }

        tvStoreName.setText(storeData.getStoreName());
        tvDatePicker.setOnClickListener(v -> showDatePickerDialog());
        tvTimePicker.setOnClickListener(v -> showTimePickerDialog());

        // Thực hiện các thao tác khác trong fragment
        return view;
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
}