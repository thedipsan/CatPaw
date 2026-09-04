package com.example.pawmate;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class SignUp extends AppCompatActivity {

     EditText etName, etEmail, etPassword, etConfirmPassword;
     MaterialButton btnCreateAccount;
     TextView tvSignIn;
     FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);


        etName= findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);

        btnCreateAccount= findViewById(R.id.btnCreateAccount);

        tvSignIn = findViewById(R.id.tvSignIn);

        mAuth= FirebaseAuth.getInstance();



      // Create Account
       btnCreateAccount.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               createAccount(); 
           }
       });

       // Goto Login
       tvSignIn.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Intent intent = new Intent(SignUp.this, LogIn.class);

           }
       });

    }



    private void createAccount() {
        String name= etName.getText().toString().trim();
        String email= etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();


          // Input Validation

        // Check name inputfield is empty or not
        if(name.isEmpty()){
            etName.setError("Enter your name");
            etName.requestFocus();
            return;
        }

        // check email inputfiled is empty or not
        if(email.isEmpty()){
            etEmail.setError("Enter your email");
            etEmail.requestFocus();
            return;
        }

        //check password inputfild is empty or not
        if(password.isEmpty()){
            etPassword.setError("Enter your password");
            etPassword.requestFocus();
            return;
        }

        // check password length is greather than 6 or not
        if(password.length()<6){
            etPassword.setError("Password must be at least 6 characters");
            etPassword.requestFocus();
            return;
        }

        // check comparing password
        if(!password.equals(confirmPassword)){
            etConfirmPassword.setError("Password do not match");
            etConfirmPassword.requestFocus();
            return;
        }



       // Disable Button While Creating Account
       btnCreateAccount.setEnabled(false);
        btnCreateAccount.setText("Creating Account...");

        // Create firebase account
        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                // Account Created
                if(task.isSuccessful()){
                    FirebaseUser user = mAuth.getCurrentUser();
                    if(user !=null){
                        // Save User Name
                        UserProfileChangeRequest profileUpdates= new UserProfileChangeRequest.Builder().setDisplayName(name).build();

                        user.updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> ProfileTask) {

                                if(ProfileTask.isSuccessful()){
                                    Toast.makeText(SignUp.this, "Account created successfully!", Toast.LENGTH_SHORT).show();
                                    //Open Dashboard
                                    Intent intent= new Intent(SignUp.this, Dashboard.class);
                                    startActivity(intent);
                                    finish();
                                }else{
                                    btnCreateAccount.setEnabled(true);
                                    btnCreateAccount.setText("Create Account");


                                    Toast.makeText(SignUp.this, "Account created, but name could not be saved", Toast.LENGTH_SHORT).show();

                                }


                            }
                        });
                    }else{
                        // Registeration Failed
                        btnCreateAccount.setEnabled(true);
                        btnCreateAccount.setText("Create Account");

                        String errorMessage= "Signup failed";

                        if(task.getException()!=null){
                            errorMessage=task.getException().getMessage();
                        }

                        Toast.makeText(SignUp.this,errorMessage, Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }
}