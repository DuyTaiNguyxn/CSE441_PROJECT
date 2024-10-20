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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.duytai.cse441_project.R;
import com.duytai.cse441_project.adapter.CategoryAdapter;
import com.duytai.cse441_project.adapter.ReservationAdapter;
import com.duytai.cse441_project.model.Store;
import com.duytai.cse441_project.model.TableInfo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ReservationFragment extends Fragment {

    private TextView tvStoreName, tvStoreLocation, tvSelectedDate, tvSelectedTime, tvDatePicker, tvTimePicker;
    private RecyclerView rcvTable;
    private EditText edtNote;
    private Button btnSubmit;

    private ReservationAdapter reservationAdapter;
    private int selectedTableId;

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
        tvDatePicker = view.findViewById(R.id.btn_date_picker);
        tvTimePicker = view.findViewById(R.id.btn_time_picker);
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
        reservationAdapter = new ReservationAdapter(getContext(), availableTableList, this);
        rcvTable.setAdapter(reservationAdapter);

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

    public void selectedTable(int tableId){
        selectedTableId = tableId;
    }
}