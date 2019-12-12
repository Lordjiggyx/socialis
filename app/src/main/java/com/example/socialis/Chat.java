package com.example.socialis;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.ColorSpace;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.socialis.adapaters.AdapaterChat;
import com.example.socialis.models.ModelChat;
import com.example.socialis.models.ModelUser;
import com.example.socialis.notifications.APIService;
import com.example.socialis.notifications.Client;
import com.example.socialis.notifications.Data;
import com.example.socialis.notifications.Response;
import com.example.socialis.notifications.Sender;
import com.example.socialis.notifications.Token;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;


public class Chat extends AppCompatActivity {

    Toolbar toolbar;
    RecyclerView recyclerView;
    ImageView profileIv;
    TextView nameTv , userStautusTV;
    EditText messageET;
    ImageButton sendbtn;

    FirebaseAuth firebaseAuth;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference reference;

    ValueEventListener seenListner;
    DatabaseReference refSeen;

    List<ModelChat> chatList;
    AdapaterChat adapaterChat;
    String hisUid;
    String myUid;
    String hisimage;

    APIService apiService;
    boolean notify = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("");

        profileIv = findViewById(R.id.profileIv);
        recyclerView = findViewById(R.id.chat_recycler);

        nameTv= findViewById(R.id.nameTv);
        userStautusTV= findViewById(R.id.userStatusTV);
        messageET= findViewById(R.id.MessageET);
        sendbtn= findViewById(R.id.sendBtn);

        Intent intent = getIntent();
        hisUid = intent.getStringExtra("hisUid");


        firebaseAuth = FirebaseAuth.getInstance();

        firebaseDatabase = FirebaseDatabase.getInstance();

        reference = firebaseDatabase.getReference("Users");


        //Layout
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);

       apiService = Client.getRetrofit("https://fcm.googleapis.com").create(APIService.class);
        //Search user to get users info
        Query userinfo = reference.orderByChild("uid").equalTo(hisUid);
        //get user picture and name
        userinfo.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //check until required info is obtained
                for(DataSnapshot ds : dataSnapshot.getChildren())
                {
                    String name = "" + ds.child("name").getValue();
                    hisimage = "" + ds.child("image").getValue();
                    String online= "" + ds.child("onlineStatus").getValue();

                    nameTv.setText(name);

                    if(online.equals("online"))
                    {
                        userStautusTV.setText(online);
                    }
                    else
                    {
                         userStautusTV.setText("Offline");
                    }
                    try
                    {
                        Picasso.get().load(hisimage).placeholder(R.drawable.ic_default_white).into(profileIv);
                    }
                    catch (Exception E)
                    {
                        Picasso.get().load(R.drawable.ic_default_white).into(profileIv);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        sendbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notify = true;
                String message = messageET.getText().toString();

                if(TextUtils.isEmpty(message))
                {
                    Toast.makeText(Chat.this , "Cannot send empty message" , Toast.LENGTH_SHORT);
                }
                else
                {
                    sendMessage(message);
                }
                messageET.setText("");
            }
        });

        readMessage();
        seenMessage();
    }

    private void seenMessage() {
        refSeen = FirebaseDatabase.getInstance().getReference("Chats");

        seenListner = refSeen.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot ds : dataSnapshot.getChildren())
                {
                    ModelChat chat = ds.getValue(ModelChat.class);
                    if(chat.getReciever().equals(myUid) && chat.getSender().equals(hisUid))
                    {
                        HashMap<String , Object> hasSeenHashmap = new HashMap<>();
                        hasSeenHashmap.put("isSeen" , true);
                        ds.getRef().updateChildren(hasSeenHashmap);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void readMessage() {
        chatList = new ArrayList<>();
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Chats");
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                chatList.clear();
                for(DataSnapshot ds : dataSnapshot.getChildren())
                {
                    ModelChat chat = ds.getValue(ModelChat.class);
                    if(chat.getReciever().equals(myUid) && chat.getSender().equals(hisUid) ||chat.getReciever().equals(hisUid) && chat.getSender().equals(myUid) )
                    {
                        chatList.add(chat);
                    }
                    //adapter
                    adapaterChat = new AdapaterChat(Chat.this ,chatList , hisimage);
                    adapaterChat.notifyDataSetChanged();

                    recyclerView.setAdapter(adapaterChat);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void sendMessage(final String message) {



        DatabaseReference dbref = FirebaseDatabase.getInstance().getReference();

        String timestamp = String.valueOf(System.currentTimeMillis());

        HashMap<String , Object> hashMap = new HashMap<>();
        hashMap.put("sender" , myUid);
        hashMap.put("reciever" , hisUid);
        hashMap.put("TimeStamp" , timestamp);
        hashMap.put("message" , message);
        hashMap.put("isSeen" , false);
        dbref.child("Chats").push().setValue(hashMap);



        final DatabaseReference database = FirebaseDatabase.getInstance().getReference("Users").child(myUid);
        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                ModelUser user = dataSnapshot.getValue(ModelUser.class);

                if(notify)
                {
                    sendNotification(hisUid ,user.getName() , message);
                }
                notify = false;
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void sendNotification(final String hisUid, final String name, final String message) {
        DatabaseReference allTokens = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query = allTokens.orderByKey().equalTo(hisUid);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for ( DataSnapshot ds : dataSnapshot.getChildren())
                {
                    Token token = ds.getValue(Token.class);
                    Data data = new Data(myUid , name +":" +message , "New Message" , hisUid , R.drawable.ic_default_img);

                    Sender sender = new Sender(data , token.getToken());
                    apiService.sendNotification(sender)
                    .enqueue(new Callback<Response>() {
                        @Override
                        public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                            Toast.makeText(Chat.this , "" + response.message() , Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFailure(Call<Response> call, Throwable t) {

                        }
                    });
;
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
            myUid = user.getUid();// current uer
        }
        else
        {
            //user is not signed in brought to main menu

            startActivity(new Intent(this , MainActivity.class));
            finish();
        }
    }

    private void checkOnlineStatus(String status)
    {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(myUid);
        HashMap<String , Object> hashMap = new HashMap<>();
        hashMap.put("onlineStatus" , status);
        reference.updateChildren(hashMap);

    }

    @Override
    protected void onStart() {
        CheckUser();
        checkOnlineStatus("online");
        super.onStart();
    }


    @Override
    protected void onPause() {
        String time = String.valueOf(System.currentTimeMillis());

        super.onPause();
        checkOnlineStatus("offline");
        refSeen.removeEventListener(seenListner);
    }

    @Override
    protected void onResume() {
        checkOnlineStatus("online");
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main , menu);

        //hide search view
        menu.findItem(R.id.action_search).setVisible(false);


        return super.onCreateOptionsMenu(menu);

    }

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
