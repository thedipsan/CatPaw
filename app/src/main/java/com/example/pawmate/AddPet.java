package com.example.pawmate;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AddPet extends AppCompatActivity {

    ImageButton btnBack;
    MaterialButton btnSelectImage;
    MaterialButton btnSavePet;


    EditText etPetName;
    EditText etBreed;
    EditText etAge;
    EditText  etWeight;
    EditText  etNotes;

    RadioGroup rgGender;

    // Firebase
    FirebaseAuth mAuth;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_pet);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        db= FirebaseFirestore.getInstance();

        // Connect to the Xml
        btnBack = findViewById(R.id.btnBack);
        btnSelectImage = findViewById(R.id.btnSelectImage);
        btnSavePet  = findViewById(R.id.btnSavePet);

        etPetName= findViewById(R.id.etPetName);
        etBreed = findViewById(R.id.etBreed);
        etAge= findViewById(R.id.etAge);
        etWeight= findViewById(R.id.etWeight);
        etNotes = findViewById(R.id.etNotes);

        rgGender= findViewById(R.id.rgGender);



        // Back button function
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               finish();
            }
        });


        // Select Image
        btnSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(AddPet.this, "Photo selection will be added next", Toast.LENGTH_SHORT).show();
            }
        });


        // Save Pet
        btnSavePet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                savePet();
            }
        });
    }


    // Save Pet to Firestore
    private void savePet() {

        String petName = etPetName.getText().toString().trim();
        String breed = etBreed.getText().toString().trim();
        String age = etAge.getText().toString().trim();
        String weight = etWeight.getText().toString().trim();
        String notes = etNotes.getText().toString().trim();

        // Validation
        if (petName.isEmpty()) {
            etPetName.setError("Enter your pet name");
            etPetName.requestFocus();
            return;
        }

        if (breed.isEmpty()) {
            etBreed.setError("Enter breed");
            etBreed.requestFocus();
            return;
        }

        if (age.isEmpty()) {
            etAge.setError("Enter age");
            etAge.requestFocus();
            return;
        }

        if (weight.isEmpty()) {
            etWeight.setError("Enter weight");
            etWeight.requestFocus();
            return;
        }

        // Validate Gender
        int selectedGender = rgGender.getCheckedRadioButtonId();

        if (selectedGender == -1) {
            Toast.makeText(this, "Please select gender", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get Gender
        String gender;

        if (selectedGender == R.id.rbMale) {
            gender = "Male";
        } else {
            gender = "Female";
        }

        // Check logged-in user
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            Toast.makeText(this, "Please login again", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get Firebase UID
        String uid = currentUser.getUid();

        // Create Pet data
        Map<String, Object> pet = new HashMap<>();

        pet.put("name", petName);
        pet.put("breed", breed);
        pet.put("age", age);
        pet.put("weight", weight);
        pet.put("gender", gender);
        pet.put("notes", notes);
        pet.put("ownerId", uid);
        pet.put("createdAt", System.currentTimeMillis());

        // Save Pet to Firestore
        db.collection("users")
                .document(uid)
                .collection("pets")
                .add(pet)
                .addOnSuccessListener(documentReference -> {

                    Toast.makeText(
                            AddPet.this,
                            "Pet saved successfully!",
                            Toast.LENGTH_SHORT
                    ).show();

                    // Optional: close this Activity
                    finish();
                })
                .addOnFailureListener(e -> {

                    Toast.makeText(
                            AddPet.this,
                            "Failed to save pet: " + e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();
                });
    }
}