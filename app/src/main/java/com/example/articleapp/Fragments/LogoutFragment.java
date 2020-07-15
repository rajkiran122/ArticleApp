package com.example.articleapp.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.se.omapi.Session;
import android.service.textservice.SpellCheckerService;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.airbnb.lottie.LottieAnimationView;
import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.example.articleapp.Activities.LoginActivity;
import com.example.articleapp.Activities.MainActivity;
import com.example.articleapp.R;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class LogoutFragment extends Fragment {

    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser currentUser = mAuth.getCurrentUser();
    AccessTokenTracker accessTokenTracker;
    private LottieAnimationView yes_btn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_logout, container, false);

        yes_btn = v.findViewById(R.id.yes_btn_logout);


        yes_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                yes_btn.playAnimation();

                new android.os.Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        startActivity(new Intent(getContext(), LoginActivity.class));
                        Animatoo.animateSlideUp(getContext());
                        getActivity().finish();

                        mAuth.signOut();

                        LoginManager.getInstance().logOut();
                    }
                }, 600);

            }
        });

        return v;
    }

    @Override
    public void onStart() {
        super.onStart();

        if (currentUser == null) {
            startActivity(new Intent(getContext(), LoginActivity.class));
        }
    }
}