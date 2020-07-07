package com.example.articleapp.Fragments;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.bumptech.glide.Glide;
import com.example.articleapp.Activities.ClickSelfActivity;
import com.example.articleapp.Activities.DetailActivity;
import com.example.articleapp.Adapters.ProfileRecyclerViewAdapter;
import com.example.articleapp.Adapters.VerticalRecyclerViewAdapter;
import com.example.articleapp.Models.Users;
import com.example.articleapp.R;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;


public class ProfileFragment extends Fragment {

    private static final int INTENT_CODE = 1;
    BottomSheetDialog bottomSheetDialog;
    private CircleImageView user_photo;
    private TextView userName, userEmail, bioDetail, disclaiMer, profile_userName_Head, profile_Biography;
    private ProgressBar progressBar;
    private EditText update_Input_Name, detail_BottomSheet_Bio;
    private ImageView ok_Btn_Name, cancel_btn_Name, edit_Btn_Name, edit_Biography;
    private FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    private CollectionReference collRef;
    private FirebaseAuth mAuth;
    private FirebaseStorage mStorage;
    private FirebaseFirestore mFirestore;
    private Uri imageUri;

    private ProfileRecyclerViewAdapter recyclerViewAdapter;

    private RecyclerView recyclerView;

    private RelativeLayout profile_image_container,myposts_text;
    private ConstraintLayout username_container,email_container,bio_container;


