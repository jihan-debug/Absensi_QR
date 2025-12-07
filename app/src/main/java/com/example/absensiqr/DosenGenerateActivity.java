package com.example.absensiqr;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.common.BitMatrix;

import java.util.HashMap;
import java.util.Map;

public class DosenGenerateActivity extends AppCompatActivity {

    private ImageView imageView;
    private TextView textInfo;
    private FirebaseFirestore db;
    private FirebaseAuth auth;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dosen_generate);

        imageView = findViewById(R.id.imageView);
        textInfo = findViewById(R.id.textInfo);
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        String namaDosen = getIntent().getStringExtra("name");     // kirim: "nama_dosen"
        String absensi = getIntent().getStringExtra("absensi");     // kirim: nama absensi/sesi

        buatSession(namaDosen, absensi);
    }

    private void buatSession(String namaDosen, String absensi) {
        String sessionId = "session_" + System.currentTimeMillis();

        Map<String, Object> data = new HashMap<>();
        data.put("session_id", sessionId);
        data.put("absensi", absensi);
        data.put("created_by", namaDosen);
        data.put("timestamp", FieldValue.serverTimestamp());

        db.collection("sessions")
                .document(sessionId)
                .set(data)
                .addOnSuccessListener(unused -> {
                    textInfo.setText("QR untuk: " + absensi + "\nDosen: " + namaDosen);
                    generateQRCode(sessionId);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Gagal buat session: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void generateQRCode(String text) {
        QRCodeWriter writer = new QRCodeWriter();
        try {
            int size = 512;
            BitMatrix bitMatrix = writer.encode(text, BarcodeFormat.QR_CODE, size, size);
            Bitmap bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.RGB_565);

            for (int x = 0; x < size; x++) {
                for (int y = 0; y < size; y++) {
                    bitmap.setPixel(x, y, bitMatrix.get(x, y) ? android.graphics.Color.BLACK : android.graphics.Color.WHITE);
                }
            }

            imageView.setImageBitmap(bitmap);
        } catch (WriterException e) {
            Toast.makeText(this, "Gagal generate QR: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}