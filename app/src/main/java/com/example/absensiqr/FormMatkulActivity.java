package com.example.absensiqr;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class FormMatkulActivity extends AppCompatActivity {

    EditText inputNamaMatkul, inputHariTanggal, inputPertemuan;
    Button btnSimpanDanGenerate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_matkul);

        inputNamaMatkul = findViewById(R.id.etNamaMatkul);
        inputHariTanggal = findViewById(R.id.etTanggal);
        inputPertemuan = findViewById(R.id.etPertemuan);
        btnSimpanDanGenerate = findViewById(R.id.btnGenerate);

        btnSimpanDanGenerate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String nama = inputNamaMatkul.getText().toString().trim();
                String hari = inputHariTanggal.getText().toString().trim();
                String pertemuan = inputPertemuan.getText().toString().trim();

                if (nama.isEmpty() || hari.isEmpty() || pertemuan.isEmpty()) {
                    Toast.makeText(FormMatkulActivity.this, "Semua data wajib diisi!", Toast.LENGTH_SHORT).show();
                    return;
                }

                // 🔗 Gabung semua data menjadi 1 string untuk QR
                String dataQR = "Matkul: " + nama +
                        "\nHari/Tanggal: " + hari +
                        "\nPertemuan Ke: " + pertemuan;

                // ⏩ Pindah ke halaman QR Generator + kirim data
                Intent intent = new Intent(FormMatkulActivity.this, QRGenerateActivity.class);
                intent.putExtra("dataQR", dataQR);
                startActivity(intent);

                Toast.makeText(FormMatkulActivity.this, "Data Disimpan!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
