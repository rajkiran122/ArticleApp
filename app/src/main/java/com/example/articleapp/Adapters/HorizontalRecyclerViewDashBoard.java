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
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.bumptech.glide.Glide;
import com.example.articleapp.Activities.DetailActivity;
import com.example.articleapp.Models.Users;
import com.example.articleapp.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class HorizontalRecyclerViewDashBoard extends RecyclerView.Adapter<HorizontalRecyclerViewDashBoard.MyViewHolder>{


    private final String KEY_TITLE = "title";
    private final String KEY_DESCRIPTION = "description";
    private final String KEY_DATE = "date";
    private final String KEY_ARTICLE_IMAGE = "articleimage";
    private final String KEY_AUTHOR_PHOTO = "authorphoto";
    private final String KEY_USERNAME = "username";
    private final String KEY_EMAIL = "email";
    private final String KEY_BIO = "bio";
    private final String KEY_USERID = "userid";
    private final String KEY_POST_KEY = "postKey";



    Context mContext;
    private FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseStorage mStorage = FirebaseStorage.getInstance();

    List<Users> arrayList;

    public HorizontalRecyclerViewDashBoard(Context mContext, List<Users> arrayList) {
        this.mContext = mContext;
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(mContext).inflate(R.layout.horizontal_recyler_view,parent,false);

        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        holder.articleTitle.setText(arrayList.get(position).getTitle());
        holder.articleUsername.setText("-By "+arrayList.get(position).getUserName());
        holder.articleDateAdded.setText(arrayList.get(position).getAddedDate());

        Glide.with(mContext).load(arrayList.get(position).getArticleImage()).into(holder.articleImage);
        Glide.with(mContext).load(arrayList.get(position).getUserPhoto()).into(holder.userPhoto);

        holder.articleTitle.setAnimation(AnimationUtils.loadAnimation(mContext,R.anim.animate_shrink_enter));
        holder.articleImage.setAnimation(AnimationUtils.loadAnimation(mContext,R.anim.animate_fade_enter));
        holder.articleUsername.setAnimation(AnimationUtils.loadAnimation(mContext,R.anim.animate_slide_left_enter));
        holder.articleDateAdded.setAnimation(AnimationUtils.loadAnimation(mContext,R.anim.animate_slide_out_right));
        holder.horizontal_cardview.setAnimation(AnimationUtils.loadAnimation(mContext,R.anim.animate_swipe_left_enter));
        holder.userPhoto.setAnimation(AnimationUtils.loadAnimation(mContext,R.anim.animate_slide_out_right));

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        CardView horizontal_cardview;
        ImageView articleImage;
        TextView articleTitle, articleUsername, articleDateAdded;
        CircleImageView userPhoto;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            articleImage = itemView.findViewById(R.id.articleImage);
            articleTitle = itemView.findViewById(R.id.articleTitle);
            articleDateAdded = itemView.findViewById(R.id.dateAddedArticle);
            articleUsername = itemView.findViewById(R.id.articlePoster_userName);
            userPhoto = itemView.findViewById(R.id.article_Userphoto);
            horizontal_cardview = itemView.findViewById(R.id.horizontal_Cardview);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();

                    Intent detailIntent = new Intent(mContext, DetailActivity.class);
                    detailIntent.putExtra(KEY_TITLE,arrayList.get(pos).getTitle());
                    detailIntent.putExtra(KEY_DESCRIPTION,arrayList.get(pos).getArticleDetail());
                    detailIntent.putExtra(KEY_DATE,arrayList.get(pos).getAddedDate());
                    detailIntent.putExtra(KEY_AUTHOR_PHOTO,arrayList.get(pos).getUserPhoto());
                    detailIntent.putExtra(KEY_ARTICLE_IMAGE,arrayList.get(pos).getArticleImage());
                    detailIntent.putExtra(KEY_USERNAME,arrayList.get(pos).getUserName());
                    detailIntent.putExtra(KEY_EMAIL,arrayList.get(pos).getUserEmail());
                    detailIntent.putExtra(KEY_BIO,arrayList.get(pos).getBiography());
                    detailIntent.putExtra(KEY_USERID,arrayList.get(pos).getUserID());
                    detailIntent.putExtra(KEY_POST_KEY,arrayList.get(pos).getPostKey());

                    mContext.startActivity(detailIntent);
                    Animatoo.animateInAndOut(mContext);

                }
            });
        }
    }
}
