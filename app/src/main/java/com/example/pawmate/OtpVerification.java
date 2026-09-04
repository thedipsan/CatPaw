package com.example.pawmate;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;

public class OtpVerification extends AppCompatActivity {

    // Firebase Authentication
    FirebaseAuth mAuth;

    // XML Component
    EditText etOtp;
    MaterialButton btnVerifyOtp;
    TextView tvResendOtp;


    // Firebase gives us this ID when OTP is sent
    String verificationId;
    // Phonenumber Recieve from forget password
    String phoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_otp_verification);

        // Initialize Firebase
        mAuth= FirebaseAuth.getInstance();

        // Connect to XML Component

    }
}