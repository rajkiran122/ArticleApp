package com.example.articleapp.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.example.articleapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.IOException;
import java.sql.Ref;

import de.hdodenhof.circleimageview.CircleImageView;

public class RegisterActivity extends AppCompatActivity {

    private static final int REQUEST_CODE = 1;
    private static final int INTENT_CODE = 2;
    private LottieAnimationView loadingAnim;
    private Button register_btn;
    private CircleImageView register_user_photo;

    private TextInputLayout email_input, username_input, password_input, confirmPassword_input;
    private Uri imageUri;

    private FirebaseAuth mAuth;
    private FirebaseStorage mStorage;
    private FirebaseFirestore mFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Window w = getWindow();
        w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            w.setStatusBarColor(Color.TRANSPARENT);
        }

        setContentView(R.layout.activity_register);

        Views();

        register_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                createUser();

            }
        });

        register_user_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImage();
            }
        });

        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();
        mStorage = FirebaseStorage.getInstance();


    }

    private void uploadImage() {

        if (Build.VERSION.SDK_INT >= 22) {
            checkRequestForPermission();
        } else {
            openGallery();
        }

    }


    private void checkRequestForPermission() {

        if (ContextCompat.checkSelfPermission(RegisterActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            openGallery();
        } else {
            requestStoragePermissions();
        }
    }


    private void requestStoragePermissions() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(RegisterActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {

            new AlertDialog.Builder(this)
                    .setTitle("Permission needed")
                    .setMessage("You have to allow this to access images")
                    .setPositiveButton("Allow", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(RegisterActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE);
                        }
                    }).setNegativeButton("Deny", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            })
                    .create().show();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE);
        }

    }

    private void openGallery() {

//        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);

        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("*/*");
        startActivityForResult(galleryIntent, INTENT_CODE);


    }

    private void cropRequest(Uri imgUri) {

        CropImage.activity(imgUri)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setMultiTouchEnabled(true)
                .start(this);

    }

    private void createUser() {

        final String username = username_input.getEditText().getText().toString().trim();
        String email = email_input.getEditText().getText().toString().trim();
        String password = password_input.getEditText().getText().toString().trim();
        String confirmPassword = confirmPassword_input.getEditText().getText().toString().trim();

        if (username.isEmpty() & email.isEmpty() & password.isEmpty() & confirmPassword.isEmpty()) {
            Toast.makeText(this, "Please fill up all the details..", Toast.LENGTH_SHORT).show();
            return;
        }
        if (username.isEmpty()) {
            Toast.makeText(this, "Please enter new username", Toast.LENGTH_SHORT).show();
            return;
        }
        if (email.isEmpty()) {
            Toast.makeText(this, "Please enter email address", Toast.LENGTH_SHORT).show();
            return;
        }
        if (password.isEmpty()) {
            Toast.makeText(this, "Please enter a password", Toast.LENGTH_SHORT).show();
            return;
        }
        if (password.length() < 6) {
            Toast.makeText(this, "Please enter password with more than 6 digits", Toast.LENGTH_SHORT).show();
            return;
        }
        if (confirmPassword.isEmpty()) {
            Toast.makeText(this, "Please confirm the password", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Please enter email in the correct format or Check your Internet Connection..", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Password doesn't match with each other", Toast.LENGTH_SHORT).show();
            return;
        }
        if (imageUri == null){
            Toast.makeText(this, "Please upload the profile picture", Toast.LENGTH_SHORT).show();
            return;
        }


        register_btn.animate().alpha(0).setDuration(200);

        loadingAnim.animate().alpha(1).setDuration(200);

        loadingAnim.playAnimation();

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {



                    Toast.makeText(RegisterActivity.this, "User Created Successfully...Logging in...Hold on for about 20 secs", Toast.LENGTH_SHORT).show();

                    updateInfo(username, imageUri, mAuth.getCurrentUser());


                } else {
                    register_btn.animate().alpha(1).setDuration(200);
                    loadingAnim.animate().alpha(0).setDuration(200);

                    if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                        Toast.makeText(RegisterActivity.this, "Email already in use", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

    }

    private void updateInfo(final String username, final Uri imageUri, FirebaseUser currentUser) {

        String userId = mAuth.getCurrentUser().getUid();

        StorageReference user_photo_Ref = mStorage.getReference().child(userId).child("user_photos");
        final StorageReference imageFilePath = user_photo_Ref.child(imageUri.getLastPathSegment());

        imageFilePath.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {
                    imageFilePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            //this uri contains the uri of the photo

                            UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(username)
                                    .setPhotoUri(uri)
                                    .build();

                            mAuth.getCurrentUser().updateProfile(profileUpdate)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {

                                                register_btn.animate().alpha(1).setDuration(200);
                                                loadingAnim.animate().alpha(0).setDuration(200);

                                                Toast.makeText(RegisterActivity.this, "Registered Successfully", Toast.LENGTH_SHORT).show();
                                                updateUi();
                                            }else {
                                                Toast.makeText(RegisterActivity.this, "Error: "+task.getException().toString(), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        }
                    });
                }else {
                    Toast.makeText(RegisterActivity.this, "Error: "+ task.getException().toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == INTENT_CODE) {
            if (data != null) {

                imageUri = data.getData();

                register_user_photo.setImageURI(imageUri);

            } else {
                Toast.makeText(this, "Upload the profile picture", Toast.LENGTH_SHORT).show();
            }
        }


    }

    private void updateUi() {
        startActivity(new Intent(RegisterActivity.this, MainActivity.class));
        Animatoo.animateSlideDown(RegisterActivity.this);
        finish();
    }

    private void Views() {
        loadingAnim = findViewById(R.id.loading_anim);
        register_btn = findViewById(R.id.register_btn_act);
        email_input = findViewById(R.id.edittext_email_register);
        username_input = findViewById(R.id.edittext_username_register);
        password_input = findViewById(R.id.edittext_password_register);
        confirmPassword_input = findViewById(R.id.edittext_confirm_pass_register);
        register_user_photo = findViewById(R.id.register_user_photo);
    }

}