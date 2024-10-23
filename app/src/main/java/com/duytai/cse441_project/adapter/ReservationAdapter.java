package com.duytai.cse441_project.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.duytai.cse441_project.R;
import com.duytai.cse441_project.model.Reservation;

import java.util.ArrayList;

public class ReservationAdapter extends RecyclerView.Adapter<ReservationAdapter.ReservationViewHolder> {

    private ArrayList<Reservation> reservationList;
    private OnItemClickListener onItemClickListener;

    public ReservationAdapter(ArrayList<Reservation> reservationList, OnItemClickListener onItemClickListener) {
        this.reservationList = reservationList;
        this.onItemClickListener = onItemClickListener;
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
        holder.tvDate.setText(reservation.getReservationDate());
        holder.tvTime.setText(reservation.getReservationTime());
        holder.tvTable.setText(reservation.getTableSeats() + " chỗ");
        Glide.with(holder.itemView.getContext())
                .load(reservation.getStoreImageUrl())
                .error(R.drawable.logo)
                .into(holder.imgStore);

        // Thiết lập sự kiện
        holder.itemView.setOnClickListener(v -> onItemClickListener.onItemClick(reservation));
    }

    @Override
    public int getItemCount() {
        return reservationList.size();
    }

    public static class ReservationViewHolder extends RecyclerView.ViewHolder {
        TextView tvStore, tvDate, tvTime, tvTable;
        ImageView imgStore;

        public ReservationViewHolder(@NonNull View itemView) {
            super(itemView);
            tvStore = itemView.findViewById(R.id.tv_store);
            imgStore = itemView.findViewById(R.id.imgStore);
            tvDate = itemView.findViewById(R.id.tv_date);
            tvTime = itemView.findViewById(R.id.tv_time);
            tvTable = itemView.findViewById(R.id.tv_table);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(Reservation reservation);
    }
}