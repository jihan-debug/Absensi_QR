package com.example.absensiqr;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class AbsenAdapter extends RecyclerView.Adapter<AbsenAdapter.ViewHolder> {

    ArrayList<AbsenModel> list;

    public AbsenAdapter(ArrayList<AbsenModel> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_absen, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int position) {

        AbsenModel a = list.get(position);

        h.tvNama.setText(a.nama);
        h.tvStatus.setText(a.status);

        // WARNA STATUS
        if (a.status != null) {
            if (a.status.equalsIgnoreCase("hadir")) {
                h.tvStatus.setTextColor(Color.parseColor("#2ECC71")); // hijau
            } else if (a.status.equalsIgnoreCase("alpha")) {
                h.tvStatus.setTextColor(Color.parseColor("#E74C3C")); // merah
            }
        }

        long waktuMillis = 0;
        if (a.waktu != null) {
            waktuMillis = a.waktu.toDate().getTime();
        }

        String t = new SimpleDateFormat("HH:mm", new Locale("id", "ID"))
                .format(new Date(waktuMillis));

        h.tvWaktu.setText(t);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNama, tvStatus, tvWaktu;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNama = itemView.findViewById(R.id.tvNama);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvWaktu = itemView.findViewById(R.id.tvWaktu);
        }
    }
}
