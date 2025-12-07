package com.example.absensiqr;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class DaftarAbsenActivity extends AppCompatActivity {

    private RecyclerView recyclerAbsen;
    private AbsenAdapter adapter;
    private ArrayList<AbsenModel> listAbsen;
    private TextView tvNamaMatkul, tvTanggal;

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daftar_absen);

        db = FirebaseFirestore.getInstance();

        String namaMatkul = getIntent().getStringExtra("namaMatkul");

        MaterialToolbar toolbar = findViewById(R.id.topAppBar);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        tvNamaMatkul = findViewById(R.id.tvNamaMatkul);
        tvTanggal = findViewById(R.id.tvTanggal);
        recyclerAbsen = findViewById(R.id.recyclerAbsen);

        if (namaMatkul != null) tvNamaMatkul.setText(namaMatkul);

        recyclerAbsen.setLayoutManager(new LinearLayoutManager(this));
        listAbsen = new ArrayList<>();
        adapter = new AbsenAdapter(listAbsen);
        recyclerAbsen.setAdapter(adapter);

        loadAbsen();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void loadAbsen() {
        db.collection("absen_masuk")
                .orderBy("waktu", Query.Direction.ASCENDING)
                .addSnapshotListener((value, error) -> {

                    if (error != null) {
                        Toast.makeText(this, "Gagal mengambil data", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    listAbsen.clear();

                    if (value == null) return;

                    // SET TANGGAL DARI DATA PERTAMA
                    if (!value.isEmpty()) {
                        Timestamp ts = value.getDocuments().get(0).getTimestamp("waktu");
                        if (ts != null) {
                            Date date = ts.toDate();
                            String tgl = new SimpleDateFormat("dd MMMM yyyy", new Locale("id", "ID"))
                                    .format(date);
                            tvTanggal.setText("Tanggal: " + tgl);
                        }
                    }

                    // MASUKKAN DATA KE LIST
                    for (var doc : value.getDocuments()) {
                        AbsenModel absen = doc.toObject(AbsenModel.class);
                        listAbsen.add(absen);
                    }

                    adapter.notifyDataSetChanged();
                });
    }
}
