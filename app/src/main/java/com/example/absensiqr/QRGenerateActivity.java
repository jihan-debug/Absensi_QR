package com.example.absensiqr;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.google.firebase.auth.FirebaseAuth;


public class QRGenerateActivity extends AppCompatActivity {

    private EditText inputKode;
    private Button btnGenerate;
    private ImageView imgQR;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_generator);

        MaterialToolbar toolbar = findViewById(R.id.topAppBar);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());


        // ⬇️ ID lama tetap aman (tanpa perubahan)
        inputKode = findViewById(R.id.inputKodeQR);
        btnGenerate = findViewById(R.id.btnGenerateQR);
        imgQR = findViewById(R.id.imageQR);

        btnGenerate.setOnClickListener(v -> {
            String kodeMatkul = inputKode.getText().toString().trim();

            if (kodeMatkul.isEmpty()) {
                Toast.makeText(this, "Isi kode matkul dulu", Toast.LENGTH_SHORT).show();
                return;
            }

            String uidDosen = FirebaseAuth.getInstance().getCurrentUser().getUid();
            long sesiTimestamp = System.currentTimeMillis();

            String qrContent = "ABSEN|" + uidDosen + "|" + kodeMatkul + "|" + sesiTimestamp;

            generateQR(qrContent);
        });

    }

    private void generateQR(String text) {
        try {
            BarcodeEncoder encoder = new BarcodeEncoder();
            Bitmap bitmap = encoder.encodeBitmap(text, BarcodeFormat.QR_CODE, 600, 600);
            imgQR.setImageBitmap(bitmap);

        } catch (WriterException e) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
