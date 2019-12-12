package com.example.socialis;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.socialis.notifications.Token;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

public class Dashboard extends AppCompatActivity {

    //Firebase authentication
    FirebaseAuth firebaseAuth;


    ActionBar action;

    String myYUid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

         action = getSupportActionBar();
        action.setTitle("Profile ");


        //Initialise
        firebaseAuth = FirebaseAuth.getInstance();

        BottomNavigationView navigationView = findViewById(R.id.navigation);
        navigationView.setOnNavigationItemSelectedListener(selected);

        action.setTitle("Profile");
        ProfileFragment frag1 = new ProfileFragment();
        FragmentTransaction ft1 = getSupportFragmentManager().beginTransaction();
        ft1.replace(R.id.content , frag1 , "");
        ft1.commit();

        CheckUser();

       // updateToken(FirebaseInstanceId.getInstance().getToken());
    }

    @Override
    protected void onResume() {
        CheckUser();
        super.onResume();
    }

    public void updateToken(String token)
    {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Tokens");
        Token mtoken = new Token(token);
//        ref.child(myYUid).setValue(mtoken);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener selected = new  BottomNavigationView.OnNavigationItemSelectedListener()
    {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            switch(menuItem.getItemId())
            {
//                case R.id.nav_home:
//                    //homr fragment
//                    action.setTitle("Home");
//                    HomeFragment frag1 = new HomeFragment();
//                    FragmentTransaction ft1 = getSupportFragmentManager().beginTransaction();
//                    ft1.replace(R.id.content , frag1 , "");
//                    ft1.commit();
//                    return true;
                case R.id.nav_profile:
                    //Profile fragment
                    action.setTitle("Profile");
                    ProfileFragment frag2 = new ProfileFragment();
                    FragmentTransaction ft2 = getSupportFragmentManager().beginTransaction();
                    ft2.replace(R.id.content , frag2 , "");
                    ft2.commit();
                    return true;
                case R.id.nav_users:
                    //users ftagement
                    action.setTitle("Users");
                    UsersFragment frag3 = new UsersFragment();
                    FragmentTransaction ft3 = getSupportFragmentManager().beginTransaction();
                    ft3.replace(R.id.content , frag3 , "");
                    ft3.commit();
                    return true;
//                case R.id.nav_chat:
//                    //users ftagement
//                    action.setTitle("Chat");
//                    ChatlistFragment frag4 = new ChatlistFragment();
//                    FragmentTransaction ft4 = getSupportFragmentManager().beginTransaction();
//                    ft4.replace(R.id.content , frag4 , "");
//                    ft4.commit();
//                    return true;
            }
            return false;

        }
    };

    private void  CheckUser()
    {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if(user != null)
        {
            //user is signed in
            //
            myYUid=user.getUid();

            SharedPreferences sp = getSharedPreferences("SP_USER" , MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.putString("CURRENT_USERID", myYUid);
            editor.apply();
        }
        else
        {
            //user is not signed in brought to main menu

            startActivity(new Intent(Dashboard.this , MainActivity.class));
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    protected void onStart()
    {
        CheckUser();
        super.onStart();
    }

    //inflate options menu


}
