package com.duytai.cse441_project.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.duytai.cse441_project.R;
import com.duytai.cse441_project.model.Reservation;

import java.util.ArrayList;

public class ReservationAdapter extends RecyclerView.Adapter<ReservationAdapter.ReservationViewHolder> {

    private ArrayList<Reservation> reservationList;
    private OnCancelButtonClickListener onCancelButtonClickListener;

    public ReservationAdapter(ArrayList<Reservation> reservationList, OnCancelButtonClickListener listener) {
        this.reservationList = reservationList;
        this.onCancelButtonClickListener = listener;
    }

    @NonNull
    @Override
    public ReservationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_reservation, parent, false);
        return new ReservationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReservationAdapter.ReservationViewHolder holder, int position) {
        Reservation reservation = reservationList.get(position);

        holder.tvStore.setText(reservation.getStoreName());
        holder.tvStorePhone.setText(reservation.getStorePhone());
        holder.tvDate.setText(reservation.getReservationDate());
        holder.tvTime.setText(reservation.getReservationTime());
        holder.tvTable.setText(reservation.getTableSeats() + " chỗ");
        holder.tvNote.setText(reservation.getNote());
        // Thiết lập sự kiện cho nút đặt bàn
        holder.btnCancel.setOnClickListener(v -> {
            if (onCancelButtonClickListener != null) {
                onCancelButtonClickListener.onCancelButtonClick(reservation);
            }
        });
    }

    @Override
    public int getItemCount() {
        return reservationList.size();
    }

    public static class ReservationViewHolder extends RecyclerView.ViewHolder {
        TextView tvStore, tvDirection, tvStorePhone, tvDate, tvTime, tvTable, tvNote;
        Button btnCancel;

        public ReservationViewHolder(@NonNull View itemView) {
            super(itemView);
            tvStore = itemView.findViewById(R.id.tv_store);
            tvDirection = itemView.findViewById(R.id.tv_direction);
            tvStorePhone = itemView.findViewById(R.id.tv_phone);
            tvDate = itemView.findViewById(R.id.tv_date);
            tvTime = itemView.findViewById(R.id.tv_time);
            tvTable = itemView.findViewById(R.id.tv_table);
            tvNote = itemView.findViewById(R.id.tv_note);
            btnCancel = itemView.findViewById(R.id.btn_cancel);
        }
    }

    // Interface để xử lý sự kiện nhấn nút
    public interface OnCancelButtonClickListener {
        void onCancelButtonClick(Reservation reservation);
    }
}