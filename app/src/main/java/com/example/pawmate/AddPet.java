package com.example.pawmate;

import android.net.Uri;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AddPet extends AppCompatActivity {

    // Declare the views
    private ImageButton btnBack;
    private MaterialButton btnSelectImage;
    private MaterialButton btnSavePet;
    private ImageView imgPet;

    private EditText etPetName;
    private EditText etBreed;
    private EditText etAge;
    private EditText etWeight;
    private EditText etNotes;

    private RadioGroup rgGender;

    // Declare Firebase
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    // Store the selected image
    private Uri selectedImageUri;

    // Create the gallery picker
    private ActivityResultLauncher<String> imagePickerLauncher;

    // Cloudinary upload preset
    private static final String CLOUDINARY_PRESET = "petcare_pet_images";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Connect Java with the Add Pet XML layout
        setContentView(R.layout.activity_add_pet);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Connect Java variables with XML views
        btnBack = findViewById(R.id.btnBack);
        btnSelectImage = findViewById(R.id.btnSelectImage);
        btnSavePet = findViewById(R.id.btnSavePet);
        imgPet = findViewById(R.id.imgPet);

        etPetName = findViewById(R.id.etPetName);
        etBreed = findViewById(R.id.etBreed);
        etAge = findViewById(R.id.etAge);
        etWeight = findViewById(R.id.etWeight);
        etNotes = findViewById(R.id.etNotes);

        rgGender = findViewById(R.id.rgGender);

        // Create the gallery picker
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        selectedImageUri = uri;
                        imgPet.setImageURI(uri);
                    }
                }
        );

        // Close the Add Pet screen
        btnBack.setOnClickListener(v -> {
            finish();
        });

        // Open the gallery
        btnSelectImage.setOnClickListener(v -> {
            imagePickerLauncher.launch("image/*");
        });

        // Save the pet
        btnSavePet.setOnClickListener(v -> {
            savePet();
        });
    }

    private void savePet() {

        // Get information from the input fields
        String petName = etPetName.getText().toString().trim();
        String breed = etBreed.getText().toString().trim();
        String age = etAge.getText().toString().trim();
        String weight = etWeight.getText().toString().trim();
        String notes = etNotes.getText().toString().trim();

        // Check pet name
        if (petName.isEmpty()) {
            etPetName.setError("Please enter pet name");
            etPetName.requestFocus();
            return;
        }

        // Check breed
        if (breed.isEmpty()) {
            etBreed.setError("Please enter breed");
            etBreed.requestFocus();
            return;
        }

        // Check age
        if (age.isEmpty()) {
            etAge.setError("Please enter age");
            etAge.requestFocus();
            return;
        }

        // Check weight
        if (weight.isEmpty()) {
            etWeight.setError("Please enter weight");
            etWeight.requestFocus();
            return;
        }

        // Check gender
        int selectedGenderId = rgGender.getCheckedRadioButtonId();

        if (selectedGenderId == -1) {
            Toast.makeText(
                    this,
                    "Please select gender",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        String gender;

        if (selectedGenderId == R.id.rbMale) {
            gender = "Male";
        } else {
            gender = "Female";
        }

        // Check image
        if (selectedImageUri == null) {
            Toast.makeText(
                    this,
                    "Please select a pet photo",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        // Get the currently logged-in user
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            Toast.makeText(
                    this,
                    "Please login first",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        // Get the user's Firebase ID
        String uid = currentUser.getUid();

        // Disable the save button during upload
        btnSavePet.setEnabled(false);
        btnSavePet.setText("Uploading...");

        // Upload the image to Cloudinary
        uploadPetImage(
                selectedImageUri,
                petName,
                breed,
                age,
                weight,
                gender,
                notes,
                uid
        );
    }

    private void uploadPetImage(
            Uri imageUri,
            String petName,
            String breed,
            String age,
            String weight,
            String gender,
            String notes,
            String uid
    ) {

        // Upload image to Cloudinary
        MediaManager.get()
                .upload(imageUri)
                .unsigned(CLOUDINARY_PRESET)
                .callback(new UploadCallback() {

                    @Override
                    public void onStart(String requestId) {
                        btnSavePet.setText("Uploading...");
                    }

                    @Override
                    public void onProgress(
                            String requestId,
                            long bytes,
                            long totalBytes
                    ) {

                        // Calculate upload progress
                        int progress = (int) ((bytes * 100) / totalBytes);

                        btnSavePet.setText(
                                "Uploading " + progress + "%"
                        );
                    }

                    @Override
                    public void onSuccess(
                            String requestId,
                            Map resultData
                    ) {

                        // Get the Cloudinary image URL
                        String imageUrl =
                                String.valueOf(resultData.get("secure_url"));

                        // Save pet information to Firestore
                        savePetToFirestore(
                                uid,
                                petName,
                                breed,
                                age,
                                weight,
                                gender,
                                notes,
                                imageUrl
                        );
                    }

                    @Override
                    public void onError(
                            String requestId,
                            ErrorInfo error
                    ) {

                        // Enable the save button again
                        btnSavePet.setEnabled(true);
                        btnSavePet.setText("Save Pet  🐾");

                        Toast.makeText(
                                AddPet.this,
                                "Image upload failed: "
                                        + error.getDescription(),
                                Toast.LENGTH_LONG
                        ).show();
                    }

                    @Override
                    public void onReschedule(
                            String requestId,
                            ErrorInfo error
                    ) {

                        Toast.makeText(
                                AddPet.this,
                                "Upload rescheduled. Please wait...",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                })
                .dispatch();
    }

    private void savePetToFirestore(
            String uid,
            String petName,
            String breed,
            String age,
            String weight,
            String gender,
            String notes,
            String imageUrl
    ) {

        // Create pet data
        Map<String, Object> pet = new HashMap<>();

        pet.put("name", petName);
        pet.put("breed", breed);
        pet.put("age", age);
        pet.put("weight", weight);
        pet.put("gender", gender);
        pet.put("notes", notes);
        pet.put("imageUrl", imageUrl);
        pet.put("ownerId", uid);
        pet.put("createdAt", System.currentTimeMillis());

        // Save pet data in Firestore
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

                    // Close Add Pet screen
                    finish();
                })
                .addOnFailureListener(e -> {

                    // Enable the save button again
                    btnSavePet.setEnabled(true);
                    btnSavePet.setText("Save Pet  🐾");

                    Toast.makeText(
                            AddPet.this,
                            "Failed to save pet: " + e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();
                });
    }
}