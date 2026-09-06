package com.example.pawmate;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class EditPet extends AppCompatActivity {

    private ImageButton btnBack;
    private MaterialButton btnUpdatePet;

    private EditText etPetName;
    private EditText etBreed;
    private EditText etAge;
    private EditText etWeight;
    private EditText etNotes;

    private RadioGroup rgGender;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private String petId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_edit_pet);

        initFirebase();
        initViews();
        getPetId();
        setupListeners();

        if (petId != null && !petId.isEmpty()) {
            btnUpdatePet.setEnabled(false);
            loadPet();
        }
    }

    private void initFirebase() {
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        btnUpdatePet = findViewById(R.id.btnUpdatePet);

        etPetName = findViewById(R.id.etPetName);
        etBreed = findViewById(R.id.etBreed);
        etAge = findViewById(R.id.etAge);
        etWeight = findViewById(R.id.etWeight);
        etNotes = findViewById(R.id.etNotes);

        rgGender = findViewById(R.id.rgGender);
    }

    private void getPetId() {
        petId = getIntent().getStringExtra("petId");

        if (petId == null || petId.trim().isEmpty()) {
            Toast.makeText(
                    this,
                    "Pet information not found",
                    Toast.LENGTH_SHORT
            ).show();

            finish();
        }
    }

    private void setupListeners() {

        btnBack.setOnClickListener(v -> finish());

        btnUpdatePet.setOnClickListener(v -> updatePet());
    }

    private void loadPet() {

        FirebaseUser user = mAuth.getCurrentUser();

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

        db.collection("users")
                .document(uid)
                .collection("pets")
                .document(petId)
                .get()
                .addOnSuccessListener(this::displayPet)
                .addOnFailureListener(this::showLoadError);
    }

    private void displayPet(DocumentSnapshot document) {

        if (!document.exists()) {
            Toast.makeText(
                    this,
                    "Pet not found",
                    Toast.LENGTH_SHORT
            ).show();

            finish();
            return;
        }

        String name = getValue(document, "name");
        String breed = getValue(document, "breed");
        String age = getValue(document, "age");
        String weight = getValue(document, "weight");
        String gender = getValue(document, "gender");
        String notes = getValue(document, "notes");

        etPetName.setText(name);
        etBreed.setText(breed);
        etAge.setText(age);
        etWeight.setText(weight);
        etNotes.setText(notes);

        if ("Male".equalsIgnoreCase(gender)) {
            rgGender.check(R.id.rbMale);

        } else if ("Female".equalsIgnoreCase(gender)) {
            rgGender.check(R.id.rbFemale);
        }

        btnUpdatePet.setEnabled(true);
    }

    private String getValue(
            DocumentSnapshot document,
            String field
    ) {
        String value = document.getString(field);

        return value == null ? "" : value;
    }

    private void updatePet() {

        String name = etPetName
                .getText()
                .toString()
                .trim();

        String breed = etBreed
                .getText()
                .toString()
                .trim();

        String age = etAge
                .getText()
                .toString()
                .trim();

        String weight = etWeight
                .getText()
                .toString()
                .trim();

        String notes = etNotes
                .getText()
                .toString()
                .trim();

        if (!validateFields(
                name,
                breed,
                age,
                weight
        )) {
            return;
        }

        int selectedGender =
                rgGender.getCheckedRadioButtonId();

        if (selectedGender == -1) {
            Toast.makeText(
                    this,
                    "Please select gender",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        String gender = getGender(selectedGender);

        FirebaseUser user = mAuth.getCurrentUser();

        if (user == null) {
            Toast.makeText(
                    this,
                    "Please login again",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        String uid = user.getUid();

        Map<String, Object> updatedPet =
                new HashMap<>();

        updatedPet.put("name", name);
        updatedPet.put("breed", breed);
        updatedPet.put("age", age);
        updatedPet.put("weight", weight);
        updatedPet.put("gender", gender);
        updatedPet.put("notes", notes);

        btnUpdatePet.setEnabled(false);

        db.collection("users")
                .document(uid)
                .collection("pets")
                .document(petId)
                .update(updatedPet)
                .addOnSuccessListener(
                        unused -> showUpdateSuccess()
                )
                .addOnFailureListener(
                        this::showUpdateError
                );
    }

    private boolean validateFields(
            String name,
            String breed,
            String age,
            String weight
    ) {

        if (name.isEmpty()) {
            etPetName.setError("Enter pet name");
            etPetName.requestFocus();
            return false;
        }

        if (breed.isEmpty()) {
            etBreed.setError("Enter breed");
            etBreed.requestFocus();
            return false;
        }

        if (age.isEmpty()) {
            etAge.setError("Enter age");
            etAge.requestFocus();
            return false;
        }

        if (weight.isEmpty()) {
            etWeight.setError("Enter weight");
            etWeight.requestFocus();
            return false;
        }

        return true;
    }

    private String getGender(int selectedId) {

        if (selectedId == R.id.rbMale) {
            return "Male";
        }

        return "Female";
    }

    private void showUpdateSuccess() {

        Toast.makeText(
                this,
                "Pet updated successfully 🐾",
                Toast.LENGTH_SHORT
        ).show();

        finish();
    }

    private void showUpdateError(Exception e) {

        btnUpdatePet.setEnabled(true);

        Toast.makeText(
                this,
                "Failed to update pet",
                Toast.LENGTH_SHORT
        ).show();
    }

    private void showLoadError(Exception e) {

        Toast.makeText(
                this,
                "Failed to load pet",
                Toast.LENGTH_SHORT
        ).show();

        finish();
    }
}