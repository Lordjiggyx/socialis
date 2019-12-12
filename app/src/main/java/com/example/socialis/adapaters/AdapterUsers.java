package com.example.socialis.adapaters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socialis.Chat;
import com.example.socialis.R;
import com.example.socialis.models.ModelUser;
import com.squareup.picasso.Picasso;

import java.util.List;


public class AdapterUsers extends RecyclerView.Adapter<AdapterUsers.MyHolder>
{
    Context context;
    List<ModelUser> userList;

    public AdapterUsers(Context context, List<ModelUser> userList) {
        this.context = context;
        this.userList = userList;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.row_users , parent ,false);

        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int i) {

        final String hisUID = userList.get(i).getUid();
        String userimage = userList.get(i).getImage();
        String username = userList.get(i).getName();
        final String useremail = userList.get(i).getEmail();

        holder.NameTv.setText(username);
        holder.EmailTv.setText(useremail);
        try{
            Picasso.get().load(userimage).into(holder.avatarIv);
        }
        catch (Exception e)
        {
            Picasso.get().load(R.drawable.ic_default_img).into(holder.avatarIv);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context , Chat.class);
                intent.putExtra("hisUid" , hisUID);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return this.userList.size();
    }

    // Create new views (invoked by the layout manager)


    class MyHolder extends RecyclerView.ViewHolder
    {

        ImageView avatarIv;
        TextView NameTv , EmailTv;


        public MyHolder(@NonNull View itemView)
        {
            super(itemView);

            avatarIv = itemView.findViewById(R.id.avatarIv);
            NameTv= itemView.findViewById(R.id.nameTv);
            EmailTv  = itemView.findViewById(R.id.EmailTv);
        }
    }
}

