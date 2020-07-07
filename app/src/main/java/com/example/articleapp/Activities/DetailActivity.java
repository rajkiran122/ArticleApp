package com.example.articleapp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.bumptech.glide.Glide;
import com.example.articleapp.Adapters.CommentAdapter;
import com.example.articleapp.Models.Comments;
import com.example.articleapp.R;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class DetailActivity extends AppCompatActivity {

    private static final String SHAREPREF = "isViewed";
    private static final String ISVIEWED = "view";
    private static final String VIEWEDID = "postkey";
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

    String postKey;
    CommentAdapter adapter;
    boolean isLiked = false;
    int size;
    private TextView title, date, description, comment_size,total_views;
    private ImageView articleImage;
    private CircleImageView authorPhoto;
    private ImageView backArrow;
    private CardView detail_container;
    private EditText comment_input;
    private ImageView add_comment_btn;
    private RecyclerView comment_recyclerview;
    private FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
    private FirebaseStorage mStorage = FirebaseStorage.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    private boolean isViewed = false;

    private List<String> viewedIDs;

    private SharedPreferences sp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            this.getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        setContentView(R.layout.activity_detail);

        Views();
        loadDetails();

        animateViews();

        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        add_comment_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postComments();
            }
        });

        postKey = getIntent().getStringExtra(KEY_POST_KEY);

        sendViews(postKey);
        countTheViews();

        setUpCommentRecylerView();

    }

    private void countTheViews() {

        mFirestore.collection("Users").document(postKey).collection("Views").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            int count = 1;
                            for (DocumentSnapshot documentSnapshot : task.getResult()){
                                total_views.setText(String.valueOf(count++));
                            }
                        }
                    }
                });

    }

    private void sendViews(String postKey) {

        Map<String, Object> view = new HashMap<>();
        view.put("userId", mAuth.getCurrentUser().getUid());

        mFirestore.collection("Users").document(postKey).collection("Views")
                .document(mAuth.getCurrentUser().getUid()).set(view);
    }

    private void setUpCommentRecylerView() {

        Query query = mFirestore.collection("Users").document(postKey).collection("Comment_Contents")
                .orderBy("postedDate", Query.Direction.ASCENDING).orderBy("postedTime", Query.Direction.ASCENDING);

        FirestoreRecyclerOptions<Comments> options = new FirestoreRecyclerOptions.Builder<Comments>()
                .setQuery(query, Comments.class)
                .build();

        adapter = new CommentAdapter(options, this);
        comment_recyclerview.setLayoutManager(new LinearLayoutManager(this));
        comment_recyclerview.setAdapter(adapter);
        adapter.notifyDataSetChanged();


        mFirestore.collection("Users").document(postKey).collection("Comment_Contents").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            int count = 1;
                            for (DocumentSnapshot documentSnapshot : task.getResult()) {
                                comment_size.setText(String.valueOf(count++));
                            }
                        } else {
                            Toast.makeText(DetailActivity.this, "Error: " + task.getException().toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    @Override
    protected void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    private void postComments() {


        String comment_content = comment_input.getText().toString();

        if (comment_content.isEmpty()) {
            Toast.makeText(this, "First, write a comment", Toast.LENGTH_SHORT).show();
            return;
        }

        add_comment_btn.setVisibility(View.GONE);

        Date date = Calendar.getInstance().getTime();

        SimpleDateFormat formatterDate = new SimpleDateFormat("MMM dd, yyyy");
        SimpleDateFormat formatterTime = new SimpleDateFormat("hh-mm");
        final String currentDate = formatterDate.format(date);
        final String currentTime = formatterTime.format(date);

        Comments c = new Comments();

        if (mAuth.getCurrentUser().getDisplayName() != null && mAuth.getCurrentUser().getPhotoUrl() != null) {

            Comments comments = new Comments(mAuth.getCurrentUser().getUid(),
                    mAuth.getCurrentUser().getPhotoUrl().toString(),
                    comment_content,
                    mAuth.getCurrentUser().getDisplayName(),
                    currentDate, currentTime);

            mFirestore.collection("Users").document(postKey).collection("Comment_Contents").add(comments)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Toast.makeText(DetailActivity.this, "Comment Added...", Toast.LENGTH_SHORT).show();
                            add_comment_btn.setVisibility(View.VISIBLE);
                            comment_input.setText("");
                            adapter.notifyDataSetChanged();

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(DetailActivity.this, "Error: " + e.toString(), Toast.LENGTH_SHORT).show();
                            add_comment_btn.setVisibility(View.VISIBLE);
                        }
                    });
        } else {
            Toast.makeText(this, "Fill up your profile credentials", Toast.LENGTH_SHORT).show();
        }
    }

    private void animateViews() {

        title.setAnimation(AnimationUtils.loadAnimation(this, R.anim.animate_slide_up_enter));
        date.setAnimation(AnimationUtils.loadAnimation(this, R.anim.animate_windmill_enter));
        backArrow.setAnimation(AnimationUtils.loadAnimation(this, R.anim.animate_diagonal_right_enter));
        description.setAnimation(AnimationUtils.loadAnimation(this, R.anim.animate_slide_down_enter));
        authorPhoto.setAnimation(AnimationUtils.loadAnimation(this, R.anim.animate_slide_up_enter));
        articleImage.setAnimation(AnimationUtils.loadAnimation(this, R.anim.animate_fade_enter));

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void loadDetails() {

        String gtitle = getIntent().getStringExtra(KEY_TITLE);
        String gdescription = getIntent().getStringExtra(KEY_DESCRIPTION);
        String gdate = getIntent().getStringExtra(KEY_DATE);
        String garticleImage = getIntent().getStringExtra(KEY_ARTICLE_IMAGE);
        String gauthorPhoto = getIntent().getStringExtra(KEY_AUTHOR_PHOTO);


        authorPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToAuthorProfile();
            }
        });

        title.setText(gtitle);
        date.setText("-" + gdate);
        description.setText(gdescription);

        Glide.with(this).load(garticleImage).into(articleImage);
        Glide.with(this).load(gauthorPhoto).into(authorPhoto);


    }

    private void goToAuthorProfile() {

        String guserName = getIntent().getStringExtra(KEY_USERNAME);
        String gEmail = getIntent().getStringExtra(KEY_EMAIL);
        String gBio = getIntent().getStringExtra(KEY_BIO);
        String gUserPhoto = getIntent().getStringExtra(KEY_AUTHOR_PHOTO);
        String userId = getIntent().getStringExtra(KEY_USERID);

        Intent profileIntent = new Intent(DetailActivity.this, ClickProfileActivity.class);
        profileIntent.putExtra(KEY_USERNAME, guserName);
        profileIntent.putExtra(KEY_EMAIL, gEmail);
        profileIntent.putExtra(KEY_BIO, gBio);
        profileIntent.putExtra(KEY_AUTHOR_PHOTO, gUserPhoto);
        profileIntent.putExtra(KEY_USERID, userId);
        startActivity(profileIntent);

        Animatoo.animateShrink(this);

    }

    private void Views() {

        title = findViewById(R.id.detail_title);
        date = findViewById(R.id.detail_date);
        backArrow = findViewById(R.id.detail_backBtn);
        description = findViewById(R.id.detail_description);
        articleImage = findViewById(R.id.detail_articleImage);
        authorPhoto = findViewById(R.id.detail_userPhoto);
        detail_container = findViewById(R.id.detail_container);
        comment_input = findViewById(R.id.comment_Input);
        add_comment_btn = findViewById(R.id.add_comment_btn);
        comment_recyclerview = findViewById(R.id.comment_RecyclerView);
        comment_size = findViewById(R.id.comment_size);
        total_views = findViewById(R.id.total_views);
    }
}