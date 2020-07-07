package com.example.articleapp.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.bumptech.glide.Glide;
import com.example.articleapp.Adapters.ClickProfileRecyclerViewAdapter;
import com.example.articleapp.Adapters.ProfileRecyclerViewAdapter;
import com.example.articleapp.Models.Users;
import com.example.articleapp.R;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import de.hdodenhof.circleimageview.CircleImageView;

public class ClickProfileActivity extends AppCompatActivity {

    private final String KEY_USERID = "userid";
    private final String KEY_AUTHOR_PHOTO = "authorphoto";
    private final String KEY_USERNAME = "username";
    private final String KEY_EMAIL = "email";
    private final String KEY_BIO = "bio";
    private final String KEY_POST_KEY = "postKey";
    private final String KEY_TITLE = "title";
    private final String KEY_DESCRIPTION = "description";
    private final String KEY_DATE = "date";
    private final String KEY_ARTICLE_IMAGE = "articleimage";
    String guserID;
    private CircleImageView userPhoto;
    private TextView username, email, bio;
    private ImageView back_btn;
    private RecyclerView recyclerView;
    private ConstraintLayout click_profile_upper_layout, click_profile_username, click_profile_email, click_profile_bio;
    private RelativeLayout click_profile_post_line;
    private ClickProfileRecyclerViewAdapter adapter;

    private FirebaseFirestore mFirestore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            this.getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        setContentView(R.layout.activity_click_profile);

        click_profile_bio = findViewById(R.id.click_profile_bio);
        click_profile_email = findViewById(R.id.click_profile_email);
        click_profile_username = findViewById(R.id.click_profile_username);
        click_profile_upper_layout = findViewById(R.id.click_profile_upper_layout);
        click_profile_post_line = findViewById(R.id.click_profile_post_line);
        back_btn = findViewById(R.id.click_profile_back_btn);


        click_profile_upper_layout.setAnimation(AnimationUtils.loadAnimation(this, R.anim.animate_slide_down_enter));
        click_profile_post_line.setAnimation(AnimationUtils.loadAnimation(this, R.anim.animate_card_enter));
        click_profile_username.setAnimation(AnimationUtils.loadAnimation(this, R.anim.animate_slide_left_enter));
        click_profile_email.setAnimation(AnimationUtils.loadAnimation(this, R.anim.animate_slide_out_right));
        click_profile_bio.setAnimation(AnimationUtils.loadAnimation(this, R.anim.animate_slide_left_enter));
        back_btn.setAnimation(AnimationUtils.loadAnimation(this, R.anim.animate_diagonal_right_enter));


        userPhoto = findViewById(R.id.click_user_photo_Profile);
        username = findViewById(R.id.click_profileAct_userName);
        email = findViewById(R.id.click_profileAct_email);
        bio = findViewById(R.id.click_profileAct_Biography);
        recyclerView = findViewById(R.id.click_posts_RecyclerView);

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        loadDetails();

        mFirestore = FirebaseFirestore.getInstance();

        setUpRecyclerView();
    }

    private void setUpRecyclerView() {

        CollectionReference collRef = mFirestore.collection("Users");

        Query query = collRef.whereEqualTo("userID", guserID);

        FirestoreRecyclerOptions<Users> options = new FirestoreRecyclerOptions.Builder<Users>()
                .setQuery(query, Users.class)
                .build();

        adapter = new ClickProfileRecyclerViewAdapter(options, this);

        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));

        recyclerView.setAdapter(adapter);

        adapter.setOnClickItemListener(new ClickProfileRecyclerViewAdapter.OnClickItemListener() {
            @Override
            public void OnClickItem(DocumentSnapshot documentSnapshot, int position) {

                Users user = documentSnapshot.toObject(Users.class);
                String title = user.getTitle();
                String description = user.getArticleDetail();
                String date = user.getAddedDate();
                String userPhoto = user.getUserPhoto();
                String articleImage = user.getArticleImage();
                String postKey = user.getPostKey();

                Intent detailIntent = new Intent(ClickProfileActivity.this, ClickSelfActivity.class);
                detailIntent.putExtra(KEY_TITLE, title);
                detailIntent.putExtra(KEY_DESCRIPTION, description);
                detailIntent.putExtra(KEY_DATE, date);
                detailIntent.putExtra(KEY_ARTICLE_IMAGE, articleImage);
                detailIntent.putExtra(KEY_AUTHOR_PHOTO, userPhoto);
                detailIntent.putExtra(KEY_POST_KEY, postKey);
                startActivity(detailIntent);
                Animatoo.animateSplit(ClickProfileActivity.this);

            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    private void loadDetails() {

        String guserName = getIntent().getStringExtra(KEY_USERNAME);
        String gemail = getIntent().getStringExtra(KEY_EMAIL);
        String gbio = getIntent().getStringExtra(KEY_BIO);
        String guserimg = getIntent().getStringExtra(KEY_AUTHOR_PHOTO);
        guserID = getIntent().getStringExtra(KEY_USERID);

        username.setText(guserName);
        bio.setText(gbio);
        if (gemail.isEmpty()){
            email.setText("Email not found");
        }else {
            email.setText(gemail);
        }

        Glide.with(this).load(guserimg).into(userPhoto);

    }
}