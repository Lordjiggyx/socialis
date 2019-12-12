package com.example.socialis;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class Register extends AppCompatActivity {


    //Views
    EditText emailet ,passwordet;
    Button regBtn;
    TextView exist;
    ProgressDialog progressDialog;

    //firebase object
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //Bar
        ActionBar action = getSupportActionBar();
        action.setTitle("Create Account");
        action.setDisplayHomeAsUpEnabled(true);
        action.setDisplayShowHomeEnabled(true);

        emailet = findViewById(R.id.emailEt);
        passwordet = findViewById(R.id.passwordEt);
        regBtn = findViewById(R.id.registerBtn);
        exist = findViewById(R.id.Existing);

        mAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog((this));
        progressDialog.setMessage("You Are Now Being Registered");

        regBtn.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = emailet.getText().toString();
                String password = passwordet.getText().toString();

                if(!Patterns.EMAIL_ADDRESS.matcher(email).matches())
                {
                    emailet.setError("Invalid Email");
                    emailet.setFocusable(true);
                }
                else if(password.length()<6)
                {
                    passwordet.setError("Invalid Password(Must be gretaer then 6 characters)");
                    passwordet.setFocusable(true);
                }
                else
                {
                    registerUser(email , password);
                }
            }
        }));

        exist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Register.this , Login.class));
                finish();
            }
        });
    }

    private void registerUser(String email, String password)
    {
        progressDialog.show();
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success
                            progressDialog.dismiss();
                            FirebaseUser user = mAuth.getCurrentUser();

                            String email = user.getEmail();
                            String uid = user.getUid();

                            HashMap<Object , String> hashMap = new HashMap<>();

                            hashMap.put("email" , email);
                            hashMap.put("uid" , uid);
                            hashMap.put("name" , "");
                            hashMap.put("phone" , "");
                            hashMap.put("image" , "");
                            hashMap.put("onllineStatus" , "Online");
                            hashMap.put("cover" , "");

                            FirebaseDatabase database = FirebaseDatabase.getInstance();

                            DatabaseReference reference = database.getReference("Users");

                            reference.child(uid).setValue(hashMap);

                            Toast.makeText(Register.this, "Registered....\n", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(Register.this, Dashboard.class));
                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            progressDialog.dismiss();
                            Toast.makeText(Register.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }


                    }
                }).addOnFailureListener((new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //get rid of progress and show error
                progressDialog.dismiss();
                Toast.makeText(Register.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();

            }
        }));
    }

    public boolean onSupportNavigateUp()
    {
        onBackPressed();;
        return super.onSupportNavigateUp();
    }
}
