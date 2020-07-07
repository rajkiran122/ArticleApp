package com.example.articleapp.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.articleapp.Adapters.NotificationAdapter;
import com.example.articleapp.Models.Users;
import com.example.articleapp.R;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.auth.User;


public class SettingsFragment extends Fragment {

    RecyclerView notification_recycleview;
    FirebaseFirestore mFirestore;
    NotificationAdapter adapter;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_settings, container, false);

        notification_recycleview = v.findViewById(R.id.notification_recyclerview);

        mFirestore = FirebaseFirestore.getInstance();

        setUpRecyclerView();

        return v;
    }

    private void setUpRecyclerView() {

        Query query = mFirestore.collection("Users").orderBy("addedDate", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<Users> options = new FirestoreRecyclerOptions.Builder<Users>()
                .setQuery(query,Users.class)
                .build();

        adapter = new NotificationAdapter(options,getContext());
        notification_recycleview.setLayoutManager(new LinearLayoutManager(getContext()));
        notification_recycleview.setAdapter(adapter);
        adapter.notifyDataSetChanged();

    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}