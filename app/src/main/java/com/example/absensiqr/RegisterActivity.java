package com.example.absensiqr;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    private EditText etEmail, etPassword, etNama, etNim;
    private RadioGroup roleGroup;
    private RadioButton rbDosen, rbMahasiswa;
    private Button btnRegister;

    private FirebaseAuth auth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etEmail     = findViewById(R.id.etEmail);
        etPassword  = findViewById(R.id.etPassword);
        etNama      = findViewById(R.id.etNama);
        etNim       = findViewById(R.id.etNim);
        roleGroup   = findViewById(R.id.roleGroup);
        rbDosen     = findViewById(R.id.rbDosen);
        rbMahasiswa = findViewById(R.id.rbMahasiswa);
        btnRegister = findViewById(R.id.btnRegister);

        auth = FirebaseAuth.getInstance();
        db   = FirebaseFirestore.getInstance();

        btnRegister.setOnClickListener(v -> registerUser());
    }

    private void registerUser() {
        String email    = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String nama     = etNama.getText().toString().trim();
        String nim      = etNim.getText().toString().trim();

        int selectedId = roleGroup.getCheckedRadioButtonId();
        if (selectedId == -1) {
            Toast.makeText(this, "Pilih role terlebih dahulu", Toast.LENGTH_SHORT).show();
            return;
        }

        String role = (selectedId == rbDosen.getId()) ? "dosen" : "mahasiswa";

        if (email.isEmpty() || password.isEmpty() || nama.isEmpty() || nim.isEmpty()) {
            Toast.makeText(this, "Isi semua field", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Format email tidak valid", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 6) {
            Toast.makeText(this, "Password minimal 6 karakter", Toast.LENGTH_SHORT).show();
            return;
        }

        auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(result -> {
                    String uid = result.getUser().getUid();

                    HashMap<String, Object> userData = new HashMap<>();
                    userData.put("email", email);
                    userData.put("nama", nama);
                    userData.put("nim", nim);
                    userData.put("role", role);

                    db.collection("users")
                            .document(uid)
                            .set(userData)
                            .addOnSuccessListener(unused -> {
                                Toast.makeText(this, "Registrasi berhasil", Toast.LENGTH_SHORT).show();

                                Intent intent;
                                if (role.equals("dosen")) {
                                    intent = new Intent(this, DosenMainActivity.class);
                                } else {
                                    intent = new Intent(this, MahasiswaMainActivity.class);
                                }

                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(this, "Gagal simpan data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Gagal daftar: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }
}