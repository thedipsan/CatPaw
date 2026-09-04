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

        String phone =
                etPhone.getText().toString().trim();

        if (phone.isEmpty()) {

            etPhone.setError("Enter phone number");
            etPhone.requestFocus();

            return;
        }

        if (!phone.startsWith("+")) {

            etPhone.setError(
                    "Use country code, example +9779812345678"
            );

            etPhone.requestFocus();

            return;
        }

        btnSendOTP.setEnabled(false);
        btnSendOTP.setText("Sending...");

        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(phone)
                        .setTimeout(
                                60L,
                                TimeUnit.SECONDS
                        )
                        .setActivity(this)
                        .setCallbacks(
                                new PhoneAuthProvider
                                        .OnVerificationStateChangedCallbacks() {

                                    @Override
                                    public void onVerificationCompleted(
                                            PhoneAuthCredential credential) {

                                        signInWithCredential(
                                                credential
                                        );
                                    }

                                    @Override
                                    public void onVerificationFailed(
                                            FirebaseException e) {

                                        btnSendOTP.setEnabled(true);
                                        btnSendOTP.setText(
                                                "Send OTP"
                                        );

                                        Toast.makeText(
                                                PhoneLogin.this,
                                                e.getMessage(),
                                                Toast.LENGTH_LONG
                                        ).show();
                                    }

                                    @Override
                                    public void onCodeSent(
                                            String verificationId,
                                            PhoneAuthProvider
                                                    .ForceResendingToken
                                                    token) {

                                        btnSendOTP.setEnabled(true);
                                        btnSendOTP.setText(
                                                "Send OTP"
                                        );

                                        Intent intent =
                                                new Intent(
                                                        PhoneLogin.this,
                                                        OtpVerification.class
                                                );

                                        intent.putExtra(
                                                "verificationId",
                                                verificationId
                                        );

                                        intent.putExtra(
                                                "phoneNumber",
                                                phone
                                        );

                                        /*
                                         * Save the resend token.
                                         *
                                         * Firebase needs this token
                                         * when we request another OTP.
                                         */
                                        intent.putExtra(
                                                "resendToken",
                                                token
                                        );

                                        startActivity(intent);

                                        finish();
                                    }
                                }
                        )
                        .build();

        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private void signInWithCredential(
            PhoneAuthCredential credential) {

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {

                        Toast.makeText(
                                PhoneLogin.this,
                                "Phone verified successfully!",
                                Toast.LENGTH_SHORT
                        ).show();

                        startActivity(
                                new Intent(
                                        PhoneLogin.this,
                                        Dashboard.class
                                )
                        );

                        finish();

                    } else {

                        Toast.makeText(
                                PhoneLogin.this,
                                "Phone verification failed",
                                Toast.LENGTH_LONG
                        ).show();
                    }
                });
    }
}