package com.example.pawmate;

import android.app.ComponentCaller;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

public class LogIn extends AppCompatActivity {

    EditText etEmail, etPassword;
    MaterialButton btnLogin, btnGoogle;
    TextView tvSignUp, tvForgotPassword;
    FirebaseAuth mAuth;

    GoogleSignInClient googleSignInClient;

    private static final int RC_SIGN_IN= 100;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_log_in);

       // Initialize Firebase Auth
        mAuth= FirebaseAuth.getInstance();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleSignInClient= GoogleSignIn.getClient(this, gso);

        // Connect with XML component
        etEmail = findViewById(R.id.etEmail);
        etPassword= findViewById(R.id.etPassword);

        btnGoogle= findViewById(R.id.btnGoogle);
        btnLogin= findViewById(R.id.btnLogin);
        tvSignUp= findViewById(R.id.tvSignUp);
        tvForgotPassword= findViewById(R.id.tvForgotPassword);



        // Login Button
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });


        //Go to forgot password
        tvForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LogIn.this, ForgotPassword.class);
                startActivity(intent);
            }
        });

        // Login With Google
        btnGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInWithGoogle();
            }
        });


        // Go to signup
        tvSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LogIn.this, SignUp.class);
                startActivity(intent);
            }
        });




    }

    private void signInWithGoogle() {
        Intent signInIntent= googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    };


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data, @NonNull ComponentCaller caller) {
        super.onActivityResult(requestCode, resultCode, data, caller);

        if(requestCode == RC_SIGN_IN){
            Task<GoogleSignInAccount>task= GoogleSignIn.getSignedInAccountFromIntent(data);

            try {
                GoogleSignInAccount account= task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);

            }catch (ApiException e){
                Toast.makeText(this, "Google Sign-In failed", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        AuthCredential credential= GoogleAuthProvider.getCredential(account.getIdToken(),null);

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()){
                        Toast.makeText(this, "Google Login successful", Toast.LENGTH_SHORT).show();


                        //Open Dashboard
                        Intent intent = new Intent(LogIn.this, Dashboard.class);
                        startActivity(intent);
                        finish();
                    }else{
                        Toast.makeText(this, "Google Authentication failed", Toast.LENGTH_SHORT).show();

                    }
                });


    }

    private void loginUser() {

        String email= etEmail.getText().toString().trim();
        String password= etPassword.getText().toString().trim();

         // Validation
        if(email.isEmpty()){
            etEmail.setError("Enter your email");
            etEmail.requestFocus();
            return;
        }
        // Check Password
        if(password.isEmpty()){
            etPassword.setError("Enter your password");
            etPassword.requestFocus();
            return;
        }

        // Disable button while loggin in
        btnLogin.setEnabled(false);
        btnLogin.setText("Logging in...");

        // Firebase Login
        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()){
                    Toast.makeText(LogIn.this, "Login Successful", Toast.LENGTH_SHORT).show();

                    //Open Dashboard
                    Intent intent = new Intent(LogIn.this, Dashboard.class);
                    startActivity(intent);
                    finish();
                }else{
                    btnLogin.setEnabled(true);
                    btnLogin.setText("Login");
                    String errorMessage= "Login failed";

                    if(task.getException()!=null){
                        errorMessage= task.getException().getMessage();
                    }

                    Toast.makeText(LogIn.this,errorMessage, Toast.LENGTH_SHORT).show();
                }


            }
        });


    }
}



