package com.example.pawmate;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
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
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPassword extends AppCompatActivity {

    EditText etEmail;
    MaterialButton btnSendReset;

    FirebaseAuth mAuth;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forgot_password);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();

        //Connect with XML
        etEmail= findViewById(R.id.etEmail);
        btnSendReset = findViewById(R.id.btnSendReset);


        // Send reset email
        btnSendReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = etEmail.getText().toString().trim();

                //Validation

                // Check empty
                if(email.isEmpty()){
                    etEmail.setError("Enter your email");
                    etEmail.requestFocus();
                    return;
                }


                // Check email format
                if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    etEmail.setError("Enter a valid email");
                    etEmail.requestFocus();
                    return;
                }

                // Disable button
                btnSendReset.setEnabled(false);
                btnSendReset.setText("Sending...");

                //Firebase password reset
                mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if(task.isSuccessful()){
                            Toast.makeText(ForgotPassword.this, "Password reset email sent", Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(ForgotPassword.this, LogIn.class);
                            startActivity(intent);
                            finish();
                        }else{
                            btnSendReset.setEnabled(true);
                            btnSendReset.setText("Send Reset Link");

                            String message= task.getException()!=null
                                    ? task.getException().getMessage()
                                    :"Unable to send reset email";

                            Toast.makeText(ForgotPassword.this,message, Toast.LENGTH_SHORT).show();
                        }

                    }
                });
            }
        });

    }
}