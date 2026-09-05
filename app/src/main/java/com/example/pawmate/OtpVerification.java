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
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class OtpVerification extends AppCompatActivity {

    // OTP INPUT FIELDS

    private EditText otp1;
    private EditText otp2;
    private EditText otp3;
    private EditText otp4;
    private EditText otp5;
    private EditText otp6;

    // TEXT VIEWS

    private TextView tvPhone;
    private TextView tvTimer;
    private TextView tvResendOTP;
    private TextView tvClearOTP;

    // BUTTON

    private MaterialButton btnVerifyOTP;

    // FIREBASE

    private FirebaseAuth mAuth;

    // VERIFICATION DATA

    private String verificationId;
    private String phoneNumber;

    // TIMER
    private CountDownTimer countDownTimer;

    // FLAGS

    private boolean isVerifying = false;
    private boolean isResending = false;


    // ON CREATE

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_otp_verification);

        // Initialize Firebase
        initializeFirebase();

        // Initialize views
        initializeViews();

        // Get data from previous Activity
        getIntentData();

        // Setup OTP input fields
        setupOtpInputs();

        // Setup button and text click listeners
        setupClickListeners();

        // Start OTP timer
        startTimer();

        // Focus first OTP field
        otp1.requestFocus();
    }


    // INITIALIZE FIREBASE

    private void initializeFirebase() {

        mAuth = FirebaseAuth.getInstance();
    }


    // INITIALIZE VIEWS

    private void initializeViews() {

        otp1 = findViewById(R.id.otp1);
        otp2 = findViewById(R.id.otp2);
        otp3 = findViewById(R.id.otp3);
        otp4 = findViewById(R.id.otp4);
        otp5 = findViewById(R.id.otp5);
        otp6 = findViewById(R.id.otp6);

        tvPhone = findViewById(R.id.tvPhone);
        tvTimer = findViewById(R.id.tvTimer);
        tvResendOTP = findViewById(R.id.tvResendOTP);
        tvClearOTP = findViewById(R.id.tvClearOTP);

        btnVerifyOTP = findViewById(R.id.btnVerifyOTP);
    }


    // GET DATA FROM PREVIOUS ACTIVITY

    private void getIntentData() {

        verificationId =
                getIntent().getStringExtra("verificationId");

        phoneNumber =
                getIntent().getStringExtra("phoneNumber");


        if (phoneNumber != null && !phoneNumber.isEmpty()) {

            tvPhone.setText(phoneNumber);
        }
    }


    // CLICK LISTENERS

    private void setupClickListeners() {

        // Verify OTP button
        btnVerifyOTP.setOnClickListener(v -> verifyOTP());


        // Clear OTP
        tvClearOTP.setOnClickListener(v -> clearOtpFields());


        // Resend OTP
        tvResendOTP.setOnClickListener(v -> {

            if (!tvResendOTP.isEnabled() || isResending) {
                return;
            }

            resendOTP();
        });
    }


    // OTP INPUT SETUP

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


        // Automatically verify when the 6th digit is entered
        otp6.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(
                    CharSequence s,
                    int start,
                    int count,
                    int after
            ) {
                // Not needed
            }


            @Override
            public void onTextChanged(
                    CharSequence s,
                    int start,
                    int before,
                    int count
            ) {

                if (s.length() == 1) {

                    verifyOTP();
                }
            }


            @Override
            public void afterTextChanged(Editable s) {
                // Not needed
            }
        });
    }


    // OTP TEXT WATCHER

    private TextWatcher createTextWatcher(
            EditText current,
            EditText next
    ) {

        return new TextWatcher() {

            @Override
            public void beforeTextChanged(
                    CharSequence s,
                    int start,
                    int count,
                    int after
            ) {
                // Not needed
            }


            @Override
            public void onTextChanged(
                    CharSequence s,
                    int start,
                    int before,
                    int count
            ) {

                if (s.length() == 1) {

                    next.requestFocus();
                }
            }


            @Override
            public void afterTextChanged(Editable s) {
                // Not needed
            }
        };
    }


    // VERIFY OTP

    private void verifyOTP() {

        // Prevent multiple verification requests
        if (isVerifying) {
            return;
        }


        // Get complete OTP
        String code = getOtpCode();


        // Check OTP length
        if (code.length() != 6) {

            Toast.makeText(
                    this,
                    "Please enter the complete 6-digit OTP",
                    Toast.LENGTH_SHORT
            ).show();

            focusFirstEmptyField();

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


        // Start verification
        isVerifying = true;


        btnVerifyOTP.setEnabled(false);
        btnVerifyOTP.setText("Verifying...");


        // Create Firebase credential
        PhoneAuthCredential credential =
                PhoneAuthProvider.getCredential(
                        verificationId,
                        code
                );


        // Verify OTP with Firebase
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {

                        // OTP VERIFIED SUCCESSFULLY
                        handleVerificationSuccess();

                    } else {

                        // OTP VERIFICATION FAILED
                        handleVerificationFailure(
                                task.getException()
                        );
                    }
                });
    }


    // GET OTP CODE

    private String getOtpCode() {

        return otp1.getText().toString().trim()
                + otp2.getText().toString().trim()
                + otp3.getText().toString().trim()
                + otp4.getText().toString().trim()
                + otp5.getText().toString().trim()
                + otp6.getText().toString().trim();
    }


    // OTP VERIFICATION SUCCESS

    private void handleVerificationSuccess() {

        // Stop timer
        if (countDownTimer != null) {

            countDownTimer.cancel();
        }


        // Show success message
        Toast.makeText(
                this,
                "Phone verified successfully!",
                Toast.LENGTH_SHORT
        ).show();


        // GO TO PASSWORD SUCCESS SCREEN

        Intent intent = new Intent(
                OtpVerification.this,
                PasswordSuccess.class
        );


        // Pass phone number if available
        if (phoneNumber != null) {

            intent.putExtra(
                    "phoneNumber",
                    phoneNumber
            );
        }


        // Start Password Success Activity
        startActivity(intent);


        // Close OTP Activity
        // User cannot go back to OTP screen
        finish();
    }


    // OTP VERIFICATION FAILURE

    private void handleVerificationFailure(
            Exception exception
    ) {

        isVerifying = false;


        btnVerifyOTP.setEnabled(true);
        btnVerifyOTP.setText("Verify OTP");


        // Clear OTP fields
        clearOtpFields();


        String errorMessage =
                "Invalid OTP. Please try again.";


        if (exception != null &&
                exception.getMessage() != null &&
                !exception.getMessage().isEmpty()) {

            String firebaseMessage =
                    exception.getMessage();


            if (firebaseMessage
                    .toLowerCase()
                    .contains("invalid verification code")) {

                errorMessage =
                        "Incorrect OTP. Please try again.";


            } else if (firebaseMessage
                    .toLowerCase()
                    .contains("session expired")) {

                errorMessage =
                        "This OTP has expired. Please request a new OTP.";
            }
        }


        Toast.makeText(
                this,
                errorMessage,
                Toast.LENGTH_LONG
        ).show();
    }


    // CLEAR OTP FIELDS

    private void clearOtpFields() {

        otp1.setText("");
        otp2.setText("");
        otp3.setText("");
        otp4.setText("");
        otp5.setText("");
        otp6.setText("");


        otp1.requestFocus();
    }


    // FOCUS FIRST EMPTY OTP FIELD

    private void focusFirstEmptyField() {

        if (otp1.getText().toString().isEmpty()) {

            otp1.requestFocus();


        } else if (otp2.getText().toString().isEmpty()) {

            otp2.requestFocus();


        } else if (otp3.getText().toString().isEmpty()) {

            otp3.requestFocus();


        } else if (otp4.getText().toString().isEmpty()) {

            otp4.requestFocus();


        } else if (otp5.getText().toString().isEmpty()) {

            otp5.requestFocus();


        } else {

            otp6.requestFocus();
        }
    }


    // RESEND OTP

    private void resendOTP() {

        if (phoneNumber == null ||
                phoneNumber.isEmpty()) {

            Toast.makeText(
                    this,
                    "Phone number is missing.",
                    Toast.LENGTH_LONG
            ).show();

            return;
        }


        if (isResending) {
            return;
        }


        isResending = true;


        tvResendOTP.setEnabled(false);


        Toast.makeText(
                this,
                "Sending new OTP...",
                Toast.LENGTH_SHORT
        ).show();


        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)

                        .setPhoneNumber(phoneNumber)

                        .setTimeout(
                                60L,
                                TimeUnit.SECONDS
                        )

                        .setActivity(this)

                        .setCallbacks(
                                new PhoneAuthProvider
                                        .OnVerificationStateChangedCallbacks() {


                                    // AUTOMATIC VERIFICATION

                                    @Override
                                    public void onVerificationCompleted(
                                            PhoneAuthCredential credential
                                    ) {

                                        signInAutomatically(
                                                credential
                                        );
                                    }


                                    // VERIFICATION FAILED

                                    @Override
                                    public void onVerificationFailed(
                                            FirebaseException e
                                    ) {

                                        isResending = false;

                                        tvResendOTP.setEnabled(true);


                                        String message =
                                                e.getMessage();


                                        if (message == null ||
                                                message.isEmpty()) {

                                            message =
                                                    "Failed to send new OTP.";
                                        }


                                        Toast.makeText(
                                                OtpVerification.this,
                                                message,
                                                Toast.LENGTH_LONG
                                        ).show();
                                    }


                                    // OTP SENT

                                    @Override
                                    public void onCodeSent(
                                            String newVerificationId,

                                            PhoneAuthProvider
                                                    .ForceResendingToken token
                                    ) {

                                        // Replace old verification ID
                                        verificationId =
                                                newVerificationId;


                                        isResending = false;


                                        // Clear old OTP
                                        clearOtpFields();


                                        // Restart timer
                                        startTimer();


                                        Toast.makeText(
                                                OtpVerification.this,
                                                "New OTP sent!",
                                                Toast.LENGTH_SHORT
                                        ).show();
                                    }
                                }
                        )

                        .build();


        PhoneAuthProvider.verifyPhoneNumber(options);
    }


    // AUTOMATIC VERIFICATION

    private void signInAutomatically(
            PhoneAuthCredential credential
    ) {

        if (isVerifying) {
            return;
        }


        isVerifying = true;


        btnVerifyOTP.setEnabled(false);
        btnVerifyOTP.setText("Verifying...");


        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {

                        // Automatically go to PasswordSuccess
                        handleVerificationSuccess();

                    } else {

                        handleVerificationFailure(
                                task.getException()
                        );
                    }
                });
    }


    // OTP TIMER

    private void startTimer() {

        if (countDownTimer != null) {

            countDownTimer.cancel();
        }


        tvResendOTP.setEnabled(false);


        countDownTimer = new CountDownTimer(
                60_000,
                1_000
        ) {

            @Override
            public void onTick(
                    long millisUntilFinished
            ) {

                long seconds =
                        millisUntilFinished / 1_000;


                long minutes =
                        seconds / 60;


                seconds =
                        seconds % 60;


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

                tvTimer.setText("OTP expired");

                tvResendOTP.setEnabled(true);
            }
        };


        countDownTimer.start();
    }


    // ACTIVITY LIFECYCLE

    @Override
    protected void onDestroy() {

        if (countDownTimer != null) {

            countDownTimer.cancel();
        }


        super.onDestroy();
    }
}