package com.example.absensiqr;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private Button btnLogin;
    private FirebaseAuth auth;
    private FirebaseFirestore db;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etEmail     = findViewById(R.id.etEmail);
        etPassword  = findViewById(R.id.etPassword);
        btnLogin    = findViewById(R.id.btnLogin);

        auth = FirebaseAuth.getInstance();
        db   = FirebaseFirestore.getInstance();

        // Jika user sudah login sebelumnya
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            cekRoleDanMasuk(currentUser.getUid());
        }

        btnLogin.setOnClickListener(v -> loginUser());
    }

    private void loginUser() {
        String email    = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Isi email dan password", Toast.LENGTH_SHORT).show();
            return;
        }

        auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(result -> {
                    FirebaseUser user = auth.getCurrentUser();
                    if (user != null) {
                        cekRoleDanMasuk(user.getUid());
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Login gagal: " + e.getMessage(), Toast.LENGTH_LONG).show()
                );
    }

    private void cekRoleDanMasuk(String uid) {
        db.collection("users")
                .document(uid)
                .get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        String role = doc.getString("role");

                        if ("Dosen".equalsIgnoreCase(role)) {
                            startActivity(new Intent(this, DosenMainActivity.class));
                        } else {
                            startActivity(new Intent(this, MahasiswaMainActivity.class));
                        }

                        finish();
                    } else {
                        Toast.makeText(this, "Akun tidak ditemukan di database", Toast.LENGTH_LONG).show();
                        auth.signOut();
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Gagal cek role: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }
}
