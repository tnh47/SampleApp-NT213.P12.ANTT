package com.example.sampleapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.example.sampleapp.model.User;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SignupActivity extends AppCompatActivity {

    private EditText editTextUsername, editTextEmail, editTextPassword, editTextRetypePassword;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        editTextUsername = findViewById(R.id.username);
        editTextEmail = findViewById(R.id.email);
        editTextPassword = findViewById(R.id.password);
        editTextRetypePassword = findViewById(R.id.retype_password);

        // Firebase Realtime Database reference
        mDatabase = FirebaseDatabase.getInstance().getReference("users");

        findViewById(R.id.already_user).setOnClickListener(view -> goToLogin());
    }

    public void registerUser(View view) {
        String username = editTextUsername.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String retypePassword = editTextRetypePassword.getText().toString().trim();

        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || retypePassword.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(retypePassword)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        // Hash password before storing it
        String hashedPassword = hashPassword(password);

        // Create a new user object
        User user = new User();
        user.setName(username);
        user.setEmail(email);
        user.setPassword(hashedPassword);

        // Save the user data into Firebase Realtime Database under the username as the key
        mDatabase.child(username).setValue(user)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show();
                        Intent loginIntent = new Intent(SignupActivity.this, LoginActivity.class);
                        startActivity(loginIntent);
                        finish();
                    } else {
                        Toast.makeText(this, "Registration failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Hash password using SHA-256
    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashInBytes = md.digest(password.getBytes());

            StringBuilder sb = new StringBuilder();
            for (byte b : hashInBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            Log.e("SignupActivity", "Error hashing password", e);
            return null;
        }
    }

    private void goToLogin() {
        Intent loginIntent = new Intent(SignupActivity.this, LoginActivity.class);
        startActivity(loginIntent);
        finish();
    }
}
