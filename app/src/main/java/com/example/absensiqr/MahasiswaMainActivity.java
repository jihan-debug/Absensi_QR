package com.example.absensiqr;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MahasiswaMainActivity extends AppCompatActivity {

    private TextView tvWelcomeMahasiswa;
    private Button btnScanQR, btnRiwayatAbsen; // ⬅️ tombol baru

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mahasiswa_main);

        tvWelcomeMahasiswa = findViewById(R.id.tvWelcomeMahasiswa);
        btnScanQR          = findViewById(R.id.btnScanQR);
        btnRiwayatAbsen    = findViewById(R.id.btnRiwayatAbsen); // ⬅️ sambungkan ke XML

        tvWelcomeMahasiswa.setText("Halo Mahasiswa, selamat datang!");

        btnScanQR.setOnClickListener(v -> {
            startActivity(new Intent(this, QRScannerActivity.class));
        });

        btnRiwayatAbsen.setOnClickListener(v -> { // ⬅️ fungsi tombol baru
            startActivity(new Intent(this, DaftarAbsenActivity.class));
        });
    }
}
