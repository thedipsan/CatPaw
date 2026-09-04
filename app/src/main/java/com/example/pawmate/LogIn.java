package com.example.pawmate;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LogIn extends AppCompatActivity {

    EditText etEmail, etPassword;
    MaterialButton btnLogin;
    TextView tvSignUp;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_log_in);

       // Initialize Firebase Auth
        mAuth= FirebaseAuth.getInstance();

        // Connect with XML component
        etEmail = findViewById(R.id.etEmail);
        etPassword= findViewById(R.id.etPassword);

        btnLogin= findViewById(R.id.btnLogin);
        tvSignUp= findViewById(R.id.tvSignUp);



        // Login Button
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });


        // Go to signup
        tvSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LogIn.this, SignUp.class);
                startActivity(intent);
            }
        });




    }

    private void loginUser() {

        String email= etEmail.getText().toString().trim();
        String password= etPassword.getText().toString().trim();

         // Validation
        if(email.isEmpty()){
            etEmail.setError("Enter your email");
            etEmail.requestFocus();
            return;
        }
        // Check Password
        if(password.isEmpty()){
            etPassword.setError("Enter your password");
            etPassword.requestFocus();
            return;
        }

        // Disable button while loggin in
        btnLogin.setEnabled(false);
        btnLogin.setText("Logging in...");

        // Firebase Login
        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()){
                    Toast.makeText(LogIn.this, "Login Successful", Toast.LENGTH_SHORT).show();

                    //Open Dashboard
                    Intent intent = new Intent(LogIn.this, Dashboard.class);
                    startActivity(intent);
                    finish();
                }else{
                    btnLogin.setEnabled(true);
                    btnLogin.setText("Login");
                    String errorMessage= "Login failed";

                    if(task.getException()!=null){
                        errorMessage= task.getException().getMessage();
                    }

                    Toast.makeText(LogIn.this,errorMessage, Toast.LENGTH_SHORT).show();
                }


            }
        });
    }
}



