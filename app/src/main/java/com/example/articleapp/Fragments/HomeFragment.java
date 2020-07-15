package com.example.articleapp.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

//import com.example.articleapp.Adapters.HorizontalRecyclerViewDashBoard;
import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.example.articleapp.Activities.DetailActivity;
import com.example.articleapp.Activities.MainActivity;
import com.example.articleapp.Adapters.HorizontalRecyclerViewDashBoard;
import com.example.articleapp.Adapters.VerticalRecyclerViewAdapter;
import com.example.articleapp.Models.Users;
import com.example.articleapp.R;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.auth.User;

import java.util.ArrayList;
import java.util.List;


public class HomeFragment extends Fragment {

    private final String KEY_TITLE = "title";
    private final String KEY_DESCRIPTION = "description";
    private final String KEY_DATE = "date";
    private final String KEY_AUTHOR_PHOTO = "authorphoto";
    private final String KEY_ARTICLE_IMAGE = "articleimage";
    private final String KEY_USERNAME = "username";
    private final String KEY_EMAIL = "email";
    private final String KEY_BIO = "bio";
    private final String KEY_USERID = "userid";
    private final String KEY_POST_KEY = "postKey";


    public RecyclerView horizontalRecyclerView, verticalRecyclerView;

    private List<Users> verticalList;
    private List<Users> horizontalList;
    TextView recentArticles;
    RelativeLayout relativeLayout;


    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    private FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
    private CollectionReference collRef = mFirestore.collection("Users");

    private HorizontalRecyclerViewDashBoard horizontalAdapter;
    private VerticalRecyclerViewAdapter verticalAdapter;
    private VerticalRecyclerViewAdapter searchAdapter;



    String data ;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_home, container, false);

        horizontalRecyclerView = v.findViewById(R.id.horizontal_recyclerView);
        verticalRecyclerView = v.findViewById(R.id.vertical_recyclerView);
        recentArticles = v.findViewById(R.id.recent_articles);
        relativeLayout = v.findViewById(R.id.more_articles);

        recentArticles.setAnimation(AnimationUtils.loadAnimation(getContext(),R.anim.animate_card_enter));
        relativeLayout.setAnimation(AnimationUtils.loadAnimation(getContext(),R.anim.animate_slide_in_left));

        setUpVerticalRecyclerView();

        setUpHorizontalRecyclerView();

        return v;
    }

    private void setUpVerticalRecyclerView() {

        Query query = collRef;

        FirestoreRecyclerOptions<Users> options = new FirestoreRecyclerOptions.Builder<Users>()
                .setQuery(query, Users.class)
                .build();

        verticalAdapter = new VerticalRecyclerViewAdapter(options, getContext());
        verticalRecyclerView.setHasFixedSize(true);

        verticalRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(1,StaggeredGridLayoutManager.VERTICAL));

        verticalRecyclerView.setAdapter(verticalAdapter);

        verticalAdapter.setOnItemClickListener(new VerticalRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {

                Users user = documentSnapshot.toObject(Users.class);
                String title = user.getTitle();
                String description = user.getArticleDetail();
                String date = user.getAddedDate();
                String userPhoto = user.getUserPhoto();
                String articleImage = user.getArticleImage();
                String userName = user.getUserName();
                String email = user.getUserEmail();
                String bio = user.getBiography();
                String userid = user.getUserID();
                String postKey = user.getPostKey();


                Log.d("raju", "onItemClick: "+postKey);

                Intent detailIntent = new Intent(getContext(), DetailActivity.class);
                detailIntent.putExtra(KEY_TITLE,title);
                detailIntent.putExtra(KEY_DESCRIPTION,description);
                detailIntent.putExtra(KEY_DATE,date);
                detailIntent.putExtra(KEY_AUTHOR_PHOTO,userPhoto);
                detailIntent.putExtra(KEY_USERNAME,userName);
                detailIntent.putExtra(KEY_EMAIL,email);
                detailIntent.putExtra(KEY_BIO,bio);
                detailIntent.putExtra(KEY_ARTICLE_IMAGE,articleImage);
                detailIntent.putExtra(KEY_USERID,userid);
                detailIntent.putExtra(KEY_POST_KEY,postKey);
                getContext().startActivity(detailIntent);

                Animatoo.animateInAndOut(getContext());


            }
        });

    }

    private void setUpHorizontalRecyclerView() {

        horizontalList = new ArrayList<>();

        mFirestore.collection("Users").limit(5).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                if (queryDocumentSnapshots != null) {
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        Users user = doc.toObject(Users.class);

                        user.getTitle();
                        user.getUserName();
                        user.getAddedDate();
                        user.getArticleImage();
                        user.getUserPhoto();


                        horizontalList.add(user);
                    }
                } else
                    Toast.makeText(getContext(), "Error: " + e.toString(), Toast.LENGTH_SHORT).show();
            }
        });

        horizontalAdapter = new HorizontalRecyclerViewDashBoard(getContext(), horizontalList);
        horizontalRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.HORIZONTAL));
        horizontalRecyclerView.setAdapter(horizontalAdapter);
    }


    @Override
    public void onStart() {
        super.onStart();
        verticalAdapter.startListening();
    }

}