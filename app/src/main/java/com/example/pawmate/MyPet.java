        package com.example.pawmate;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class MyPet extends AppCompatActivity {

    // UI
    private ImageButton btnBack;
    private ImageButton btnFilterPets;
    private FloatingActionButton btnAddPet;

    private EditText etSearchPets;
    private LinearLayout petContainer;
    private TextView tvEmpty;
    private TextView tvPetCount;

    // Firebase
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    // All pets
    private final List<QueryDocumentSnapshot> allPets =
            new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_pet);

        initFirebase();
        initViews();
        setupListeners();
    }

    // --------------------------------------------------
    // INITIALIZATION
    // --------------------------------------------------

    private void initFirebase() {
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        btnFilterPets = findViewById(R.id.btnFilterPets);
        btnAddPet = findViewById(R.id.btnAddPet);

        etSearchPets = findViewById(R.id.etSearchPets);
        petContainer = findViewById(R.id.petContainer);

        tvEmpty = findViewById(R.id.tvEmpty);
        tvPetCount = findViewById(R.id.tvPetCount);
    }

    private void setupListeners() {

        // Back
        btnBack.setOnClickListener(v -> finish());

        // Add pet
        btnAddPet.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddPet.class);
            startActivity(intent);
        });

        // Filter information
        btnFilterPets.setOnClickListener(v ->
                Toast.makeText(
                        this,
                        "You can search by pet name or breed.",
                        Toast.LENGTH_SHORT
                ).show()
        );

        // Search
        etSearchPets.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(
                    CharSequence s,
                    int start,
                    int count,
                    int after
            ) {
            }

            @Override
            public void onTextChanged(
                    CharSequence s,
                    int start,
                    int before,
                    int count
            ) {
                filterPets(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    // --------------------------------------------------
    // LIFECYCLE
    // --------------------------------------------------

    @Override
    protected void onResume() {
        super.onResume();
        loadPets();
    }

    // --------------------------------------------------
    // LOAD PETS
    // --------------------------------------------------

    private void loadPets() {

        FirebaseUser currentUser =
                mAuth.getCurrentUser();

        // CHECK USER LOGIN

        if (currentUser == null) {

            petContainer.removeAllViews();

            tvPetCount.setText("0 pets");

            tvEmpty.setText(
                    "Please login again."
            );

            tvEmpty.setVisibility(View.VISIBLE);

            return;
        }

        // GET UID

        String uid =
                currentUser.getUid();

        // CLEAR OLD CARDS

        petContainer.removeAllViews();

        tvEmpty.setVisibility(View.GONE);

        // LOAD PETS

        db.collection("users")
                .document(uid)
                .collection("pets")
                .get()

                .addOnSuccessListener(
                        queryDocumentSnapshots -> {

                            // Clear old list
                            allPets.clear();

                            // Store pets
                            for (
                                    QueryDocumentSnapshot document
                                    : queryDocumentSnapshots
                            ) {

                                allPets.add(document);

                            }

                            // Update count
                            updatePetCount(
                                    allPets.size()
                            );

                            // Display
                            displayPets(
                                    allPets
                            );

                        }
                )

                .addOnFailureListener(e -> {

                    petContainer.removeAllViews();

                    tvPetCount.setText("0 pets");

                    tvEmpty.setText(
                            "Failed to load pets.\n"
                                    + "Please try again."
                    );

                    tvEmpty.setVisibility(
                            View.VISIBLE
                    );

                    Toast.makeText(
                            MyPet.this,
                            "Firestore error: "
                                    + e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();

                });
    }

    private void showLoginMessage() {

        petContainer.removeAllViews();
        tvPetCount.setText("0 pets");

        tvEmpty.setText("Please login again.");
        tvEmpty.setVisibility(View.VISIBLE);
    }

    private void showLoadError(Exception e) {

        petContainer.removeAllViews();
        tvPetCount.setText("0 pets");

        tvEmpty.setText(
                "Failed to load pets.\nPlease try again."
        );

        tvEmpty.setVisibility(View.VISIBLE);

        Toast.makeText(
                this,
                "Firestore error: " + e.getMessage(),
                Toast.LENGTH_LONG
        ).show();
    }

    // --------------------------------------------------
    // DISPLAY PETS
    // --------------------------------------------------

    private void displayPets(
            List<QueryDocumentSnapshot> pets
    ) {

        petContainer.removeAllViews();

        if (pets.isEmpty()) {
            showEmptyPets();
            return;
        }

        tvEmpty.setVisibility(View.GONE);

        for (QueryDocumentSnapshot document : pets) {
            addPetCard(document);
        }
    }

    private void showEmptyPets() {

        tvEmpty.setText(
                "No pets found.\n"
                        + "Tap + to add your first pet 🐾"
        );

        tvEmpty.setVisibility(View.VISIBLE);
    }

    // --------------------------------------------------
    // SEARCH PETS
    // --------------------------------------------------

    private void filterPets(String searchText) {

        String query = searchText
                .trim()
                .toLowerCase();

        if (query.isEmpty()) {
            updatePetCount(allPets.size());
            displayPets(allPets);
            return;
        }

        List<QueryDocumentSnapshot> filteredPets =
                new ArrayList<>();

        for (QueryDocumentSnapshot document : allPets) {

            String name = getValue(document, "name");
            String breed = getValue(document, "breed");

            if (name.toLowerCase().contains(query)
                    || breed.toLowerCase().contains(query)) {

                filteredPets.add(document);
            }
        }

        updatePetCount(filteredPets.size());
        displayPets(filteredPets);
    }

    // --------------------------------------------------
    // CREATE PET CARD
    // --------------------------------------------------

    private void addPetCard(
            QueryDocumentSnapshot document
    ) {

        View petView = LayoutInflater
                .from(this)
                .inflate(
                        R.layout.item_pet,
                        petContainer,
                        false
                );

        TextView tvPetName =
                petView.findViewById(R.id.tvPetName);

        TextView tvBreed =
                petView.findViewById(R.id.tvBreed);

        TextView tvDetails =
                petView.findViewById(R.id.tvDetails);

        // Get data
        String name = getValue(document, "name");
        String breed = getValue(document, "breed");
        String age = getValue(document, "age");
        String weight = getValue(document, "weight");
        String gender = getValue(document, "gender");

        // Default values
        name = defaultValue(name, "Unknown Pet");
        breed = defaultValue(breed, "Unknown Breed");
        age = defaultValue(age, "N/A");
        weight = defaultValue(weight, "N/A");
        gender = defaultValue(gender, "N/A");

        // Display data
        tvPetName.setText(name);
        tvBreed.setText(breed);

        tvDetails.setText(
                age + " years • "
                        + weight + " kg • "
                        + gender
        );

        // --------------------------------------------------
        // PET CARD CLICK
        // --------------------------------------------------

        String petId = document.getId();

        petView.setOnClickListener(v -> {

            Intent intent = new Intent(
                    MyPet.this,
                    PetDetails.class
            );

            intent.putExtra("petId", petId);
            startActivity(intent);
        });

        // Add card
        petContainer.addView(petView);
    }

    // --------------------------------------------------
    // HELPERS
    // --------------------------------------------------

    private String getValue(
            QueryDocumentSnapshot document,
            String field
    ) {

        String value = document.getString(field);

        return value == null ? "" : value;
    }

    private String defaultValue(
            String value,
            String fallback
    ) {

        if (value == null || value.trim().isEmpty()) {
            return fallback;
        }

        return value;
    }

    // --------------------------------------------------
    // PET COUNT
    // --------------------------------------------------

    private void updatePetCount(int count) {

        if (count == 1) {
            tvPetCount.setText("1 pet");
        } else {
            tvPetCount.setText(count + " pets");
        }
    }
}
