package com.example.pawmate;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class PhoneLogin extends AppCompatActivity {

    private EditText etPhone;
    private MaterialButton btnSendOTP;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_login);

        mAuth = FirebaseAuth.getInstance();

        etPhone = findViewById(R.id.etPhone);
        btnSendOTP = findViewById(R.id.btnSendOTP);

        btnSendOTP.setOnClickListener(v -> sendOTP());
    }

    private void sendOTP() {
        String phoneNumber = etPhone.getText().toString().trim();

        if (phoneNumber.isEmpty()) {
            etPhone.setError("Enter phone number");
            etPhone.requestFocus();
            return;
        }

        if (!phoneNumber.startsWith("+")) {
            etPhone.setError("Use country code, example +9779812345678");
            etPhone.requestFocus();
            return;
        }

        btnSendOTP.setEnabled(false);
        btnSendOTP.setText("Sending...");

        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(mAuth)
                .setPhoneNumber(phoneNumber)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                    @Override
                    public void onVerificationCompleted(PhoneAuthCredential credential) {
                        signInWithCredential(credential);
                    }

                    @Override
                    public void onVerificationFailed(FirebaseException e) {
                        btnSendOTP.setEnabled(true);
                        btnSendOTP.setText("Send OTP");

                        String message = e.getMessage();

                        if (message == null || message.isEmpty()) {
                            message = "Failed to send OTP";
                        }

                        Toast.makeText(
                                PhoneLogin.this,
                                message,
                                Toast.LENGTH_LONG
                        ).show();
                    }

                    @Override
                    public void onCodeSent(
                            String verificationId,
                            PhoneAuthProvider.ForceResendingToken token
                    ) {
                        btnSendOTP.setEnabled(true);
                        btnSendOTP.setText("Send OTP");

                        Intent intent = new Intent(
                                PhoneLogin.this,
                                OtpVerification.class
                        );

                        intent.putExtra("verificationId", verificationId);
                        intent.putExtra("phoneNumber", phoneNumber);

                        /*
                         * Do not pass ForceResendingToken through Intent.
                         * OtpVerification will handle OTP resend itself.
                         */

                        startActivity(intent);
                        finish();
                    }
                })
                .build();

        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private void signInWithCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(
                                PhoneLogin.this,
                                "Phone verified successfully!",
                                Toast.LENGTH_SHORT
                        ).show();

                        Intent intent = new Intent(
                                PhoneLogin.this,
                                Dashboard.class
                        );

                        startActivity(intent);
                        finish();

                    } else {
                        String message = "Phone verification failed";

                        if (task.getException() != null &&
                                task.getException().getMessage() != null) {
                            message = task.getException().getMessage();
                        }

                        Toast.makeText(
                                PhoneLogin.this,
                                message,
                                Toast.LENGTH_LONG
                        ).show();
                    }
                });
    }
}