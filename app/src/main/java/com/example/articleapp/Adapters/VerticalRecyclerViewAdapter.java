package com.example.articleapp.Adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.articleapp.Activities.DetailActivity;
import com.example.articleapp.Models.Users;
import com.example.articleapp.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import okio.Options;

public class VerticalRecyclerViewAdapter extends FirestoreRecyclerAdapter<Users, VerticalRecyclerViewAdapter.MyViewHolder> {

    Context mContext;
    private OnItemClickListener listener;

    public VerticalRecyclerViewAdapter(@NonNull FirestoreRecyclerOptions<Users> options, Context mContext) {
        super(options);
        this.mContext = mContext;
    }

    @Override
    protected void onBindViewHolder(@NonNull MyViewHolder holder, int position, @NonNull Users model) {

        holder.author.setText("-By " + model.getUserName());
        holder.date.setText(model.getAddedDate());
        holder.title.setText(model.getTitle());

        String articleImage = model.getArticleImage();
        String userPhoto = model.getUserPhoto();

        Glide.with(mContext).load(articleImage).into(holder.articleImage);
        Glide.with(mContext).load(userPhoto).into(holder.userPhoto);

        holder.verticalLay.setAnimation(AnimationUtils.loadAnimation(mContext, R.anim.animate_diagonal_right_enter));
        holder.author.setAnimation(AnimationUtils.loadAnimation(mContext, R.anim.animate_diagonal_right_enter));
        holder.date.setAnimation(AnimationUtils.loadAnimation(mContext, R.anim.animate_diagonal_right_enter));
        holder.articleImage.setAnimation(AnimationUtils.loadAnimation(mContext, R.anim.animate_diagonal_right_enter));
        holder.userPhoto.setAnimation(AnimationUtils.loadAnimation(mContext, R.anim.animate_diagonal_right_enter));
        holder.title.setAnimation(AnimationUtils.loadAnimation(mContext, R.anim.animate_diagonal_right_enter));

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(mContext).inflate(R.layout.vertical_recycler_view, parent, false);

        return new MyViewHolder(v);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {

        this.listener = listener;

    }

    public interface OnItemClickListener {
        void onItemClick(DocumentSnapshot documentSnapshot, int position);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView articleImage;
        CircleImageView userPhoto;
        TextView title, date, author;

        ConstraintLayout verticalLay;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            articleImage = itemView.findViewById(R.id.vertical_article_image);
            userPhoto = itemView.findViewById(R.id.vertical_userPhoto);
            title = itemView.findViewById(R.id.vertical_article_title);
            date = itemView.findViewById(R.id.vertical_articleAdded);
            author = itemView.findViewById(R.id.articleAuthor);
            verticalLay = itemView.findViewById(R.id.vertical_Layout);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();

                    if (pos != RecyclerView.NO_POSITION && listener != null) {

                        listener.onItemClick(getSnapshots().getSnapshot(pos), pos);


                    }

                }
            });

        }
    }

}
