package com.example.socialis.adapaters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.format.DateFormat;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socialis.R;
import com.example.socialis.models.ModelChat;
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

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class AdapaterChat  extends RecyclerView.Adapter<AdapaterChat.MyHolder>  {


    private static  final int MSG_TYPE_LEFT = 0;
    private static  final int MSG_TYPE_rIGHT = 1;
    Context context;
    List<ModelChat> chatList;
    FirebaseUser firebaseUser;
    String imageurl;

    public AdapaterChat(Context context, List<ModelChat> chatList, String imageurl) {
        this.context = context;
        this.chatList = chatList;
        this.imageurl = imageurl;
    }

    @NonNull
    @Override
    public AdapaterChat.MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if(viewType == MSG_TYPE_rIGHT)
        {
            View view = LayoutInflater.from(context).inflate(R.layout.row_chat_right , parent , false);
            return new MyHolder(view);
        }
        else
        {
            View view = LayoutInflater.from(context).inflate(R.layout.row_chat_left , parent , false);
            return new MyHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull AdapaterChat.MyHolder holder, final int position) {
        String message = chatList.get(position).getMessage();
        String time = chatList.get(position).getTime();
//
//        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
//        calendar.setTimeInMillis(Long.parseLong(time));
//        String datetime = DateFormat.format("dd/MM/yyyy hh:mm aa" , calendar).toString();

        holder.messageTv.setText(message);
        holder.timeTv.setText(time);

        try
        {
            Picasso.get().load(imageurl).into(holder.profileIV);
        }
        catch (Exception e)
        {
            Picasso.get().load(R.drawable.ic_default_img).into(holder.profileIV);
        }



        if(position==chatList.size()-1)
        {
            if(chatList.get(position).isSeen())
            {
                holder.isSeenTv.setText("Seen");

            }
            else
            {
                holder.isSeenTv.setText("Delivered");

            }
        }
        else
        {
            holder.isSeenTv.setVisibility(View.GONE);
        }


    }


    @Override
    public int getItemCount() {
        return chatList.size();
    }

    @Override
    public int getItemViewType(int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if(chatList.get(position).getSender().equals(firebaseUser.getUid()))
        {
            return MSG_TYPE_rIGHT;
        }
        else
        {
            return MSG_TYPE_LEFT;
        }


    }

    class MyHolder extends RecyclerView.ViewHolder {

        ImageView profileIV;
        TextView messageTv , timeTv , isSeenTv;
        LinearLayout messageLayout;





        public MyHolder(@NonNull View itemView) {
            super(itemView);

            profileIV = itemView.findViewById(R.id.profileIv);
            messageTv = itemView.findViewById(R.id.MessageTv);
            timeTv = itemView.findViewById(R.id.timeTv);
            isSeenTv = itemView.findViewById(R.id.isSeenTv);
            messageLayout = itemView.findViewById(R.id.messageLayout);



        }
    }
}
