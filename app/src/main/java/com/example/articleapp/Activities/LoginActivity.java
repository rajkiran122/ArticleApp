package com.example.articleapp.Activities;

import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.example.articleapp.R;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import org.w3c.dom.Text;

public class LoginActivity extends AppCompatActivity {

    private static final int INTENT_CODE = 1;
    private final String TAG = "dbArticulo";
    public ImageView google_signIn_btn;
    Button login_Btn, register_Btn;
    private LottieAnimationView loadingAnimation;
    private TextInputLayout email_edittext, password_edittext;
    private FirebaseAuth mAuth;
    private FirebaseStorage mStorage;
    private FirebaseFirestore mFirestore;
    private FirebaseUser currentUser;
    private long backPressedTime;
    private Toast backToast;
    private LoginButton facebook_sign_in;

    private GoogleSignInClient googleSignInClient;

    private CallbackManager mCallbackManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Window w = getWindow();

        w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            w.setStatusBarColor(Color.TRANSPARENT);
        }

        setContentView(R.layout.activity_login);

        Views();

        //for rotating the image_logo
        findViewById(R.id.image_logo).animate().rotationY(36000).setDuration(600000);

        mAuth = FirebaseAuth.getInstance();
        FacebookSdk.sdkInitialize(this);

        currentUser = mAuth.getCurrentUser();

        login_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInUser(v);
            }
        });

        register_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));

                Animatoo.animateSlideDown(LoginActivity.this);

            }
        });

        googlePres();

        google_signIn_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openConsentScreen();
            }
        });

        mCallbackManager = CallbackManager.Factory.create();

        facebook_sign_in.setReadPermissions("email","public_profile");

        facebookSignInFirst();

        updateUIFacebook(mAuth.getCurrentUser());
    }

    private void facebookSignInFirst() {

        facebook_sign_in.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                handleFacebookSignIn(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });
    }

    private void handleFacebookSignIn(AccessToken accessToken) {

        AuthCredential credential = FacebookAuthProvider.getCredential(accessToken.getToken());

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                            updateUIFacebook(mAuth.getCurrentUser());
                        } else {
                            Toast.makeText(LoginActivity.this, "Error: " + task.getException().toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    private void updateUIFacebook(FirebaseUser currentUser) {

        if (currentUser != null) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            Animatoo.animateSlideDown(this);
            finish();
        }

    }

    private void googlePres() {

        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestProfile()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);

        updateUIGoogle(mAuth.getCurrentUser());

    }

    public void openConsentScreen() {

        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, INTENT_CODE);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == INTENT_CODE && data != null) {
            Task<GoogleSignInAccount> accountTask = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleGoogleSignIn(accountTask);
        }
    }

    public void handleGoogleSignIn(Task<GoogleSignInAccount> accountTask) {

        try {
            GoogleSignInAccount account = accountTask.getResult(ApiException.class);

            firebaseAuthWithGoogle(account);

        } catch (ApiException e) {
            e.printStackTrace();
        }

    }

    public void firebaseAuthWithGoogle(GoogleSignInAccount account) {

        AuthCredential authCredential = GoogleAuthProvider.getCredential(account.getIdToken(), null);

        mAuth.signInWithCredential(authCredential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                            updateUIGoogle(mAuth.getCurrentUser());
                        }
                    }
                });

    }

    public void updateUIGoogle(FirebaseUser account) {
        if (account != null) {
            startActivity(new Intent(this, MainActivity.class));
            Animatoo.animateSlideDown(this);
            finish();
        }
    }

    private void signInUser(View v) {


        String email = email_edittext.getEditText().getText().toString().trim();
        String password = password_edittext.getEditText().getText().toString().trim();

//        if (!email.isEmpty() && !password.isEmpty()) {

        if (TextUtils.isEmpty(email) && TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please enter both email and password", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Please enter your email", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Enter the valid email address", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please enter your password", Toast.LENGTH_SHORT).show();
            return;
        }

        login_Btn.animate().alpha(0).setDuration(200);

        loadingAnimation.animate().alpha(1).setDuration(200);

        loadingAnimation.playAnimation();

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    loadingAnimation.animate().alpha(0).setDuration(200);
                    login_Btn.animate().alpha(1).setDuration(200);


                    Toast.makeText(LoginActivity.this, "Login Successful...", Toast.LENGTH_SHORT).show();

                } else {
                    loadingAnimation.animate().alpha(0).setDuration(200);
                    login_Btn.animate().alpha(1).setDuration(200);

                    if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                        Toast.makeText(LoginActivity.this, "Password doesn't match", Toast.LENGTH_SHORT).show();
                    } else if (task.getException() instanceof FirebaseAuthInvalidUserException) {
                        Toast.makeText(LoginActivity.this, "User doesn't exist", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });


    }

    private void Views() {

        loadingAnimation = findViewById(R.id.loading_anim);
        login_Btn = findViewById(R.id.login_btn);
        register_Btn = findViewById(R.id.login_register_btn);
        email_edittext = findViewById(R.id.login_edittext_email);
        password_edittext = findViewById(R.id.login_edittext_password);
        google_signIn_btn = findViewById(R.id.google_login_btn);
        facebook_sign_in = findViewById(R.id.facebook_login);

    }

    @Override
    protected void onStart() {
        super.onStart();

        if (currentUser != null) {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }

    }

    @Override
    public void onBackPressed() {

        if (backPressedTime + 2000 > System.currentTimeMillis()) {
            backToast.cancel();
            super.onBackPressed();
            moveTaskToBack(true);
            return;
        } else {
            backToast = Toast.makeText(this, "Press back again to exit", Toast.LENGTH_SHORT);
            backToast.show();
        }

        backPressedTime = System.currentTimeMillis();

    }

}
