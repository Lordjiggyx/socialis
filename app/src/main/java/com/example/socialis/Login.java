package com.example.socialis;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class Login extends AppCompatActivity {

    private static final int RC_SIGN_IN =100 ;
    GoogleSignInClient mGoogleSignInClient;
    EditText emailet , passwordet ;
    TextView noAcc , recoverPass;
    Button loginbtn;
    SignInButton GoogleBtn;

    private FirebaseAuth mAuth;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Bar
        ActionBar action = getSupportActionBar();
        action.setTitle("Login");
        action.setDisplayHomeAsUpEnabled(true);
        action.setDisplayShowHomeEnabled(true);

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this,gso);

        mAuth = FirebaseAuth.getInstance();

        emailet = findViewById(R.id.emailEt);
        passwordet = findViewById(R.id.passwordEt);
        loginbtn = findViewById(R.id.loginBtn);
        noAcc = findViewById(R.id.Existnull);
        recoverPass= findViewById(R.id.recoverPss);
        GoogleBtn = findViewById(R.id.googlebtn);

        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = emailet.getText().toString();
                String password = passwordet.getText().toString();

                if(!Patterns.EMAIL_ADDRESS.matcher(email).matches())
                {
                    emailet.setError("Invalid Email");
                    emailet.setFocusable(true);
                }
                else
                {
                   login(email , password);
                }
            }
        });

        noAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Login.this , Register.class));
                finish();
            }
        });

        recoverPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRecoverPasswordDialog();
            }
        });

        GoogleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);

            }
        });


        progressDialog = new ProgressDialog((this));

    }


    private void showRecoverPasswordDialog()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Recover Password");

        LinearLayout linearLayout = new LinearLayout(this);

        final EditText emailet = new EditText(this);
        emailet.setHint("Email");
        emailet.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);

        emailet.setMinEms(16);

        linearLayout.addView(emailet);
        linearLayout.setPadding(10 ,10 ,10 ,10 );

        builder.setView(linearLayout);

        builder.setPositiveButton("Recover", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String email = emailet.getText().toString();
                beginRecovery(email);
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
            }
        });

        builder.create().show();

    }

    private void beginRecovery(String email) {
        progressDialog.setMessage("Sending email...");
        progressDialog.show();
        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                progressDialog.dismiss();
                if(task.isSuccessful())
                {
                    Toast.makeText(Login.this , "Email sent" , Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(Login.this , "Failed" , Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(Login.this , ""+e.getMessage() , Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void login(String email , String password)
    {
        progressDialog.setMessage("You Are Now Being Logged In");
        progressDialog.show();
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information

                            FirebaseUser user = mAuth.getCurrentUser();
                            progressDialog.dismiss();
                            startActivity(new Intent(Login.this , Dashboard.class) );
                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(Login.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                        }

                        // ...
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Login.this, "" + e.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    public boolean onSupportNavigateUp()
    {
        onBackPressed();;
        return super.onSupportNavigateUp();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Toast.makeText(this , ""+ e.getMessage() , Toast.LENGTH_SHORT).show();
                // ...
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();

                            if(task.getResult().getAdditionalUserInfo().isNewUser())
                            {
                                String email = user.getEmail();
                                String uid = user.getUid();

                                HashMap<Object , String> hashMap = new HashMap<>();

                                hashMap.put("email" , email);
                                hashMap.put("uid" , uid);
                                hashMap.put("name" , "");
                                hashMap.put("onlineStatus" , "online");
                                hashMap.put("phone" , "");
                                hashMap.put("image" , "");
                                hashMap.put("cover" , "");

                                FirebaseDatabase database = FirebaseDatabase.getInstance();

                                DatabaseReference reference = database.getReference("Users");

                                reference.child(uid).setValue(hashMap);
                            }




                            Toast.makeText(Login.this , ""+user.getEmail()  , Toast.LENGTH_SHORT).show();

                            startActivity(new Intent(Login.this , Dashboard.class) );
                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(Login.this , "Login Failed..."  , Toast.LENGTH_SHORT).show();

                        }

                        // ...
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Login.this , ""+ e.getMessage() , Toast.LENGTH_SHORT).show();
            }
        });
    }

}
