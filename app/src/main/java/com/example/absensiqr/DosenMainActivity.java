package com.example.absensiqr;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class DosenMainActivity extends AppCompatActivity {

    private TextView tvWelcomeDosen;
    private Button btnGenerateQR, btnDaftarAbsen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dosen_main);

        tvWelcomeDosen = findViewById(R.id.tvWelcomeDosen);
        btnGenerateQR  = findViewById(R.id.btnGenerateQR);
        btnDaftarAbsen = findViewById(R.id.btnDaftarAbsen);

        tvWelcomeDosen.setText("Halo Dosen, selamat datang!");

        btnGenerateQR.setOnClickListener(v -> {
            startActivity(new Intent(this, FormMatkulActivity.class));
        });

        btnDaftarAbsen.setOnClickListener(v -> {
            startActivity(new Intent(this, DaftarAbsenActivity.class));
        });
    }
}