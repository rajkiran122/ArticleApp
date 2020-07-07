package com.example.articleapp.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.articleapp.Models.Users;
import com.example.articleapp.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.auth.User;

public class ClickProfileRecyclerViewAdapter extends FirestoreRecyclerAdapter<Users, ClickProfileRecyclerViewAdapter.MyViewHolder> {

    Context mContext;
    ClickProfileRecyclerViewAdapter.OnClickItemListener listener;


    public ClickProfileRecyclerViewAdapter(@NonNull FirestoreRecyclerOptions<Users> options, Context mContext) {
        super(options);
        this.mContext = mContext;
    }

    @Override
    protected void onBindViewHolder(@NonNull MyViewHolder holder, int position, @NonNull Users model) {

        holder.title.setText(model.getTitle());
        holder.date.setText(model.getAddedDate());

        Log.d("raju", "onBindViewHolder: "+model.getTitle());

        Glide.with(mContext).load(model.getArticleImage()).into(holder.articleImage);

        holder.cardView.setAnimation(AnimationUtils.loadAnimation(mContext,R.anim.animate_slide_up_enter));

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(mContext).inflate(R.layout.my_posts_in_profile_fragment, parent, false);

        return new MyViewHolder(v);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView articleImage;
        TextView title;
        TextView date;
        CardView cardView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            articleImage = itemView.findViewById(R.id.profile_image);
            title = itemView.findViewById(R.id.profile_title);
            date = itemView.findViewById(R.id.profile_date);
            cardView = itemView.findViewById(R.id.cardview_container);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();

                    if (pos!=RecyclerView.NO_POSITION&&listener!=null){
                        listener.OnClickItem(getSnapshots().getSnapshot(pos),pos);
                    }
                }
            });

        }
    }

    public void setOnClickItemListener(ClickProfileRecyclerViewAdapter.OnClickItemListener listener){
        this.listener = listener;
    }

    public interface OnClickItemListener{
        void OnClickItem(DocumentSnapshot documentSnapshot,int position);
    }

}
