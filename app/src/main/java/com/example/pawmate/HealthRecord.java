package com.example.pawmate;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class HealthRecord extends AppCompatActivity {

    private TextView tvPetName;

    private MaterialCardView cardVaccination;
    private MaterialCardView cardVetVisits;
    private MaterialCardView cardMedications;
    private MaterialCardView cardHealthHistory;

    private FirebaseFirestore firestore;

    private String petId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_health_record);

        firestore = FirebaseFirestore.getInstance();

        // Get pet ID from PetDetails
        petId = getIntent().getStringExtra("petId");

        tvPetName = findViewById(R.id.tvPetName);

        cardVaccination = findViewById(R.id.cardVaccination);
        cardVetVisits = findViewById(R.id.cardVetVisits);
        cardMedications = findViewById(R.id.cardMedications);
        cardHealthHistory = findViewById(R.id.cardHealthHistory);


        if (petId == null || petId.isEmpty()) {

            Toast.makeText(
                    this,
                    "Pet information not found",
                    Toast.LENGTH_SHORT
            ).show();

            finish();

            return;
        }


        loadPetName();


        // Vaccination
        cardVaccination.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            HealthRecord.this,
                            Vaccinations.class
                    );

            intent.putExtra("petId", petId);

            startActivity(intent);
        });


        // Vet Visits
        cardVetVisits.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            HealthRecord.this,
                            VetVisit.class
                    );

            intent.putExtra("petId", petId);

            startActivity(intent);
        });


        // Medications
        cardMedications.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            HealthRecord.this,
                            Medication.class
                    );

            intent.putExtra("petId", petId);

            startActivity(intent);
        });


        // Health History
        cardHealthHistory.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            HealthRecord.this,
                            HealthHistory.class
                    );

            intent.putExtra("petId", petId);

            startActivity(intent);
        });
    }


    private void loadPetName() {

        FirebaseUser user =
                FirebaseAuth.getInstance().getCurrentUser();

        if (user == null) {
            return;
        }

        String uid = user.getUid();

        firestore
                .collection("users")
                .document(uid)
                .collection("pets")
                .document(petId)
                .get()

                .addOnSuccessListener(document -> {

                    if (document.exists()) {

                        String name =
                                document.getString("name");

                        if (name != null) {

                            tvPetName.setText(
                                    name + "'s Health"
                            );
                        }
                    }
                });
    }
}