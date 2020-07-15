package com.example.articleapp.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.articleapp.Models.Users;
import com.example.articleapp.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.api.Context;
import org.w3c.dom.Text;

import de.hdodenhof.circleimageview.CircleImageView;

public class NotificationAdapter extends FirestoreRecyclerAdapter<Users,NotificationAdapter.MyViewHolder> {

    android.content.Context mContext;

    public NotificationAdapter(@NonNull FirestoreRecyclerOptions<Users> options, android.content.Context mContext) {
        super(options);
        this.mContext = mContext;
    }

    @Override
    protected void onBindViewHolder(@NonNull MyViewHolder holder, int position, @NonNull Users model) {

        if (!model.getUserPhoto().isEmpty()&&!model.getUserName().isEmpty()&&!model.getAddedDate().isEmpty()){
            holder.username.setText(model.getUserName());
            holder.date.setText(model.getAddedDate());
            Glide.with(mContext).load(model.getUserPhoto()).into(holder.userphoto);
            holder.notification_container.setAnimation(AnimationUtils.loadAnimation(mContext,R.anim.layout_anim));
        }

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(mContext).inflate(R.layout.notification_layout,parent,false);

        return new MyViewHolder(v);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        CircleImageView userphoto;
        TextView username;
        TextView date;
        ConstraintLayout notification_container;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.noti_username);
            userphoto = itemView.findViewById(R.id.noti_userPhoto);
            date = itemView.findViewById(R.id.noti_date);
            notification_container = itemView.findViewById(R.id.notification_container);
        }
    }

}
