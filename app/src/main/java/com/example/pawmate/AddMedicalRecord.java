package com.example.pawmate;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class AddMedicalRecord extends AppCompatActivity {

    private TextView tvTitle;

    private EditText etDiagnosis;
    private EditText etDate;
    private EditText etVetName;
    private EditText etNotes;

    private MaterialButton btnSaveRecord;

    private FirebaseFirestore firestore;

    private String petId;
    private String recordId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(
                R.layout.activity_add_medical_record
        );


        // Firebase
        firestore =
                FirebaseFirestore.getInstance();


        // Get Pet ID
        petId =
                getIntent()
                        .getStringExtra("petId");


        // Get Record ID
        recordId =
                getIntent()
                        .getStringExtra("recordId");


        // Find Views

        tvTitle =
                findViewById(R.id.tvTitle);


        etDiagnosis =
                findViewById(R.id.etDiagnosis);


        etDate =
                findViewById(R.id.etDate);


        etVetName =
                findViewById(R.id.etVetName);


        etNotes =
                findViewById(R.id.etNotes);


        btnSaveRecord =
                findViewById(R.id.btnSaveRecord);


        // Date picker

        etDate.setOnClickListener(v -> {

            showDatePicker();
        });


        // Check Edit Mode

        if (recordId != null) {

            tvTitle.setText(
                    "Edit Medical Record"
            );


            btnSaveRecord.setText(
                    "Update Medical Record"
            );


            etDiagnosis.setText(
                    getIntent()
                            .getStringExtra("diagnosis")
            );


            etDate.setText(
                    getIntent()
                            .getStringExtra("date")
            );


            etVetName.setText(
                    getIntent()
                            .getStringExtra("vetName")
            );


            etNotes.setText(
                    getIntent()
                            .getStringExtra("notes")
            );
        }


        // Save / Update

        btnSaveRecord.setOnClickListener(v -> {

            if (recordId == null) {

                saveMedicalRecord();

            } else {

                updateMedicalRecord();
            }
        });
    }


    // ==========================================
    // DATE PICKER
    // ==========================================

    private void showDatePicker() {

        Calendar calendar =
                Calendar.getInstance();


        int year =
                calendar.get(Calendar.YEAR);


        int month =
                calendar.get(Calendar.MONTH);


        int day =
                calendar.get(Calendar.DAY_OF_MONTH);


        DatePickerDialog dialog =
                new DatePickerDialog(
                        this,
                        (view, selectedYear,
                         selectedMonth,
                         selectedDay) -> {

                            String date =
                                    String.format(
                                            "%02d/%02d/%04d",
                                            selectedDay,
                                            selectedMonth + 1,
                                            selectedYear
                                    );


                            etDate.setText(date);
                        },
                        year,
                        month,
                        day
                );


        dialog.show();
    }


    // ==========================================
    // SAVE
    // ==========================================

    private void saveMedicalRecord() {

        String diagnosis =
                etDiagnosis
                        .getText()
                        .toString()
                        .trim();


        String date =
                etDate
                        .getText()
                        .toString()
                        .trim();


        String vetName =
                etVetName
                        .getText()
                        .toString()
                        .trim();


        String notes =
                etNotes
                        .getText()
                        .toString()
                        .trim();


        // Validation

        if (diagnosis.isEmpty()) {

            etDiagnosis.setError(
                    "Enter diagnosis"
            );

            etDiagnosis.requestFocus();

            return;
        }


        if (date.isEmpty()) {

            Toast.makeText(
                    this,
                    "Select medical record date",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }


        if (vetName.isEmpty()) {

            etVetName.setError(
                    "Enter veterinarian name"
            );

            etVetName.requestFocus();

            return;
        }


        FirebaseUser user =
                FirebaseAuth
                        .getInstance()
                        .getCurrentUser();


        if (user == null) {

            Toast.makeText(
                    this,
                    "Please login again",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }


        String uid =
                user.getUid();


        // Create data

        Map<String, Object> data =
                new HashMap<>();


        data.put(
                "diagnosis",
                diagnosis
        );


        data.put(
                "date",
                date
        );


        data.put(
                "vetName",
                vetName
        );


        data.put(
                "notes",
                notes
        );


        data.put(
                "createdAt",
                System.currentTimeMillis()
        );


        // Save

        firestore
                .collection("users")
                .document(uid)
                .collection("pets")
                .document(petId)
                .collection("medicalRecords")
                .add(data)

                .addOnSuccessListener(documentReference -> {

                    Toast.makeText(
                            this,
                            "Medical record saved",
                            Toast.LENGTH_SHORT
                    ).show();


                    finish();
                })

                .addOnFailureListener(e -> {

                    Toast.makeText(
                            this,
                            "Failed to save medical record",
                            Toast.LENGTH_LONG
                    ).show();
                });
    }


    // ==========================================
    // UPDATE
    // ==========================================

    private void updateMedicalRecord() {

        String diagnosis =
                etDiagnosis
                        .getText()
                        .toString()
                        .trim();


        String date =
                etDate
                        .getText()
                        .toString()
                        .trim();


        String vetName =
                etVetName
                        .getText()
                        .toString()
                        .trim();


        String notes =
                etNotes
                        .getText()
                        .toString()
                        .trim();


        // Validation

        if (diagnosis.isEmpty()) {

            etDiagnosis.setError(
                    "Enter diagnosis"
            );

            return;
        }


        if (date.isEmpty()) {

            Toast.makeText(
                    this,
                    "Select date",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }


        if (vetName.isEmpty()) {

            etVetName.setError(
                    "Enter veterinarian name"
            );

            return;
        }


        FirebaseUser user =
                FirebaseAuth
                        .getInstance()
                        .getCurrentUser();


        if (user == null) {

            Toast.makeText(
                    this,
                    "Please login again",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }


        String uid =
                user.getUid();


        Map<String, Object> data =
                new HashMap<>();


        data.put(
                "diagnosis",
                diagnosis
        );


        data.put(
                "date",
                date
        );


        data.put(
                "vetName",
                vetName
        );


        data.put(
                "notes",
                notes
        );


        firestore
                .collection("users")
                .document(uid)
                .collection("pets")
                .document(petId)
                .collection("medicalRecords")
                .document(recordId)
                .update(data)

                .addOnSuccessListener(unused -> {

                    Toast.makeText(
                            this,
                            "Medical record updated",
                            Toast.LENGTH_SHORT
                    ).show();


                    finish();
                })

                .addOnFailureListener(e -> {

                    Toast.makeText(
                            this,
                            "Failed to update record",
                            Toast.LENGTH_LONG
                    ).show();
                });
    }
}