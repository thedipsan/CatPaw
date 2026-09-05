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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class MyPet extends AppCompatActivity {

    private ImageButton btnBack;
    private ImageButton btnFilterPets;
    private com.google.android.material.floatingactionbutton.FloatingActionButton btnAddPet;

    private EditText etSearchPets;

    private LinearLayout petContainer;

    private TextView tvEmpty;
    private TextView tvPetCount;

    // FIREBASE

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    // LOCAL PET LIST

    private final List<QueryDocumentSnapshot> allPets =
            new ArrayList<>();


    // ON CREATE

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_my_pet);

        // INITIALIZE FIREBASE

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // FIND VIEWS

        btnBack = findViewById(R.id.btnBack);
        btnFilterPets = findViewById(R.id.btnFilterPets);
        btnAddPet = findViewById(R.id.btnAddPet);

        etSearchPets = findViewById(R.id.etSearchPets);

        petContainer = findViewById(R.id.petContainer);

        tvEmpty = findViewById(R.id.tvEmpty);
        tvPetCount = findViewById(R.id.tvPetCount);

        // BACK BUTTON

        btnBack.setOnClickListener(v -> {

            finish();

        });

        // ADD PET BUTTON

        btnAddPet.setOnClickListener(v -> {

            Intent intent =
                    new Intent(MyPet.this, AddPet.class);

            startActivity(intent);

        });

        // SEARCH

        etSearchPets.addTextChangedListener(
                new TextWatcher() {

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

                        filterPets(
                                s.toString()
                        );

                    }

                    @Override
                    public void afterTextChanged(
                            Editable s
                    ) {
                    }
                }
        );

        // FILTER BUTTON

        btnFilterPets.setOnClickListener(v -> {

            Toast.makeText(
                    MyPet.this,
                    "You can search by pet name or breed.",
                    Toast.LENGTH_SHORT
            ).show();

        });
    }


    // RELOAD WHEN SCREEN RETURNS

    @Override
    protected void onResume() {

        super.onResume();

        loadPets();

    }

    // LOAD PETS FROM FIRESTORE
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


    // DISPLAY PETS

    private void displayPets(
            List<QueryDocumentSnapshot> pets
    ) {

        // Remove old cards
        petContainer.removeAllViews();

        // NO PETS

        if (pets.isEmpty()) {

            tvEmpty.setText(
                    "No pets found.\n"
                            + "Tap + to add your first pet 🐾"
            );

            tvEmpty.setVisibility(
                    View.VISIBLE
            );

            return;
        }

        // PETS EXIST

        tvEmpty.setVisibility(
                View.GONE
        );

        // CREATE CARDS

        for (
                QueryDocumentSnapshot document
                : pets
        ) {

            addPetCard(document);

        }
    }


    // SEARCH PETS

    private void filterPets(
            String searchText
    ) {

        String query =
                searchText
                        .trim()
                        .toLowerCase();

        // EMPTY SEARCH

        if (query.isEmpty()) {

            updatePetCount(
                    allPets.size()
            );

            displayPets(
                    allPets
            );

            return;
        }

        // ==========================================
        // FILTERED LIST
        // ==========================================

        List<QueryDocumentSnapshot> filteredPets =
                new ArrayList<>();

        for (
                QueryDocumentSnapshot document
                : allPets
        ) {

            String name =
                    document.getString("name");

            String breed =
                    document.getString("breed");

            // Safe values
            if (name == null) {
                name = "";
            }

            if (breed == null) {
                breed = "";
            }

            // Convert lowercase
            name =
                    name.toLowerCase();

            breed =
                    breed.toLowerCase();

            // Search name OR breed
            if (
                    name.contains(query)
                            ||
                            breed.contains(query)
            ) {

                filteredPets.add(
                        document
                );

            }
        }

        // Update filtered count
        updatePetCount(
                filteredPets.size()
        );

        // Display filtered pets
        displayPets(
                filteredPets
        );
    }


    // ==========================================
    // CREATE PET CARD
    // ==========================================

    private void addPetCard(
            QueryDocumentSnapshot document
    ) {

        // ==========================================
        // INFLATE ITEM LAYOUT
        // ==========================================

        View petView =
                LayoutInflater
                        .from(this)
                        .inflate(
                                R.layout.item_pet,
                                petContainer,
                                false
                        );

        // ==========================================
        // FIND CARD VIEWS
        // ==========================================

        TextView tvPetName =
                petView.findViewById(
                        R.id.tvPetName
                );

        TextView tvBreed =
                petView.findViewById(
                        R.id.tvBreed
                );

        TextView tvDetails =
                petView.findViewById(
                        R.id.tvDetails
                );

        // ==========================================
        // GET FIRESTORE DATA
        // ==========================================

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

        // ==========================================
        // DEFAULT VALUES
        // ==========================================

        if (
                name == null
                        ||
                        name.isEmpty()
        ) {

            name = "Unknown Pet";

        }

        if (
                breed == null
                        ||
                        breed.isEmpty()
        ) {

            breed = "Unknown Breed";

        }

        if (
                age == null
                        ||
                        age.isEmpty()
        ) {

            age = "N/A";

        }

        if (
                weight == null
                        ||
                        weight.isEmpty()
        ) {

            weight = "N/A";

        }

        if (
                gender == null
                        ||
                        gender.isEmpty()
        ) {

            gender = "N/A";

        }

        // ==========================================
        // DISPLAY
        // ==========================================

        tvPetName.setText(
                name
        );

        tvBreed.setText(
                breed
        );

        tvDetails.setText(
                age
                        + " years • "
                        + weight
                        + " kg • "
                        + gender
        );

        // ADD CARD

        petContainer.addView(
                petView
        );
    }

    // UPDATE PET COUNT

    private void updatePetCount(
            int count
    ) {
        if (count == 1) {
            tvPetCount.setText("1 pet");

        } else {
            tvPetCount.setText(count + " pets");

        }
    }
}

