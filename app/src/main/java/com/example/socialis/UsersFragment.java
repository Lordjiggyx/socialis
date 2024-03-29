package com.example.socialis;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import com.example.socialis.adapaters.AdapterUsers;
import com.example.socialis.models.ModelUser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class UsersFragment extends Fragment {

RecyclerView recyclerView;
AdapterUsers adapterUsers;
List<ModelUser> userList;

    FirebaseAuth firebaseAuth;



    public UsersFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_users, container, false);

        firebaseAuth = FirebaseAuth.getInstance();

        recyclerView = view.findViewById(R.id.users_recyclerView);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));


        userList = new ArrayList<>();

        getAllUsers();


        return view;
    }

    private void getAllUsers() {
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        //Path to usets
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userList.clear();
                for(DataSnapshot ds : dataSnapshot.getChildren())
                {
                    ModelUser modelUser = ds.getValue(ModelUser.class);

                    if(!modelUser.getUid().equals(firebaseUser.getUid()))
                    {
                        userList.add(modelUser);
                    }

                    adapterUsers = new AdapterUsers(getActivity() , userList);
                    recyclerView.setAdapter(adapterUsers);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void SearhUsers(final String query) {

            final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            //Path to usets
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");

            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    userList.clear();
                    for(DataSnapshot ds : dataSnapshot.getChildren())
                    {
                        ModelUser modelUser = ds.getValue(ModelUser.class);

                        //Get all searched users
                        if(!modelUser.getUid().equals(firebaseUser.getUid()))
                        {
                            if(modelUser.getName().toLowerCase().contains(query.toLowerCase()) || modelUser.getEmail().toLowerCase().contains(query.toLowerCase()))
                            {
                                userList.add(modelUser);
                            }

                        }

                        adapterUsers = new AdapterUsers(getActivity() , userList);
                        //refresh adapter
                        adapterUsers.notifyDataSetChanged();
                        recyclerView.setAdapter(adapterUsers);

                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
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
        
        MenuItem item = menu.findItem((R.id.action_search));
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                if(!TextUtils.isEmpty(s.trim()))
                {
                    //Search text contains texts
                    SearhUsers(s);
                }
                else
                {
                    getAllUsers();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {

                if(!TextUtils.isEmpty(s.trim()))
                {
                    //Search by whatis in text
                    SearhUsers(s);
                }
                else
                {
                    getAllUsers();
                }
                return false;
            }
        });
        
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
