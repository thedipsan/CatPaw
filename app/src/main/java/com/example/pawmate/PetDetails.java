package com.example.pawmate;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class PetDetails extends AppCompatActivity {

    private ImageButton btnBack;

    private TextView tvPetName;
    private TextView tvBreed;
    private TextView tvAge;
    private TextView tvWeight;
    private TextView tvGender;
    private TextView tvNotes;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    // Firebase document ID
    private String petId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_pet_details);


        // FIREBASE

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();


        // FIND VIEWS

        btnBack = findViewById(R.id.btnBack);

        tvPetName = findViewById(R.id.tvPetName);
        tvBreed = findViewById(R.id.tvBreed);
        tvAge = findViewById(R.id.tvAge);
        tvWeight = findViewById(R.id.tvWeight);
        tvGender = findViewById(R.id.tvGender);
        tvNotes = findViewById(R.id.tvNotes);


        // GET PET ID

        petId = getIntent().getStringExtra("petId");


        // CHECK PET ID

        if (petId == null || petId.isEmpty()) {

            Toast.makeText(
                    PetDetails.this,
                    "Pet information not found",
                    Toast.LENGTH_SHORT
            ).show();

            finish();

            return;
        }


        // BACK
        btnBack.setOnClickListener(v -> {

            finish();

        });


        // LOAD PET

        loadPet();
    }


    // LOAD PET FROM FIRESTORE

    private void loadPet() {

        FirebaseUser currentUser =
                mAuth.getCurrentUser();


        if (currentUser == null) {

            Toast.makeText(
                    PetDetails.this,
                    "Please login again",
                    Toast.LENGTH_SHORT
            ).show();

            finish();

            return;
        }


        String uid = currentUser.getUid();


        db.collection("users")
                .document(uid)
                .collection("pets")
                .document(petId)
                .get()

                .addOnSuccessListener(documentSnapshot -> {

                    if (!documentSnapshot.exists()) {

                        Toast.makeText(
                                PetDetails.this,
                                "Pet not found",
                                Toast.LENGTH_SHORT
                        ).show();

                        finish();

                        return;
                    }


                    // GET DATA

                    String name =
                            documentSnapshot.getString("name");

                    String breed =
                            documentSnapshot.getString("breed");

                    String age =
                            documentSnapshot.getString("age");

                    String weight =
                            documentSnapshot.getString("weight");

                    String gender =
                            documentSnapshot.getString("gender");

                    String notes =
                            documentSnapshot.getString("notes");


                    // SET DEFAULT VALUES

                    if (name == null) {
                        name = "Unknown Pet";
                    }

                    if (breed == null) {
                        breed = "Unknown Breed";
                    }

                    if (age == null) {
                        age = "N/A";
                    }

                    if (weight == null) {
                        weight = "N/A";
                    }

                    if (gender == null) {
                        gender = "N/A";
                    }

                    if (notes == null || notes.isEmpty()) {
                        notes = "No additional notes.";
                    }


                    // DISPLAY DATA
                    tvPetName.setText(name);

                    tvBreed.setText(breed);

                    tvAge.setText(age + " years");

                    tvWeight.setText(weight + " kg");

                    tvGender.setText(gender);

                    tvNotes.setText(notes);

                })

                .addOnFailureListener(e -> {

                    Toast.makeText(
                            PetDetails.this,
                            "Failed to load pet",
                            Toast.LENGTH_SHORT
                    ).show();

                });
    }

}