    private final String KEY_TITLE = "title";
    private final String KEY_DESCRIPTION = "description";
    private final String KEY_DATE = "date";
    private final String KEY_AUTHOR_PHOTO = "authorphoto";
    private final String KEY_ARTICLE_IMAGE = "articleimage";
    private final String KEY_POST_KEY = "postKey";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);


        profile_image_container = view.findViewById(R.id.profile_image_container);
        myposts_text = view.findViewById(R.id.myposts_text);
        username_container = view.findViewById(R.id.username_container);
        email_container = view.findViewById(R.id.email_container);
        bio_container = view.findViewById(R.id.bio_container);

        profile_image_container.setAnimation(AnimationUtils.loadAnimation(getContext(),R.anim.animate_slide_down_enter));
        myposts_text.setAnimation(AnimationUtils.loadAnimation(getContext(),R.anim.animate_card_enter));
        username_container.setAnimation(AnimationUtils.loadAnimation(getContext(),R.anim.animate_slide_out_right));
        email_container.setAnimation(AnimationUtils.loadAnimation(getContext(),R.anim.animate_card_enter));
        bio_container.setAnimation(AnimationUtils.loadAnimation(getContext(),R.anim.animate_slide_out_right));

        user_photo = view.findViewById(R.id.user_photo_Profile);
        userName = view.findViewById(R.id.profileAct_userName);
        userEmail = view.findViewById(R.id.profileAct_email);
        bioDetail = view.findViewById(R.id.profileAct_Biography);
        progressBar = view.findViewById(R.id.progressBar);
        disclaiMer = view.findViewById(R.id.disclaimer_progressBar);
        update_Input_Name = view.findViewById(R.id.update_Name_Input);
        ok_Btn_Name = view.findViewById(R.id.profile_edit_ok_img);
        cancel_btn_Name = view.findViewById(R.id.profile_edit_cancel_img);
        profile_userName_Head = view.findViewById(R.id.profile_userName_Head);
        edit_Btn_Name = view.findViewById(R.id.edit_userName);
        profile_Biography = view.findViewById(R.id.profileAct_Biography);
        edit_Biography = view.findViewById(R.id.edit_Biography);


        mAuth = FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser().getPhotoUrl() != null) {

            String imageUri = mAuth.getCurrentUser().getPhotoUrl().toString();
            imageUri = imageUri + "?type=large";

            Glide.with(getActivity()).load(imageUri).into(user_photo);
        }
        try {
            userName.setText(currentUser.getDisplayName());
        }catch (Exception e){
        }
        if (!currentUser.getEmail().isEmpty()) {
            userEmail.setText(currentUser.getEmail());
        }else {
            userEmail.setText("No Email Registered Found");
        }

        user_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        mStorage = FirebaseStorage.getInstance();

        mFirestore = FirebaseFirestore.getInstance();

        collRef = mFirestore.collection("Users");


        edit_Btn_Name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showUpdateName();
            }
        });

        cancel_btn_Name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideUpdateName();
            }
        });


        ok_Btn_Name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                updateUsername();

            }
        });

        edit_Biography.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBottomUpdateBioDialog();
            }
        });


        recyclerView = view.findViewById(R.id.myposts_RecyclerView);

        setUpRecyclerView();

        return view;
    }

    private void setUpRecyclerView() {

        CollectionReference collRef = mFirestore.collection("Users");

        Query query = collRef.whereEqualTo("userID", mAuth.getCurrentUser().getUid())
                .orderBy("addedDate", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<Users> options = new FirestoreRecyclerOptions.Builder<Users>()
                .setQuery(query, Users.class)
                .build();

        recyclerViewAdapter = new ProfileRecyclerViewAdapter(options, getContext());

        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));

        recyclerView.setAdapter(recyclerViewAdapter);

        recyclerViewAdapter.setOnItemClickListener(new ProfileRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onClickItem(DocumentSnapshot documentSnapshot, int position) {

                Users user = documentSnapshot.toObject(Users.class);
                String title = user.getTitle();
                String description = user.getArticleDetail();
                String date = user.getAddedDate();
                String userPhoto = user.getUserPhoto();
                String articleImage = user.getArticleImage();
                String postKey = user.getPostKey();

                Intent detailIntent = new Intent(getContext(), ClickSelfActivity.class);
                detailIntent.putExtra(KEY_TITLE,title);
                detailIntent.putExtra(KEY_DESCRIPTION,description);
                detailIntent.putExtra(KEY_DATE,date);
                detailIntent.putExtra(KEY_AUTHOR_PHOTO,userPhoto);
                detailIntent.putExtra(KEY_ARTICLE_IMAGE,articleImage);
                detailIntent.putExtra(KEY_POST_KEY,postKey);
                getContext().startActivity(detailIntent);

                Animatoo.animateSplit(getContext());

            }
        });

        recyclerViewAdapter.notifyDataSetChanged();



    }

    private void showBottomUpdateBioDialog() {

        bottomSheetDialog = new BottomSheetDialog(getActivity(), R.style.BottomSheetDialogTheme);

        View bottomSheetView = LayoutInflater.from(getContext()).inflate(R.layout.bottom_sheet_edit_bio_dialog, null);

        detail_BottomSheet_Bio = bottomSheetView.findViewById(R.id.bio_detail_edittext);

        bottomSheetView.findViewById(R.id.updateBio_Btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                updateBiography();

            }
        });

        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();

    }

    private void updateBiography() {

        String updatedBio = detail_BottomSheet_Bio.getText().toString();

        if (updatedBio.isEmpty()) {
            Toast.makeText(getContext(), "Please update your bio", Toast.LENGTH_SHORT).show();
            return;
        }

        Users user = new Users(updatedBio);

        String userId = mAuth.getCurrentUser().getUid();

        collRef.document(userId).collection("Biography").document(userId).set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(getContext(), "Bio updated successfully", Toast.LENGTH_SHORT).show();
                    displayBioInProfile();
                } else {
                    Toast.makeText(getContext(), "Error: " + task.getException().toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    private void displayBioInProfile() {

        String userId = mAuth.getCurrentUser().getUid();

        collRef.document(userId).collection("Biography").document(userId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                String biography = (String) documentSnapshot.get("biography");

                profile_Biography.setText(biography);

                bottomSheetDialog.dismiss();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), "Error: " + e.toString(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();

        String userId = mAuth.getCurrentUser().getUid();

        collRef.document(userId).collection("Biography").document(userId).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {

                String bio = (String) documentSnapshot.get("biography");

                profile_Biography.setText(bio);

            }
        });

        recyclerViewAdapter.startListening();

    }

    private void updateUsername() {

        String updatedUsername = update_Input_Name.getText().toString();

        if (updatedUsername.isEmpty()) {
            Toast.makeText(getContext(), "Please enter a new username", Toast.LENGTH_SHORT).show();
            return;
        }

        UserProfileChangeRequest updateName = new UserProfileChangeRequest.Builder()
                .setDisplayName(updatedUsername)
                .build();

        mAuth.getCurrentUser().updateProfile(updateName).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(getContext(), "Username changed successfully", Toast.LENGTH_SHORT).show();

                    hideUpdateName();

                    update_Input_Name.setText("");

                    refreshItems();

                } else {
                    Toast.makeText(getContext(), "Error: " + task.getException().toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    private void refreshItems() {
        userName.setText(currentUser.getDisplayName());
    }

    private void showUpdateName() {

        profile_userName_Head.animate().alpha(0).setDuration(500);
        userName.animate().alpha(0).setDuration(500);
        edit_Btn_Name.animate().alpha(0).setDuration(500);

        profile_userName_Head.setVisibility(View.GONE);
        userName.setVisibility(View.GONE);
        edit_Btn_Name.setVisibility(View.GONE);

        update_Input_Name.animate().alpha(1).setDuration(500);
        ok_Btn_Name.animate().alpha(1).setDuration(500);
        cancel_btn_Name.animate().alpha(1).setDuration(500);

        update_Input_Name.setVisibility(View.VISIBLE);
        ok_Btn_Name.setVisibility(View.VISIBLE);
        cancel_btn_Name.setVisibility(View.VISIBLE);

    }

    private void hideUpdateName() {

        profile_userName_Head.animate().alpha(1).setDuration(500);
        userName.animate().alpha(1).setDuration(500);
        edit_Btn_Name.animate().alpha(1).setDuration(500);

        profile_userName_Head.setVisibility(View.VISIBLE);
        userName.setVisibility(View.VISIBLE);
        edit_Btn_Name.setVisibility(View.VISIBLE);

        update_Input_Name.animate().alpha(0).setDuration(500);
        ok_Btn_Name.animate().alpha(0).setDuration(500);
        cancel_btn_Name.animate().alpha(0).setDuration(500);

        update_Input_Name.setVisibility(View.GONE);
        ok_Btn_Name.setVisibility(View.GONE);
        cancel_btn_Name.setVisibility(View.GONE);

        update_Input_Name.setText("");

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

                updateProfilePicture(imageUri);

            }

        }

    }

    private void updateProfilePicture(Uri imageUri) {

        progressBar.setVisibility(View.VISIBLE);
        disclaiMer.setVisibility(View.VISIBLE);

        String userId = mAuth.getCurrentUser().getUid();

        StorageReference user_photo_Ref = mStorage.getReference().child(userId).child("user_photos");
        final StorageReference imageFilePath = user_photo_Ref.child(imageUri.getLastPathSegment());

        imageFilePath.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {

                    imageFilePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(final Uri uri) {

                            //uri contains the photo

                            UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder()
                                    .setPhotoUri(uri)
                                    .build();

                            mAuth.getCurrentUser().updateProfile(profileUpdate)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {

                                            disclaiMer.setVisibility(View.GONE);
                                            progressBar.setVisibility(View.GONE);

                                            Toast.makeText(getContext(), "Profile picture updated", Toast.LENGTH_SHORT).show();

                                            user_photo.setImageURI(uri);

                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                    disclaiMer.setVisibility(View.GONE);
                                    progressBar.setVisibility(View.GONE);

                                    Toast.makeText(getActivity(), "Error: " + e.toString(), Toast.LENGTH_SHORT).show();
                                }
                            });

                        }
                    });

                } else {
                    Toast.makeText(getActivity(), "Error: " + task.getException().toString(), Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                double progress = (100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                progressBar.setProgress((int) progress);
            }
        });

    }


    @Override
    public void onResume() {
        super.onResume();
        if (imageUri != null) {
            updateProfilePicture(imageUri);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        recyclerViewAdapter.stopListening();
    }
}