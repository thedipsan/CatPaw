package com.example.pawmate;

import static java.lang.Thread.sleep;

import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    // Create variables for ImageView and TextView
    ImageView imageView;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Enable edge-to-edge screen
        EdgeToEdge.enable(this);

        // Connect Java code with activity_main.xml
        setContentView(R.layout.activity_main);

        // Connect ImageView with the XML ImageView
        imageView = findViewById(R.id.splash_image);

        // Connect TextView with the XML TextView
        textView = findViewById(R.id.splash_text);

        // Load the animation from res/anim/animation.xml
        Animation animation = AnimationUtils.loadAnimation(
                this,
                R.anim.animation
        );

        // Start animation on the image
        imageView.startAnimation(animation);

        // Start the same animation on the text
        textView.startAnimation(animation);


        // Create a new thread
        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {

                try {
                    // Wait for 5 seconds (5000 milliseconds)
                    sleep(5000);

                    // Open the Welcome activity after 5 seconds
                    Intent intent = new Intent(
                            getApplicationContext(),
                            Welcome.class
                    );

                    // Start the Welcome activity
                    startActivity(intent);

                } catch (InterruptedException e) {

                    // Handle thread interruption error
                    throw new RuntimeException(e);
                }
            }
        });

        // Start the thread
        thread.start();
    }
}