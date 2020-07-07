package com.example.articleapp.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.articleapp.Fragments.AddFragment;
import com.example.articleapp.Fragments.HomeFragment;
import com.example.articleapp.Fragments.LogoutFragment;
import com.example.articleapp.Fragments.ProfileFragment;
import com.example.articleapp.Fragments.SettingsFragment;
import com.example.articleapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    private long backPressedTime;
    private Toast backToast;
    private ChipNavigationBar bottom_navigation;
    private FragmentManager fragmentManager;

    private TextView userName;
    private CircleImageView userPhoto;


    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP){
            this.getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        setContentView(R.layout.activity_main);


        mAuth = FirebaseAuth.getInstance();

        currentUser = mAuth.getCurrentUser();

        Views();

        bottom_navigation.setOnItemSelectedListener(new ChipNavigationBar.OnItemSelectedListener() {
            @Override
            public void onItemSelected(int id) {
                replaceFragment(id);
            }
        });

        if (savedInstanceState==null){
            bottom_navigation.setItemSelected(R.id.home_icon,true);

            fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.fragment_container,new HomeFragment()).commit();
        }

        userPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.fragment_container,new ProfileFragment()).commit();
                bottom_navigation.setItemSelected(R.id.profile_icon,true);
            }
        });

    }

    private void replaceFragment(int id) {

        Fragment fragment = null;
        switch (id){

            case R.id.home_icon:
                fragment = new HomeFragment();
                break;

            case R.id.profile_icon:
                fragment = new ProfileFragment();
                break;

            case R.id.add_icon:
                fragment = new AddFragment();
                break;

            case R.id.notification_icon:
                fragment = new SettingsFragment();
                break;

            case R.id.logout_icon:
                fragment = new LogoutFragment();
                break;

        }

        if (fragment!=null){

            fragmentManager = getSupportFragmentManager();

            fragmentManager.beginTransaction().replace(R.id.fragment_container,fragment).commit();

        }else {
            Log.d("dbArticle", "Fragment is null");
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (currentUser == null){
            startActivity(new Intent(MainActivity.this,LoginActivity.class));
        }

        if (currentUser!=null){
            Glide.with(this).load(currentUser.getPhotoUrl()).into(userPhoto);
            userName.setText("Hi, "+currentUser.getDisplayName());
        }

    }

    @Override
    public void onBackPressed() {

        if (backPressedTime +2000>System.currentTimeMillis()){
            backToast.cancel();
            super.onBackPressed();
            moveTaskToBack(true);
            return;
        }else {
           backToast=  Toast.makeText(this, "Press back again to exit", Toast.LENGTH_SHORT);
            backToast.show();
        }

        backPressedTime = System.currentTimeMillis();

    }

    private void Views(){
        bottom_navigation = findViewById(R.id.bottom_nav);
        userName = findViewById(R.id.userName_mainAct);
        userPhoto = findViewById(R.id.userPhoto);
    }

}