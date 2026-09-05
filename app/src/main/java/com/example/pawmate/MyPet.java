package com.example.pawmate;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MyPet extends AppCompatActivity {

    // XML Views
    private ImageButton btnBack;
    private FloatingActionButton btnAddPet;
    private MaterialCardView cardPet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Enable Edge-to-Edge
        EdgeToEdge.enable(this);

        // Connect XML layout
        setContentView(R.layout.activity_my_pet);

        // ===============================
        // CONNECT XML VIEWS
        // ===============================

        btnBack = findViewById(R.id.btnBack);
        btnAddPet = findViewById(R.id.btnAddPet);
        cardPet = findViewById(R.id.cardPet);

        // ===============================
        // BACK BUTTON
        // ===============================

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // ===============================
        // ADD PET BUTTON
        // ===============================

        btnAddPet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MyPet.this, AddPet.class);
                startActivity(intent);
            }
        });

        // ===============================
        // PET CARD
        // ===============================

        cardPet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(
                        MyPet.this,
                        "Pet details will open here",
                        Toast.LENGTH_SHORT
                ).show();
            }
        });
    }
}

