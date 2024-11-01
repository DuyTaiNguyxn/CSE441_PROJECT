package com.duytai.cse441_project.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.net.ParseException;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.duytai.cse441_project.R;
import com.duytai.cse441_project.model.Reservation;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

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

        // Thiết lập sự kiện nhấp vào item
        holder.itemView.setOnClickListener(v -> {
            String reservationDateTime = reservation.getReservationDate() + " " + reservation.getReservationTime();
            if (isReservationExpired(reservationDateTime)) {
                // Nếu đặt chỗ đã hết hạn, gọi sự kiện hủy đặt chỗ
                onItemClickListener.onCancelReservation(reservation);
            } else {
                // Nếu chưa hết hạn, gọi sự kiện xem chi tiết
                onItemClickListener.onItemClick(reservation);
            }
        });
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
        void onCancelReservation(Reservation reservation); // Thêm phương thức cho hủy đặt chỗ
    }

    public boolean isReservationExpired(String reservationDateTime) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        try {
            Date reservationDate = sdf.parse(reservationDateTime);
            Date currentDate = new Date();
            return reservationDate.before(currentDate); // Kiểm tra xem đặt chỗ đã hết hạn chưa
        } catch (ParseException e) {
            e.printStackTrace();
            return false; // Hoặc xử lý theo cách khác nếu có lỗi
        } catch (java.text.ParseException e) {
            throw new RuntimeException(e);
        }
    }

}
