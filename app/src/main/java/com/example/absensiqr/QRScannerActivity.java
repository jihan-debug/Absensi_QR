package com.example.absensiqr;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.common.InputImage;

import java.util.concurrent.ExecutionException;

public class QRScannerActivity extends AppCompatActivity {

    private PreviewView previewView;
    private TextView tvHasilScan;
    private boolean sudahScan = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrscanner);

        previewView = findViewById(R.id.cameraPreview);
        tvHasilScan = findViewById(R.id.tvHasilScan);

        // 🔹 tombol kembali dari toolbar
        MaterialToolbar topAppBar = findViewById(R.id.topAppBar);
        topAppBar.setNavigationOnClickListener(v -> finish());

        // 🔹 izin kamera
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA}, 100);
        } else {
            startCamera();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 100 &&
                grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startCamera();
        } else {
            Toast.makeText(this, "Izin kamera diperlukan", Toast.LENGTH_SHORT).show();
        }
    }

    private void startCamera() {

        ListenableFuture<ProcessCameraProvider> cameraProviderFuture =
                ProcessCameraProvider.getInstance(this);

        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider provider = cameraProviderFuture.get();

                Preview preview = new Preview.Builder().build();
                preview.setSurfaceProvider(previewView.getSurfaceProvider());

                CameraSelector selector = CameraSelector.DEFAULT_BACK_CAMERA;

                ImageAnalysis analysis = new ImageAnalysis.Builder()
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build();

                BarcodeScanner scanner = BarcodeScanning.getClient();

                analysis.setAnalyzer(ContextCompat.getMainExecutor(this),
                        imageProxy -> scanBarcode(scanner, imageProxy));

                provider.bindToLifecycle(this, selector, preview, analysis);

            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }, ContextCompat.getMainExecutor(this));
    }

    private void scanBarcode(BarcodeScanner scanner, ImageProxy imageProxy) {

        if (sudahScan) {
            imageProxy.close();
            return;
        }
        if (imageProxy.getImage() == null) {
            imageProxy.close();
            return;
        }

        InputImage image = InputImage.fromMediaImage(
                imageProxy.getImage(),
                imageProxy.getImageInfo().getRotationDegrees());

        scanner.process(image)
                .addOnSuccessListener(barcodes -> {

                    if (!barcodes.isEmpty()) {

                        Barcode kode = barcodes.get(0);
                        String hasil = kode.getRawValue();

                        sudahScan = true;
                        tvHasilScan.setText("QR: " + hasil);

                        ambilNamaDanSimpan(hasil);
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Gagal scan: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                })
                .addOnCompleteListener(task -> imageProxy.close());
    }

    private void ambilNamaDanSimpan(final String absensiId) {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user == null) {
            tvHasilScan.setText("User belum login");
            return;
        }

        final String uid = user.getUid();

        FirebaseFirestore.getInstance()
                .collection("users")
                .document(uid)
                .get()
                .addOnSuccessListener(doc -> {

                    String nama = doc.getString("nama");
                    if (nama == null || nama.isEmpty()) nama = "Tanpa Nama";

                    simpanAbsen(absensiId, uid, nama);

                })
                .addOnFailureListener(e -> {
                    simpanAbsen(absensiId, uid, "Tanpa Nama");
                });
    }

    private void simpanAbsen(String absensiId, String uid, String nama) {

        Absen data = new Absen(uid, nama);

        FirebaseFirestore.getInstance()
                .collection("absen_masuk")
                .document(absensiId + "_" + uid)
                .set(data)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Absensi tersimpan untuk " + nama, Toast.LENGTH_SHORT).show();
                    finish(); // auto Exit ✔
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Gagal simpan: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    public static class Absen {
        public String uid, nama, status;
        public Timestamp waktu;

        public Absen() {}

        public Absen(String uid, String nama) {
            this.uid = uid;
            this.nama = nama;
            this.status = "Hadir";
            this.waktu = Timestamp.now();
        }
    }
}
