package com.example.socialis;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class HomeFragment extends Fragment {

FirebaseAuth firebaseAuth;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        firebaseAuth=FirebaseAuth.getInstance();

         View view = inflater.inflate(R.layout.fragment_home, container, false);

        return view;
    }

    private void  CheckUser()
    {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if(user != null)
        {
            //user is signed in
            //.setText(user.getEmail());
        }
        else
        {
            //user is not signed in brought to main menu

            startActivity(new Intent(getActivity() , MainActivity.class));
            getActivity().finish();
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu , MenuInflater inflater) {
        //inflating menu
        inflater.inflate(R.menu.menu_main , menu);
        super.onCreateOptionsMenu(menu , inflater);
    }

    //menu click

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();
        if( id== R.id.action_logout )
        {
            firebaseAuth.signOut();
            CheckUser();
        }
        return super.onOptionsItemSelected(item);
    }
}
