package com.example.pawmate;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class AddVaccination extends AppCompatActivity {

    private EditText etVaccineName;
    private EditText etVaccinationDate;
    private EditText etNextDate;
    private EditText etNotes;

    private MaterialButton btnSaveVaccination;

    private FirebaseFirestore firestore;

    private String petId;
    private String vaccinationId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_vaccination);

        firestore = FirebaseFirestore.getInstance();

        petId = getIntent().getStringExtra("petId");

        vaccinationId =
                getIntent().getStringExtra("vaccinationId");

        etVaccineName =
                findViewById(R.id.etVaccineName);

        etVaccinationDate =
                findViewById(R.id.etVaccinationDate);

        etNextDate =
                findViewById(R.id.etNextDate);

        etNotes =
                findViewById(R.id.etNotes);

        btnSaveVaccination =
                findViewById(R.id.btnSaveVaccination);

        /*
         * Check if this page is opened
         * for editing an existing vaccination.
         */
        if (vaccinationId != null &&
                !vaccinationId.isEmpty()) {

            etVaccineName.setText(
                    getIntent().getStringExtra("name")
            );

            etVaccinationDate.setText(
                    getIntent().getStringExtra("date")
            );

            etNextDate.setText(
                    getIntent().getStringExtra("nextDate")
            );

            etNotes.setText(
                    getIntent().getStringExtra("notes")
            );

            btnSaveVaccination.setText(
                    "Update Vaccination"
            );
        }

        /*
         * Vaccination date picker.
         */
        etVaccinationDate.setOnClickListener(v ->
                showDatePicker(etVaccinationDate)
        );

        /*
         * Next vaccination date picker.
         */
        etNextDate.setOnClickListener(v ->
                showDatePicker(etNextDate)
        );

        /*
         * Save or update vaccination.
         */
        btnSaveVaccination.setOnClickListener(v -> {

            if (vaccinationId == null ||
                    vaccinationId.isEmpty()) {

                saveVaccination();

            } else {

                updateVaccination(vaccinationId);
            }
        });
    }

    /*
     * Show date picker.
     */
    private void showDatePicker(EditText editText) {

        Calendar calendar = Calendar.getInstance();

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog =
                new DatePickerDialog(
                        this,
                        (view, selectedYear,
                         selectedMonth, selectedDay) -> {

                            String date =
                                    selectedDay + " "
                                            + getMonthName(
                                            selectedMonth)
                                            + " "
                                            + selectedYear;

                            editText.setText(date);
                        },
                        year,
                        month,
                        day
                );

        datePickerDialog.show();
    }

    /*
     * Get month name.
     */
    private String getMonthName(int month) {

        String[] months = {
                "January",
                "February",
                "March",
                "April",
                "May",
                "June",
                "July",
                "August",
                "September",
                "October",
                "November",
                "December"
        };

        return months[month];
    }

    /*
     * Save a new vaccination.
     */
    private void saveVaccination() {

        String vaccineName =
                etVaccineName
                        .getText()
                        .toString()
                        .trim();

        String vaccinationDate =
                etVaccinationDate
                        .getText()
                        .toString()
                        .trim();

        String nextDate =
                etNextDate
                        .getText()
                        .toString()
                        .trim();

        String notes =
                etNotes
                        .getText()
                        .toString()
                        .trim();

        /*
         * Validate vaccine name.
         */
        if (vaccineName.isEmpty()) {

            etVaccineName.setError(
                    "Enter vaccine name"
            );

            etVaccineName.requestFocus();

            return;
        }

        /*
         * Validate vaccination date.
         */
        if (vaccinationDate.isEmpty()) {

            Toast.makeText(
                    this,
                    "Select vaccination date",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        /*
         * Validate next vaccination date.
         */
        if (nextDate.isEmpty()) {

            Toast.makeText(
                    this,
                    "Select next vaccination date",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        /*
         * Check logged-in user.
         */
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

        /*
         * Check pet ID.
         */
        if (petId == null || petId.isEmpty()) {

            Toast.makeText(
                    this,
                    "Pet information not found",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        String uid = user.getUid();

        /*
         * Create a new vaccination ID.
         */
        vaccinationId =
                firestore
                        .collection("users")
                        .document(uid)
                        .collection("pets")
                        .document(petId)
                        .collection("vaccinations")
                        .document()
                        .getId();

        /*
         * Create vaccination data.
         */
        Map<String, Object> vaccination =
                new HashMap<>();

        vaccination.put(
                "id",
                vaccinationId
        );

        vaccination.put(
                "name",
                vaccineName
        );

        vaccination.put(
                "date",
                vaccinationDate
        );

        vaccination.put(
                "nextDate",
                nextDate
        );

        vaccination.put(
                "notes",
                notes
        );

        /*
         * Save vaccination to Firestore.
         */
        firestore
                .collection("users")
                .document(uid)
                .collection("pets")
                .document(petId)
                .collection("vaccinations")
                .document(vaccinationId)
                .set(vaccination)

                .addOnSuccessListener(unused -> {

                    Toast.makeText(
                            this,
                            "Vaccination saved successfully",
                            Toast.LENGTH_SHORT
                    ).show();

                    finish();
                })

                .addOnFailureListener(e -> {

                    Toast.makeText(
                            this,
                            "Failed to save vaccination",
                            Toast.LENGTH_LONG
                    ).show();
                });
    }

    /*
     * Update an existing vaccination.
     */
    private void updateVaccination(
            String vaccinationId) {

        String vaccineName =
                etVaccineName
                        .getText()
                        .toString()
                        .trim();

        String vaccinationDate =
                etVaccinationDate
                        .getText()
                        .toString()
                        .trim();

        String nextDate =
                etNextDate
                        .getText()
                        .toString()
                        .trim();

        String notes =
                etNotes
                        .getText()
                        .toString()
                        .trim();

        /*
         * Validate vaccine name.
         */
        if (vaccineName.isEmpty()) {

            etVaccineName.setError(
                    "Enter vaccine name"
            );

            return;
        }

        /*
         * Validate vaccination date.
         */
        if (vaccinationDate.isEmpty()) {

            Toast.makeText(
                    this,
                    "Select vaccination date",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        /*
         * Validate next vaccination date.
         */
        if (nextDate.isEmpty()) {

            Toast.makeText(
                    this,
                    "Select next vaccination date",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        /*
         * Check logged-in user.
         */
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

        /*
         * Check pet ID.
         */
        if (petId == null || petId.isEmpty()) {

            Toast.makeText(
                    this,
                    "Pet information not found",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        String uid = user.getUid();

        /*
         * Create updated vaccination data.
         */
        Map<String, Object> vaccination =
                new HashMap<>();

        vaccination.put(
                "id",
                vaccinationId
        );

        vaccination.put(
                "name",
                vaccineName
        );

        vaccination.put(
                "date",
                vaccinationDate
        );

        vaccination.put(
                "nextDate",
                nextDate
        );

        vaccination.put(
                "notes",
                notes
        );

        /*
         * Update the existing Firestore document.
         */
        firestore
                .collection("users")
                .document(uid)
                .collection("pets")
                .document(petId)
                .collection("vaccinations")
                .document(vaccinationId)
                .set(vaccination)

                .addOnSuccessListener(unused -> {

                    Toast.makeText(
                            this,
                            "Vaccination updated successfully",
                            Toast.LENGTH_SHORT
                    ).show();

                    finish();
                })

                .addOnFailureListener(e -> {

                    Toast.makeText(
                            this,
                            "Failed to update vaccination",
                            Toast.LENGTH_LONG
                    ).show();
                });
    }
}