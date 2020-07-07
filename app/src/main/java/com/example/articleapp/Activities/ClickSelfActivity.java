package com.example.articleapp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ClickSelfActivity extends AppCompatActivity {

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


    private TextView title, date, description,total_views;
    private ImageView articleImage;
    private CircleImageView authorPhoto;
    private ImageView backArrow;
    private CardView detail_container;
    private RecyclerView comment_recyclerview;
    private ImageView add_comment_btn;
    private EditText comment_input;

    private FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
    private FirebaseStorage mStorage = FirebaseStorage.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    private CommentAdapter commentAdapter;
    private TextView comment_count;


    String postKEY;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            this.getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        setContentView(R.layout.activity_click_self);

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

        setUpRecyclerView();

        postKEY = getIntent().getStringExtra(KEY_POST_KEY);

        sendViews(postKEY);
        countTheViews();

    }

    private void countTheViews() {

        mFirestore.collection("Users").document(postKEY).collection("Views").get()
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

    private void setUpRecyclerView() {

        postKEY = getIntent().getStringExtra(KEY_POST_KEY);

        Query query = mFirestore.collection("Users").document(postKEY).collection("Comment_Contents")
                .orderBy("postedDate", Query.Direction.DESCENDING).orderBy("postedTime", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<Comments> options = new FirestoreRecyclerOptions.Builder<Comments>()
                .setQuery(query,Comments.class)
                .build();

        commentAdapter = new CommentAdapter(options,this);
        comment_recyclerview.setLayoutManager(new LinearLayoutManager(this));
        comment_recyclerview.setAdapter(commentAdapter);
        commentAdapter.notifyDataSetChanged();

        mFirestore.collection("Users").document(postKEY).collection("Comment_Contents").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            int count = 1;
                            for (DocumentSnapshot documentSnapshot : task.getResult()) {
                                comment_count.setText(String.valueOf(count++));
                            }
                        } else {
                            Toast.makeText(ClickSelfActivity.this, "Error: " + task.getException().toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

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
        SimpleDateFormat formatterTime = new SimpleDateFormat("hh-mm a");
        final String currentDate = formatterDate.format(date);
        final String currentTime = formatterTime.format(date);

        Comments c = new Comments();

        if (mAuth.getCurrentUser().getDisplayName()!=null&&mAuth.getCurrentUser().getPhotoUrl()!=null) {

            Comments comments = new Comments(mAuth.getCurrentUser().getUid(),
                    mAuth.getCurrentUser().getPhotoUrl().toString(),
                    comment_content,
                    mAuth.getCurrentUser().getDisplayName(),
                    currentDate, currentTime);

            mFirestore.collection("Users").document(postKEY).collection("Comment_Contents").add(comments)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Toast.makeText(ClickSelfActivity.this, "Comment Added...", Toast.LENGTH_SHORT).show();
                            add_comment_btn.setVisibility(View.VISIBLE);
                            comment_input.setText("");

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(ClickSelfActivity.this, "Error: " + e.toString(), Toast.LENGTH_SHORT).show();
                            add_comment_btn.setVisibility(View.VISIBLE);
                        }
                    });
        }else {
            Toast.makeText(this, "Fill up your profile credentials", Toast.LENGTH_SHORT).show();
        }
    }


    private void animateViews() {

        title.setAnimation(AnimationUtils.loadAnimation(this,R.anim.animate_slide_up_enter));
        date.setAnimation(AnimationUtils.loadAnimation(this,R.anim.animate_windmill_enter));
        backArrow.setAnimation(AnimationUtils.loadAnimation(this,R.anim.animate_diagonal_right_enter));
        description.setAnimation(AnimationUtils.loadAnimation(this,R.anim.animate_slide_down_enter));
        articleImage.setAnimation(AnimationUtils.loadAnimation(this,R.anim.animate_fade_enter));

    }

    @Override
    protected void onStart() {
        super.onStart();
        commentAdapter.startListening();
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


        title.setText(gtitle);
        date.setText("-" + gdate);
        description.setText(gdescription);

        Glide.with(this).load(garticleImage).into(articleImage);
        Glide.with(this).load(gauthorPhoto).into(authorPhoto);



    }


    private void Views() {

        title = findViewById(R.id.click_detail_title);
        date = findViewById(R.id.click_detail_date);
        backArrow = findViewById(R.id.detail_backBtn);
        description = findViewById(R.id.click_detail_description);
        articleImage = findViewById(R.id.detail_articleImage);
        authorPhoto = findViewById(R.id.click_detail_userPhoto);
        detail_container = findViewById(R.id.detail_container);
        comment_recyclerview = findViewById(R.id.click_comment_recylerview);
        comment_input = findViewById(R.id.click_comment_Input);
        add_comment_btn = findViewById(R.id.click_add_comment_btn);
        comment_count = findViewById(R.id.click_comment_count);
        total_views = findViewById(R.id.views_click);

    }

}