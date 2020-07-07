package com.example.articleapp.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.example.articleapp.Models.Comments;
import com.example.articleapp.Models.Users;
import com.example.articleapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;


public class AddFragment extends Fragment {

    public static final String SHAREDPREFNAME = "titdesc";
    private static final int INTENT_CODE = 1;
    String UserId;
    private Button next_btn;
    private CircleImageView userPhoto;
    private EditText description_container, title_container;
    private ImageView addPhotoIcon, image_preview;
    private Uri imageUri;
    private FirebaseFirestore mFirestore;
    private FirebaseStorage mStorage;
    private FirebaseAuth mAuth;
    private ImageView saveData;
    private SharedPreferences sp;
    private LottieAnimationView loadingANim;


     String postKey;


    private ImageView articleImage;
    private TextView articleTitle, articleAuthor, articleDate;
    private CircleImageView articleUserImage;

    private RelativeLayout save_and_continue,image_add_icon_container,next_btn_container;
    private ConstraintLayout title_photo_container;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add, container, false);

        save_and_continue = view.findViewById(R.id.save_and_continue);
        image_add_icon_container = view.findViewById(R.id.image_addIcon_container);
        next_btn_container = view.findViewById(R.id.next_btn_container);
        title_photo_container = view.findViewById(R.id.title_photo_container);
        description_container = view.findViewById(R.id.addActivity_detail_input);


        save_and_continue.setAnimation(AnimationUtils.loadAnimation(getContext(),R.anim.animate_slide_left_enter));
        image_add_icon_container.setAnimation(AnimationUtils.loadAnimation(getContext(),R.anim.layout_anim));
        next_btn_container.setAnimation(AnimationUtils.loadAnimation(getContext(),R.anim.animate_split_enter));
        title_photo_container.setAnimation(AnimationUtils.loadAnimation(getContext(),R.anim.animate_card_enter));
        description_container.setAnimation(AnimationUtils.loadAnimation(getContext(),R.anim.animate_card_enter));

        next_btn = view.findViewById(R.id.next_Btn_addAct);
        userPhoto = view.findViewById(R.id.addActivity_userPhoto);
        title_container = view.findViewById(R.id.title_container);
        addPhotoIcon = view.findViewById(R.id.add_image_icon);
        image_preview = view.findViewById(R.id.image_preview);
        saveData = view.findViewById(R.id.sharedPref_Btn);
        loadingANim = view.findViewById(R.id.loadingAnim);


        articleImage = view.findViewById(R.id.articleImage);
        articleTitle = view.findViewById(R.id.articleTitle);
        articleAuthor = view.findViewById(R.id.articlePoster_userName);
        articleDate = view.findViewById(R.id.dateAddedArticle);
        articleUserImage = view.findViewById(R.id.article_Userphoto);


        SharedPreferences sharedPreferences = getActivity().getApplicationContext().getSharedPreferences(SHAREDPREFNAME, Context.MODE_PRIVATE);

        String title = sharedPreferences.getString("title", "");
        String desc = sharedPreferences.getString("description", "");

        title_container.setText(title);
        description_container.setText(desc);

        sp = getActivity().getSharedPreferences(SHAREDPREFNAME, Context.MODE_PRIVATE);


        mFirestore = FirebaseFirestore.getInstance();
        mStorage = FirebaseStorage.getInstance();
        mAuth = FirebaseAuth.getInstance();


        Log.d("raju", "onCreateView: " + mAuth.getCurrentUser().getUid());


        addPhotoIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });


        saveData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String title = title_container.getText().toString();
                String desc = description_container.getText().toString();

                SharedPreferences.Editor editor = sp.edit();

                editor.putString("title", title);
                editor.putString("description", desc);

                editor.commit();

                Toast.makeText(getContext(), "Succesfully saved...You can continue writing later on...", Toast.LENGTH_LONG).show();

            }
        });

        String imgUri = mAuth.getCurrentUser().getPhotoUrl().toString();

        Glide.with(getActivity()).load(imgUri).into(userPhoto);


        next_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postTheArticle();
            }
        });


        return view;



    }


    private void postTheArticle() {

        postTheDetail();

    }





    private void postTheDetail() {

        final String title = title_container.getText().toString();
        final String description = description_container.getText().toString();

        if (title.isEmpty()) {
            Toast.makeText(getContext(), "Please write a title", Toast.LENGTH_SHORT).show();
            return;
        }
        if (description.isEmpty()) {
            Toast.makeText(getContext(), "Please write a description", Toast.LENGTH_SHORT).show();
            return;
        }

        loadingANim.playAnimation();
        loadingANim.setVisibility(View.VISIBLE);
        next_btn.setVisibility(View.GONE);

        String Userid = mAuth.getCurrentUser().getUid();

        Date date = Calendar.getInstance().getTime();

        SimpleDateFormat formatterDate = new SimpleDateFormat("MMM dd, yyyy");

        final String currentDate = formatterDate.format(date);

        if (imageUri != null) {

            StorageReference mStorageRef = FirebaseStorage.getInstance().getReference().child("PostImages");

            final StorageReference imagefilePath = mStorageRef.child(imageUri.getLastPathSegment());

            imagefilePath.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                    if (task.isSuccessful()) {

                        imagefilePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {

                                //this uri containes the post images

                                final String postImage = uri.toString();

                                mFirestore.collection("Users").document(mAuth.getCurrentUser().getUid()).collection("Biography").document(mAuth.getCurrentUser().getUid()).get()
                                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                            @Override
                                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                String bio = (String) documentSnapshot.get("biography");
                                                String articleImg = postImage;


                                                Users user = new Users(title, description, currentDate, postImage, mAuth.getCurrentUser().getDisplayName(), mAuth.getCurrentUser().getPhotoUrl().toString(), mAuth.getCurrentUser().getUid(), mAuth.getCurrentUser().getEmail(), bio,postKey);
                                                addPostForAll(user);

                                            }
                                        });


                            }
                        });

                    } else {
                        Toast.makeText(getContext(), "Error: " + task.getException().toString(), Toast.LENGTH_SHORT).show();
                    }

                }
            });


        } else {

            mFirestore.collection("Users").document(mAuth.getCurrentUser().getUid()).collection("Biography").document(mAuth.getCurrentUser().getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {

                    String bio = (String) documentSnapshot.get("biography");

                    Users user = new Users(title, description, currentDate, "https://i.redd.it/ke7l953tt1g41.png", mAuth.getCurrentUser().getDisplayName(), mAuth.getCurrentUser().getPhotoUrl().toString(), mAuth.getCurrentUser().getUid(), mAuth.getCurrentUser().getEmail(), bio,postKey);

                    addPostForAll(user);

                }
            });

        }

    }

    private void addPostForAll(Users user) {

        FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();

        CollectionReference collRef = mFirestore.collection("Users");

        DocumentReference documentReference = collRef.document();
        postKey = documentReference.getId();

        user.setPostKey(postKey);

        documentReference.set(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        loadingANim.pauseAnimation();
                        loadingANim.setVisibility(View.GONE);
                        next_btn.setVisibility(View.VISIBLE);
                        image_preview.setVisibility(View.GONE);
                        imageUri = null;

                        SharedPreferences.Editor editor = sp.edit();

                        editor.putString("title", "");
                        editor.putString("description", "");

                        editor.commit();

                        Toast.makeText(getContext(), "Article Uploaded...", Toast.LENGTH_SHORT).show();


                        title_container.setText("");
                        description_container.setText("");

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), "Error: " + e.toString(), Toast.LENGTH_SHORT).show();
            }
        });

    }


    private void openGallery() {

        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("*/*");
        startActivityForResult(galleryIntent, INTENT_CODE);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == INTENT_CODE) {
            if (data != null) {

                imageUri = data.getData();

                image_preview.setVisibility(View.VISIBLE);
                image_preview.setImageURI(imageUri);

            }
        }

    }

}