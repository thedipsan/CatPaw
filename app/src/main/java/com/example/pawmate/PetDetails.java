package com.example.pawmate;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class PetDetails extends AppCompatActivity {

    // =========================================================
    // HEADER / BUTTONS
    // =========================================================

    private ImageButton btnBack;
    private MaterialButton btnEditPet;
    private MaterialButton btnDelete;
    private ImageButton btnRoutineMore;


    // =========================================================
    // PET INFORMATION
    // =========================================================

    private TextView tvPetName;
    private TextView tvBreed;
    private TextView tvWeight;
    private TextView tvGender;
    private TextView tvNotes;


    // =========================================================
    // HEALTH INFORMATION
    // =========================================================

    private TextView tvVaccinationStatus;
    private TextView tvLastVetCheck;
    private TextView tvAllergies;


    // =========================================================
    // ROUTINE INFORMATION
    // =========================================================

    private TextView tvRoutineName;
    private TextView tvRoutineTime;


    // =========================================================
    // FIREBASE
    // =========================================================

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;


    // =========================================================
    // PET ID
    // =========================================================

    private String petId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // =====================================================
        // LOAD LAYOUT
        // =====================================================

        setContentView(R.layout.activity_pet_details);


        // =====================================================
        // INITIALIZE FIREBASE
        // =====================================================

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();


        // =====================================================
        // INITIALIZE VIEWS
        // =====================================================

        btnBack = findViewById(R.id.btnBack);
        btnEditPet = findViewById(R.id.btnEditPet);
        btnDelete = findViewById(R.id.btnDeletePet);
        btnRoutineMore = findViewById(R.id.btnRoutineMore);


        // Pet information
        tvPetName = findViewById(R.id.tvPetName);
        tvBreed = findViewById(R.id.tvBreed);
        tvWeight = findViewById(R.id.tvWeight);
        tvGender = findViewById(R.id.tvGender);
        tvNotes = findViewById(R.id.tvNotes);


        // Health information
        tvVaccinationStatus =
                findViewById(R.id.tvVaccinationStatus);

        tvLastVetCheck =
                findViewById(R.id.tvLastVetCheck);

        tvAllergies =
                findViewById(R.id.tvAllergies);


        // Routine information
        tvRoutineName =
                findViewById(R.id.tvRoutineName);

        tvRoutineTime =
                findViewById(R.id.tvRoutineTime);


        // =====================================================
        // GET PET ID
        // =====================================================

        petId = getIntent().getStringExtra("petId");


        // =====================================================
        // CHECK PET ID
        // =====================================================

        if (petId == null || petId.trim().isEmpty()) {

            Toast.makeText(
                    this,
                    "Pet information not found",
                    Toast.LENGTH_SHORT
            ).show();

            finish();
            return;
        }


        // =====================================================
        // BACK BUTTON
        // =====================================================

        btnBack.setOnClickListener(v -> {
            finish();
        });


        // =====================================================
        // EDIT PET
        // =====================================================

        btnEditPet.setOnClickListener(v -> {

            Intent intent = new Intent(
                    PetDetails.this,
                    EditPet.class
            );

            intent.putExtra("petId", petId);

            startActivity(intent);
        });


        // =====================================================
        // DELETE PET
        // =====================================================

        btnDelete.setOnClickListener(v -> {
            showDeleteDialog();
        });


        // =====================================================
        // ROUTINE MORE BUTTON
        // =====================================================

        btnRoutineMore.setOnClickListener(v -> {

            Toast.makeText(
                    PetDetails.this,
                    "Routine options",
                    Toast.LENGTH_SHORT
            ).show();

        });


        // =====================================================
        // LOAD PET FROM FIRESTORE
        // =====================================================

        loadPet();
    }


    // =========================================================
    // LOAD PET
    // =========================================================

    private void loadPet() {

        FirebaseUser user = mAuth.getCurrentUser();

        // -----------------------------------------------------
        // CHECK LOGIN
        // -----------------------------------------------------

        if (user == null) {

            Toast.makeText(
                    this,
                    "Please login again",
                    Toast.LENGTH_SHORT
            ).show();

            finish();
            return;
        }


        String uid = user.getUid();


        // -----------------------------------------------------
        // FIRESTORE PATH
        // users/{uid}/pets/{petId}
        // -----------------------------------------------------

        db.collection("users")
                .document(uid)
                .collection("pets")
                .document(petId)
                .get()
                .addOnSuccessListener(document -> {

                    // -------------------------------------------------
                    // PET DOES NOT EXIST
                    // -------------------------------------------------

                    if (!document.exists()) {

                        Toast.makeText(
                                PetDetails.this,
                                "Pet not found",
                                Toast.LENGTH_SHORT
                        ).show();

                        finish();
                        return;
                    }


                    // =================================================
                    // BASIC PET INFORMATION
                    // =================================================

                    String name =
                            document.getString("name");

                    String breed =
                            document.getString("breed");

                    String age =
                            document.getString("age");

                    String weight =
                            document.getString("weight");

                    String gender =
                            document.getString("gender");

                    String notes =
                            document.getString("notes");


                    // =================================================
                    // HEALTH INFORMATION
                    // =================================================

                    String vaccinationStatus =
                            document.getString("vaccinationStatus");

                    String lastVetCheck =
                            document.getString("lastVetCheck");

                    String allergies =
                            document.getString("allergies");


                    // =================================================
                    // ROUTINE INFORMATION
                    // =================================================

                    String routineName =
                            document.getString("routineName");

                    String routineTime =
                            document.getString("routineTime");


                    // =================================================
                    // DEFAULT VALUES
                    // =================================================

                    if (name == null || name.trim().isEmpty()) {
                        name = "Unknown Pet";
                    }

                    if (breed == null || breed.trim().isEmpty()) {
                        breed = "Unknown Breed";
                    }

                    if (age == null || age.trim().isEmpty()) {
                        age = "N/A";
                    }

                    if (weight == null || weight.trim().isEmpty()) {
                        weight = "N/A";
                    }

                    if (gender == null || gender.trim().isEmpty()) {
                        gender = "N/A";
                    }

                    if (notes == null || notes.trim().isEmpty()) {
                        notes = "No additional notes.";
                    }

                    if (vaccinationStatus == null ||
                            vaccinationStatus.trim().isEmpty()) {

                        vaccinationStatus = "Not available";
                    }

                    if (lastVetCheck == null ||
                            lastVetCheck.trim().isEmpty()) {

                        lastVetCheck = "Not available";
                    }

                    if (allergies == null ||
                            allergies.trim().isEmpty()) {

                        allergies = "None";
                    }

                    if (routineName == null ||
                            routineName.trim().isEmpty()) {

                        routineName = "No routine added";
                    }

                    if (routineTime == null ||
                            routineTime.trim().isEmpty()) {

                        routineTime = "No schedule";
                    }


                    // =================================================
                    // DISPLAY BASIC PET INFORMATION
                    // =================================================

                    tvPetName.setText(name);


                    // Breed + Age
                    tvBreed.setText(
                            breed + " • " + age + " years"
                    );


                    // Weight
                    if (weight.equals("N/A")) {

                        tvWeight.setText("Weight unavailable");

                    } else {

                        tvWeight.setText(
                                weight + " kg"
                        );
                    }


                    // Gender
                    tvGender.setText(gender);


                    // Notes
                    tvNotes.setText(notes);


                    // =================================================
                    // DISPLAY HEALTH INFORMATION
                    // =================================================

                    tvVaccinationStatus.setText(
                            vaccinationStatus
                    );

                    tvLastVetCheck.setText(
                            lastVetCheck
                    );

                    tvAllergies.setText(
                            allergies
                    );


                    // =================================================
                    // DISPLAY ROUTINE INFORMATION
                    // =================================================

                    tvRoutineName.setText(
                            routineName
                    );

                    tvRoutineTime.setText(
                            routineTime
                    );

                })
                .addOnFailureListener(e -> {

                    Toast.makeText(
                            PetDetails.this,
                            "Failed to load pet: "
                                    + e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();

                });
    }


    // =========================================================
    // DELETE CONFIRMATION DIALOG
    // =========================================================

    private void showDeleteDialog() {

        new AlertDialog.Builder(this)

                .setTitle("Delete Pet?")

                .setMessage(
                        "Are you sure you want to delete this pet?\n\n"
                                + "This action cannot be undone."
                )

                .setNegativeButton(
                        "Cancel",
                        null
                )

                .setPositiveButton(
                        "Delete",
                        (dialog, which) -> deletePet()
                )

                .show();
    }


    // =========================================================
    // DELETE PET
    // =========================================================

    private void deletePet() {

        FirebaseUser user = mAuth.getCurrentUser();


        // -----------------------------------------------------
        // CHECK LOGIN
        // -----------------------------------------------------

        if (user == null) {

            Toast.makeText(
                    this,
                    "Please login again",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }


        String uid = user.getUid();


        // -----------------------------------------------------
        // DELETE PET
        // users/{uid}/pets/{petId}
        // -----------------------------------------------------

        db.collection("users")
                .document(uid)
                .collection("pets")
                .document(petId)
                .delete()
                .addOnSuccessListener(unused -> {

                    Toast.makeText(
                            PetDetails.this,
                            "Pet deleted successfully",
                            Toast.LENGTH_SHORT
                    ).show();


                    // Return to previous screen
                    finish();

                })
                .addOnFailureListener(e -> {

                    Toast.makeText(
                            PetDetails.this,
                            "Failed to delete pet: "
                                    + e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();

                });
    }


    // =========================================================
    // REFRESH PET AFTER EDIT
    // =========================================================

    @Override
    protected void onResume() {
        super.onResume();

        /*
         * When EditPet finishes, this screen becomes visible
         * again and reloads the latest Firestore data.
         */

        if (petId != null && !petId.trim().isEmpty()) {
            loadPet();
        }
    }
}