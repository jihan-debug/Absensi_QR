package com.example.absensiqr;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class HomeActivity extends AppCompatActivity {

    private TextView welcomeText;
    private Button btnScanQR, btnGenerateQR, btnLihatAbsen, btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        welcomeText   = findViewById(R.id.textWelcome);
        btnScanQR     = findViewById(R.id.btnScanQR);
        btnGenerateQR = findViewById(R.id.btnGenerateQR);
        btnLihatAbsen = findViewById(R.id.btnLihatAbsen);
        btnLogout     = findViewById(R.id.btnLogout);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            welcomeText.setText("Selamat datang, " + user.getEmail());
        } else {
            welcomeText.setText("Selamat datang di aplikasi AbsensiQR");
        }

        String role = getIntent().getStringExtra("role");
        if ("mahasiswa".equals(role)) {
            btnScanQR.setVisibility(View.VISIBLE);
            btnGenerateQR.setVisibility(View.GONE);
            btnLihatAbsen.setVisibility(View.GONE);
        } else if ("dosen".equals(role)) {
            btnScanQR.setVisibility(View.GONE);
            btnGenerateQR.setVisibility(View.VISIBLE);
            btnLihatAbsen.setVisibility(View.VISIBLE);
        }

        // Tombol Lihat Absen (khusus dosen)
        btnLihatAbsen.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, DaftarAbsenActivity.class);
            startActivity(intent);
        });

        // Tombol Logout
        btnLogout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }
}