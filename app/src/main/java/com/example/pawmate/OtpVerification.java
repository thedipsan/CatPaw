package com.example.pawmate;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

public class OtpVerification extends AppCompatActivity {

    private EditText otp1;
    private EditText otp2;
    private EditText otp3;
    private EditText otp4;
    private EditText otp5;
    private EditText otp6;

    private TextView tvPhone;
    private TextView tvTimer;
    private TextView tvResendOTP;

    private MaterialButton btnVerifyOTP;

    private FirebaseAuth mAuth;

    private String verificationId;
    private String phoneNumber;

    private CountDownTimer countDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_otp_verification);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();

        // Connect XML views
        otp1 = findViewById(R.id.otp1);
        otp2 = findViewById(R.id.otp2);
        otp3 = findViewById(R.id.otp3);
        otp4 = findViewById(R.id.otp4);
        otp5 = findViewById(R.id.otp5);
        otp6 = findViewById(R.id.otp6);

        tvPhone = findViewById(R.id.tvPhone);
        tvTimer = findViewById(R.id.tvTimer);
        tvResendOTP = findViewById(R.id.tvResendOTP);

        btnVerifyOTP = findViewById(R.id.btnVerifyOTP);

        // Get data from PhoneLogin
        verificationId =
                getIntent().getStringExtra("verificationId");

        phoneNumber =
                getIntent().getStringExtra("phoneNumber");

        // Display phone number
        if (phoneNumber != null) {
            tvPhone.setText(phoneNumber);
        }

        // Start timer
        startTimer();

        // Setup OTP movement
        setupOtpInputs();

        // Verify button
        btnVerifyOTP.setOnClickListener(v -> verifyOTP());

        // Resend OTP
        tvResendOTP.setOnClickListener(v -> {

            Toast.makeText(
                    OtpVerification.this,
                    "Resend OTP will be connected next",
                    Toast.LENGTH_SHORT
            ).show();

        });
    }


    // OTP INPUT MOVEMENT


    private void setupOtpInputs() {

        otp1.addTextChangedListener(
                createTextWatcher(otp1, otp2)
        );

        otp2.addTextChangedListener(
                createTextWatcher(otp2, otp3)
        );

        otp3.addTextChangedListener(
                createTextWatcher(otp3, otp4)
        );

        otp4.addTextChangedListener(
                createTextWatcher(otp4, otp5)
        );

        otp5.addTextChangedListener(
                createTextWatcher(otp5, otp6)
        );
    }

    private TextWatcher createTextWatcher(
            EditText current,
            EditText next) {

        return new TextWatcher() {

            @Override
            public void beforeTextChanged(
                    CharSequence s,
                    int start,
                    int count,
                    int after) {
            }

            @Override
            public void onTextChanged(
                    CharSequence s,
                    int start,
                    int before,
                    int count) {

                if (s.length() == 1) {
                    next.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(
                    Editable s) {
            }
        };
    }


    // VERIFY OTP


    private void verifyOTP() {

        String code =
                otp1.getText().toString()
                        + otp2.getText().toString()
                        + otp3.getText().toString()
                        + otp4.getText().toString()
                        + otp5.getText().toString()
                        + otp6.getText().toString();

        // Check OTP
        if (code.length() != 6) {

            Toast.makeText(
                    this,
                    "Please enter the complete 6-digit OTP",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        // Check verification ID
        if (verificationId == null ||
                verificationId.isEmpty()) {

            Toast.makeText(
                    this,
                    "Verification session expired. Please request a new OTP.",
                    Toast.LENGTH_LONG
            ).show();

            return;
        }

        btnVerifyOTP.setEnabled(false);
        btnVerifyOTP.setText("Verifying...");

        // Create Firebase credential
        PhoneAuthCredential credential =
                PhoneAuthProvider.getCredential(
                        verificationId,
                        code
                );

        // Sign in with phone credential
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {

                        Toast.makeText(
                                OtpVerification.this,
                                "Phone verified successfully!",
                                Toast.LENGTH_SHORT
                        ).show();

                        // Stop timer
                        if (countDownTimer != null) {
                            countDownTimer.cancel();
                        }

                        // Go Dashboard
                        Intent intent =
                                new Intent(
                                        OtpVerification.this,
                                        Dashboard.class
                                );

                        startActivity(intent);

                        finish();

                    } else {

                        btnVerifyOTP.setEnabled(true);
                        btnVerifyOTP.setText("Verify OTP");

                        Toast.makeText(
                                OtpVerification.this,
                                "Invalid OTP. Please try again.",
                                Toast.LENGTH_LONG
                        ).show();
                    }
                });
    }


    // TIMER


    private void startTimer() {

        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        countDownTimer =
                new CountDownTimer(
                        180000,
                        1000
                ) {

                    @Override
                    public void onTick(long millisUntilFinished) {

                        long minutes =
                                millisUntilFinished / 60000;

                        long seconds =
                                (millisUntilFinished % 60000)
                                        / 1000;

                        String time =
                                String.format(
                                        "OTP expires in %02d:%02d",
                                        minutes,
                                        seconds
                                );

                        tvTimer.setText(time);
                    }

                    @Override
                    public void onFinish() {

                        tvTimer.setText(
                                "OTP expired"
                        );

                        btnVerifyOTP.setEnabled(false);

                        tvResendOTP.setEnabled(true);
                    }
                };

        countDownTimer.start();
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();

        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }
}