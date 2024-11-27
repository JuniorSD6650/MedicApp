package com.example.medicapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    Button btn_register;
    EditText usuario, email, password;

    FirebaseFirestore mFirestore;
    FirebaseAuth mAuth;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mFirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        usuario = findViewById(R.id.et_usuario);
        email = findViewById(R.id.et_correo);
        password = findViewById(R.id.et_contrasena);
        btn_register = findViewById(R.id.button);

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nameUser = usuario.getText().toString();
                String emailUser = email.getText().toString();
                String passwordUser = password.getText().toString();

                if (nameUser.isEmpty() || emailUser.isEmpty() || passwordUser.isEmpty()){
                    Toast.makeText(RegisterActivity.this, "Llene todos los campos", Toast.LENGTH_SHORT).show();

                } else {
                    registerUser(nameUser, emailUser, passwordUser);
                }
            }
        });
    }

    private void registerUser(String nameUser, String emailUser, String passwordUser) {
        // Validaciones previas al registro
        if (!isValidEmail(emailUser)) {
            Toast.makeText(this, "Por favor, ingresa un correo válido.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!isValidPassword(passwordUser)) {
            Toast.makeText(this, "La contraseña debe tener al menos 6 caracteres, incluyendo un número y una letra.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (nameUser.isEmpty() || emailUser.isEmpty() || passwordUser.isEmpty()) {
            Toast.makeText(this, "Por favor, completa todos los campos.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Proceder con el registro en Firebase
        mAuth.createUserWithEmailAndPassword(emailUser, passwordUser).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String id = mAuth.getCurrentUser().getUid();
                Map<String, Object> map = new HashMap<>();
                map.put("id", id);
                map.put("name", nameUser);
                map.put("email", emailUser);
                map.put("password", passwordUser);

                mFirestore.collection("user").document(id).set(map).addOnSuccessListener(unused -> {
                    finish();
                    startActivity(new Intent(RegisterActivity.this, Principal.class));
                    Toast.makeText(RegisterActivity.this, "Usuario registrado correctamente", Toast.LENGTH_SHORT).show();
                }).addOnFailureListener(e -> {
                    Toast.makeText(RegisterActivity.this, "Error al registrar el usuario", Toast.LENGTH_SHORT).show();
                });
            } else {
                Toast.makeText(RegisterActivity.this, "Error al registrar el usuario: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(RegisterActivity.this, "Error al registrar el usuario: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    public void Volver(View view) {
        finish();
    }

    private boolean isValidEmail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean isValidPassword(String password) {
        if (password.length() < 6) {
            return false; // Longitud mínima
        }

        // Validar que contenga al menos un número y una letra
        String passwordPattern = "^(?=.*[A-Za-z])(?=.*\\d).+$";
        return password.matches(passwordPattern);
    }

}