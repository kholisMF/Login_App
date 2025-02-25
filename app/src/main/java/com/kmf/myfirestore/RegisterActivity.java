package com.kmf.myfirestore;

import android.content.Intent;
import android.graphics.Insets;
import android.os.Bundle;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.Toast;

import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.firebase.Firebase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kmf.myfirestore.databinding.ActivityRegisterBinding;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
    private ActivityRegisterBinding binding;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            androidx.core.graphics.Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        db = FirebaseFirestore.getInstance();

        setupAction();
    }

    private void setupAction() {
        binding.tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        binding.btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = binding.etEmail.getText().toString();
                String nama = binding.etNama.getText().toString();
                String password = binding.etPassword.getText().toString();

                if (email.isEmpty() || nama.isEmpty() || password.isEmpty()){
                    Toast.makeText(RegisterActivity.this, "Lengkapi datanya!", Toast.LENGTH_SHORT).show();
                }

                else {
                    cekEmailTerdaftar(email, nama, password);
                }
            }
        });
    }

    private void cekEmailTerdaftar(String email, String nama, String password) {
        db.collection("users")
                .whereEqualTo("email", email)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        // Email sudah terdaftar
                        Toast.makeText(RegisterActivity.this, "Email sudah digunakan!", Toast.LENGTH_SHORT).show();
                    } else {
                        // Email belum terdaftar, buat akun baru
                        buatAkun(email, nama, password);
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(RegisterActivity.this, "Terjadi kesalahan, coba lagi!", Toast.LENGTH_SHORT).show()
                );
    }
    private void buatAkun(String email, String nama, String password) {
        Map<String, Object> user = new HashMap<>();
        user.put("email", email);
        user.put("nama", nama);
        user.put("password", password);

        db.collection("users")
                .add(user)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(RegisterActivity.this, "Berhasil membuat akun", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(RegisterActivity.this, "Gagal membuat akun", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}