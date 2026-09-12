package com.example.pawmate;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
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

public class MedicalRecords extends AppCompatActivity {

    private TextView tvPetName;
    private TextView tvEmpty;

    private ImageButton btnBack;
    private MaterialButton btnAddMedicalRecord;

    private RecyclerView recyclerMedicalRecords;

    private FirebaseFirestore firestore;

    private ArrayList<MedicalRecord> recordList;

    private MedicalRecordAdapter adapter;

    private String petId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_medical_records);


        // Firebase
        firestore = FirebaseFirestore.getInstance();


        // Get Pet ID
        petId = getIntent().getStringExtra("petId");


        // Find Views
        tvPetName =
                findViewById(R.id.tvPetName);

        tvEmpty =
                findViewById(R.id.tvEmpty);

        btnBack =
                findViewById(R.id.btnBack);

        btnAddMedicalRecord =
                findViewById(R.id.btnAddMedicalRecord);

        recyclerMedicalRecords =
                findViewById(R.id.recyclerMedicalRecords);


        // Check Pet ID
        if (petId == null || petId.isEmpty()) {

            Toast.makeText(
                    this,
                    "Pet information not found",
                    Toast.LENGTH_SHORT
            ).show();

            finish();

            return;
        }


        // List
        recordList = new ArrayList<>();


        // Adapter
        adapter = new MedicalRecordAdapter(
                recordList,
                new MedicalRecordAdapter
                        .OnMedicalRecordActionListener() {

                    @Override
                    public void onEdit(
                            MedicalRecord record) {

                        editMedicalRecord(record);
                    }


                    @Override
                    public void onDelete(
                            MedicalRecord record) {

                        deleteMedicalRecord(record);
                    }
                }
        );


        // RecyclerView
        recyclerMedicalRecords.setLayoutManager(
                new LinearLayoutManager(this)
        );

        recyclerMedicalRecords.setAdapter(adapter);


        // Back button
        btnBack.setOnClickListener(v -> {

            finish();
        });


        // Add Medical Record
        btnAddMedicalRecord.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            MedicalRecords.this,
                            AddMedicalRecord.class
                    );

            intent.putExtra(
                    "petId",
                    petId
            );

            startActivity(intent);
        });


        // Load data
        loadPetName();

        loadMedicalRecords();
    }


    // ==========================================
    // LOAD PET NAME
    // ==========================================

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

                .addOnSuccessListener(document -> {

                    if (document.exists()) {

                        String name =
                                document.getString("name");


                        if (name != null) {

                            tvPetName.setText(
                                    name +
                                            "'s Medical Records"
                            );
                        }
                    }
                });
    }


    // ==========================================
    // LOAD MEDICAL RECORDS
    // ==========================================

    private void loadMedicalRecords() {

        FirebaseUser user =
                FirebaseAuth
                        .getInstance()
                        .getCurrentUser();


        if (user == null) {

            Toast.makeText(
                    this,
                    "Please login again",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }


        firestore
                .collection("users")
                .document(user.getUid())
                .collection("pets")
                .document(petId)
                .collection("medicalRecords")
                .get()

                .addOnSuccessListener(
                        queryDocumentSnapshots -> {

                            recordList.clear();


                            for (
                                    com.google.firebase
                                            .firestore
                                            .DocumentSnapshot document
                                    : queryDocumentSnapshots
                            ) {

                                MedicalRecord record =
                                        document.toObject(
                                                MedicalRecord.class
                                        );


                                if (record != null) {

                                    record.setId(
                                            document.getId()
                                    );

                                    recordList.add(record);
                                }
                            }


                            adapter.notifyDataSetChanged();


                            // Empty state
                            if (recordList.isEmpty()) {

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
                            "Failed to load medical records",
                            Toast.LENGTH_SHORT
                    ).show();
                });
    }


    // ==========================================
    // EDIT MEDICAL RECORD
    // ==========================================

    private void editMedicalRecord(
            MedicalRecord record) {

        Intent intent =
                new Intent(
                        MedicalRecords.this,
                        AddMedicalRecord.class
                );


        intent.putExtra(
                "petId",
                petId
        );


        intent.putExtra(
                "recordId",
                record.getId()
        );


        intent.putExtra(
                "diagnosis",
                record.getDiagnosis()
        );


        intent.putExtra(
                "date",
                record.getDate()
        );


        intent.putExtra(
                "vetName",
                record.getVetName()
        );


        intent.putExtra(
                "notes",
                record.getNotes()
        );


        startActivity(intent);
    }


    // ==========================================
    // DELETE MEDICAL RECORD
    // ==========================================

    private void deleteMedicalRecord(
            MedicalRecord record) {

        new AlertDialog.Builder(this)

                .setTitle("Delete Medical Record")

                .setMessage(
                        "Are you sure you want to delete this medical record?"
                )

                .setNegativeButton(
                        "Cancel",
                        null
                )

                .setPositiveButton(
                        "Delete",
                        (dialog, which) -> {

                            performDelete(record);
                        })

                .show();
    }


    // ==========================================
    // PERFORM DELETE
    // ==========================================

    private void performDelete(
            MedicalRecord record) {

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
                .collection("medicalRecords")
                .document(record.getId())
                .delete()

                .addOnSuccessListener(unused -> {

                    Toast.makeText(
                            this,
                            "Medical record deleted",
                            Toast.LENGTH_SHORT
                    ).show();


                    loadMedicalRecords();
                })

                .addOnFailureListener(e -> {

                    Toast.makeText(
                            this,
                            "Failed to delete medical record",
                            Toast.LENGTH_SHORT
                    ).show();
                });
    }


    // ==========================================
    // REFRESH WHEN RETURNING
    // ==========================================

    @Override
    protected void onResume() {

        super.onResume();


        if (petId != null) {

            loadMedicalRecords();
        }
    }
}