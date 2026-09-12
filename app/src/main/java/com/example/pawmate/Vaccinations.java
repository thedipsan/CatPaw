package com.example.pawmate;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class Vaccinations extends AppCompatActivity {

    private TextView tvPetName;
    private TextView tvEmpty;

    private MaterialButton btnAddVaccination;

    private RecyclerView recyclerVaccinations;

    private FirebaseFirestore firestore;

    private ArrayList<Vaccination> vaccinationList;

    private VaccinationAdapter adapter;

    private String petId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(
                R.layout.activity_vaccinations
        );


        firestore =
                FirebaseFirestore.getInstance();


        petId =
                getIntent()
                        .getStringExtra("petId");


        tvPetName =
                findViewById(R.id.tvPetName);


        tvEmpty =
                findViewById(R.id.tvEmpty);


        btnAddVaccination =
                findViewById(
                        R.id.btnAddVaccination
                );


        recyclerVaccinations =
                findViewById(
                        R.id.recyclerVaccinations
                );


        if (petId == null || petId.isEmpty()) {

            Toast.makeText(
                    this,
                    "Pet information not found",
                    Toast.LENGTH_SHORT
            ).show();

            finish();

            return;
        }


        vaccinationList =
                new ArrayList<>();


        adapter =
                new VaccinationAdapter(
                        vaccinationList,
                        new VaccinationAdapter
                                .OnVaccinationActionListener() {

                            @Override
                            public void onEdit(
                                    Vaccination vaccination) {

                                editVaccination(
                                        vaccination
                                );
                            }


                            @Override
                            public void onDelete(
                                    Vaccination vaccination) {

                                deleteVaccination(
                                        vaccination
                                );
                            }
                        }
                );


        recyclerVaccinations.setLayoutManager(
                new LinearLayoutManager(this)
        );


        recyclerVaccinations.setAdapter(
                adapter
        );


        loadPetName();

        loadVaccinations();


        btnAddVaccination.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            Vaccinations.this,
                            AddVaccination.class
                    );

            intent.putExtra(
                    "petId",
                    petId
            );

            startActivity(intent);
        });
    }


    private void loadPetName() {

        FirebaseUser user =
                FirebaseAuth
                        .getInstance()
                        .getCurrentUser();


        if (user == null) {
            return;
        }


        firestore
                .collection("users")
                .document(user.getUid())
                .collection("pets")
                .document(petId)
                .get()

                .addOnSuccessListener(
                        document -> {

                            if (document.exists()) {

                                String name =
                                        document.getString(
                                                "name"
                                        );

                                if (name != null) {

                                    tvPetName.setText(
                                            name +
                                                    "'s Vaccinations"
                                    );
                                }
                            }
                        }
                );
    }


    private void loadVaccinations() {

        FirebaseUser user =
                FirebaseAuth
                        .getInstance()
                        .getCurrentUser();


        if (user == null) {
            return;
        }


        firestore
                .collection("users")
                .document(user.getUid())
                .collection("pets")
                .document(petId)
                .collection("vaccinations")
                .get()

                .addOnSuccessListener(
                        queryDocumentSnapshots -> {

                            vaccinationList.clear();


                            for (
                                    com.google.firebase
                                            .firestore
                                            .DocumentSnapshot document :
                                    queryDocumentSnapshots
                            ) {

                                Vaccination vaccination =
                                        document.toObject(
                                                Vaccination.class
                                        );


                                if (vaccination != null) {

                                    vaccination.setId(
                                            document.getId()
                                    );

                                    vaccinationList.add(
                                            vaccination
                                    );
                                }
                            }


                            adapter.notifyDataSetChanged();


                            if (vaccinationList.isEmpty()) {

                                tvEmpty.setVisibility(
                                        TextView.VISIBLE
                                );

                            } else {

                                tvEmpty.setVisibility(
                                        TextView.GONE
                                );
                            }
                        })

                .addOnFailureListener(e -> {

                    Toast.makeText(
                            this,
                            "Failed to load vaccinations",
                            Toast.LENGTH_SHORT
                    ).show();
                });
    }


    // -------------------------
    // EDIT
    // -------------------------

    private void editVaccination(
            Vaccination vaccination) {

        Intent intent =
                new Intent(
                        Vaccinations.this,
                        AddVaccination.class
                );


        intent.putExtra(
                "petId",
                petId
        );


        intent.putExtra(
                "vaccinationId",
                vaccination.getId()
        );


        intent.putExtra(
                "name",
                vaccination.getName()
        );


        intent.putExtra(
                "date",
                vaccination.getDate()
        );


        intent.putExtra(
                "nextDate",
                vaccination.getNextDate()
        );


        intent.putExtra(
                "notes",
                vaccination.getNotes()
        );


        startActivity(intent);
    }


    // -------------------------
    // DELETE
    // -------------------------

    private void deleteVaccination(
            Vaccination vaccination) {

        new AlertDialog.Builder(this)

                .setTitle("Delete Vaccination")

                .setMessage(
                        "Are you sure you want to delete " +
                                vaccination.getName() +
                                "?"
                )

                .setNegativeButton(
                        "Cancel",
                        null
                )

                .setPositiveButton(
                        "Delete",
                        (dialog, which) ->
                                performDelete(
                                        vaccination
                                )
                )

                .show();
    }


    private void performDelete(
            Vaccination vaccination) {

        FirebaseUser user =
                FirebaseAuth
                        .getInstance()
                        .getCurrentUser();


        if (user == null) {
            return;
        }


        firestore
                .collection("users")
                .document(user.getUid())
                .collection("pets")
                .document(petId)
                .collection("vaccinations")
                .document(vaccination.getId())
                .delete()

                .addOnSuccessListener(unused -> {

                    Toast.makeText(
                            this,
                            "Vaccination deleted",
                            Toast.LENGTH_SHORT
                    ).show();

                    loadVaccinations();
                })

                .addOnFailureListener(e -> {

                    Toast.makeText(
                            this,
                            "Failed to delete vaccination",
                            Toast.LENGTH_SHORT
                    ).show();
                });
    }


    @Override
    protected void onResume() {

        super.onResume();

        if (petId != null) {

            loadVaccinations();
        }
    }
}