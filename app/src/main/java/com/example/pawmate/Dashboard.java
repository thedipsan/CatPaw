package com.example.pawmate;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Dashboard extends AppCompatActivity {

    // Firebase Authentication
    FirebaseAuth mAuth;

    //UI Element
    TextView tvGreeting;
    ImageButton btnNotification;
    BottomNavigationView bottomNavigation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_dashboard);

        // Initialize Firebase
        mAuth= FirebaseAuth.getInstance();

        // Connect with XML
        tvGreeting= findViewById(R.id.tvGreeting);
        btnNotification= findViewById(R.id.btnNotification);
        bottomNavigation= findViewById(R.id.bottomNavigation);

        // Checked whether the Useer is loggedin
        checkUser();

        // Notification button
        btnNotification.setOnClickListener(v -> {

            Toast.makeText(Dashboard.this, "No new notifications", Toast.LENGTH_SHORT).show();

        });


        // Bottom Navigation
        bottomNavigation.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if(id == R.id.navHome){
                // Already on dashboard
                return true;
            }
            if(id == R.id.navPets){
                // Open my pets

                Intent intent = new Intent(Dashboard.this, MyPet.class);
                startActivity(intent);
                return true;
            }
            if(id == R.id.navSchedule){
                //Open My Schedule
                Intent intent= new Intent(Dashboard.this, Schedule.class);
                startActivity(intent);
                return true;
            }
            if(id== R.id.navProfile){
                //Open My Profile
                Intent intent = new Intent(Dashboard.this,MyProfile.class);
                startActivity(intent);
                return true;

            }
            return false;
        });
    }


    // Check the current user inside the firebase
    private void checkUser() {
        FirebaseUser user = mAuth.getCurrentUser();
        if(user == null){
            // User is not  loggedin
            // Send them back to log in
            Intent intent = new Intent(Dashboard.this, LogIn.class);
            startActivity(intent);
            finish();
            return;

        }

        // Get User's Display Name
        String name = user.getDisplayName();
        if(name != null && !name.isEmpty()){
            tvGreeting.setText("Hi,"+name+"👋");
        }else{
            tvGreeting.setText("Hi, PawMate User");
        }
    }
}