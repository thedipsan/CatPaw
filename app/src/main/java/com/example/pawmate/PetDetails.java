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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class PetDetails extends AppCompatActivity {

    private ImageButton btnBack;
    private MaterialButton btnEditPet;
    private MaterialButton btnDeletePet;
    private MaterialButton btnHealthRecords;
    private ImageButton btnRoutineMore;

    private TextView tvPetName;
    private TextView tvBreed;
    private TextView tvWeight;
    private TextView tvGender;
    private TextView tvNotes;

    private TextView tvVaccinationStatus;
    private TextView tvLastVetCheck;
    private TextView tvAllergies;

    private TextView tvRoutineName;
    private TextView tvRoutineTime;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private String petId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_pet_details);

        initFirebase();
        initViews();

        petId = getIntent().getStringExtra("petId");

        if (petId == null || petId.isEmpty()) {
            Toast.makeText(
                    this,
                    "Pet information not found",
                    Toast.LENGTH_SHORT
            ).show();

            finish();
            return;
        }

        setupListeners();
        loadPet();
    }

    private void initFirebase() {
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    private void initViews() {

        btnBack = findViewById(R.id.btnBack);
        btnEditPet = findViewById(R.id.btnEditPet);
        btnDeletePet = findViewById(R.id.btnDeletePet);
        btnHealthRecords = findViewById(R.id.btnHealthRecords);
        btnRoutineMore = findViewById(R.id.btnRoutineMore);

        tvPetName = findViewById(R.id.tvPetName);
        tvBreed = findViewById(R.id.tvBreed);
        tvWeight = findViewById(R.id.tvWeight);
        tvGender = findViewById(R.id.tvGender);
        tvNotes = findViewById(R.id.tvNotes);

        tvVaccinationStatus =
                findViewById(R.id.tvVaccinationStatus);

        tvLastVetCheck =
                findViewById(R.id.tvLastVetCheck);

        tvAllergies =
                findViewById(R.id.tvAllergies);

        tvRoutineName =
                findViewById(R.id.tvRoutineName);

        tvRoutineTime =
                findViewById(R.id.tvRoutineTime);
    }

    private void setupListeners() {

        // Back button
        btnBack.setOnClickListener(v -> {
            finish();
        });

        // Edit Pet button
        btnEditPet.setOnClickListener(v -> {

            Intent intent = new Intent(
                    PetDetails.this,
                    EditPet.class
            );

            intent.putExtra("petId", petId);

            startActivity(intent);
        });

        // Health Records button
        btnHealthRecords.setOnClickListener(v -> {

            Intent intent = new Intent(
                    PetDetails.this,
                    HealthRecord.class
            );

            intent.putExtra("petId", petId);

            startActivity(intent);
        });

        // Delete Pet button
        btnDeletePet.setOnClickListener(v -> {
            showDeleteDialog();
        });

        // Routine more button
        btnRoutineMore.setOnClickListener(v -> {

            Toast.makeText(
                    PetDetails.this,
                    "Routine options",
                    Toast.LENGTH_SHORT
            ).show();
        });
    }

    private void loadPet() {

        FirebaseUser user = mAuth.getCurrentUser();

        if (user == null) {

            Toast.makeText(
                    this,
                    "Please login first",
                    Toast.LENGTH_SHORT
            ).show();

            finish();
            return;
        }

        String uid = user.getUid();

        db.collection("users")
                .document(uid)
                .collection("pets")
                .document(petId)
                .get()
                .addOnSuccessListener(document -> {

                    if (document.exists()) {
                        displayPet(document);
                    } else {

                        Toast.makeText(
                                PetDetails.this,
                                "Pet not found",
                                Toast.LENGTH_SHORT
                        ).show();

                        finish();
                    }
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

    private void displayPet(DocumentSnapshot document) {

        String name = document.getString("name");
        String breed = document.getString("breed");
        String age = document.getString("age");
        String weight = document.getString("weight");
        String gender = document.getString("gender");
        String notes = document.getString("notes");

        String vaccinationStatus =
                document.getString("vaccinationStatus");

        String lastVetCheck =
                document.getString("lastVetCheck");

        String allergies =
                document.getString("allergies");

        String routineName =
                document.getString("routineName");

        String routineTime =
                document.getString("routineTime");

        // Pet name
        if (name != null && !name.isEmpty()) {
            tvPetName.setText(name);
        } else {
            tvPetName.setText("Pet");
        }

        // Breed + Age
        String breedAge = "";

        if (breed != null && !breed.isEmpty()) {
            breedAge = breed;
        }

        if (age != null && !age.isEmpty()) {

            if (!breedAge.isEmpty()) {
                breedAge += " • ";
            }

            breedAge += age;
        }

        if (!breedAge.isEmpty()) {
            tvBreed.setText(breedAge);
        } else {
            tvBreed.setText("Breed and age not specified");
        }

        // Weight
        if (weight != null && !weight.isEmpty()) {
            tvWeight.setText(weight);
        } else {
            tvWeight.setText("Weight not specified");
        }

        // Gender
        if (gender != null && !gender.isEmpty()) {
            tvGender.setText(gender);
        } else {
            tvGender.setText("Not specified");
        }

        // Notes
        if (notes != null && !notes.isEmpty()) {
            tvNotes.setText(notes);
        } else {
            tvNotes.setText("No notes available");
        }

        // Vaccination status
        if (vaccinationStatus != null
                && !vaccinationStatus.isEmpty()) {

            tvVaccinationStatus.setText(
                    vaccinationStatus
            );

        } else {

            tvVaccinationStatus.setText(
                    "Not available"
            );
        }

        // Last vet check
        if (lastVetCheck != null
                && !lastVetCheck.isEmpty()) {

            tvLastVetCheck.setText(
                    lastVetCheck
            );

        } else {

            tvLastVetCheck.setText(
                    "Not available"
            );
        }

        // Allergies
        if (allergies != null
                && !allergies.isEmpty()) {

            tvAllergies.setText(
                    allergies
            );

        } else {

            tvAllergies.setText(
                    "None"
            );
        }

        // Routine name
        if (routineName != null
                && !routineName.isEmpty()) {

            tvRoutineName.setText(
                    routineName
            );

        } else {

            tvRoutineName.setText(
                    "No routine"
            );
        }

        // Routine time
        if (routineTime != null
                && !routineTime.isEmpty()) {

            tvRoutineTime.setText(
                    routineTime
            );

        } else {

            tvRoutineTime.setText(
                    "Not scheduled"
            );
        }
    }

    private void showDeleteDialog() {

        new AlertDialog.Builder(this)
                .setTitle("Delete Pet")
                .setMessage(
                        "Are you sure you want to delete this pet?"
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

    private void deletePet() {

        FirebaseUser user = mAuth.getCurrentUser();

        if (user == null) {

            Toast.makeText(
                    this,
                    "Please login first",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        String uid = user.getUid();

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

    @Override
    protected void onResume() {
        super.onResume();

        if (petId != null && !petId.isEmpty()) {
            loadPet();
        }
    }
}