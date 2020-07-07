package com.example.articleapp.Adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.articleapp.Models.Users;
import com.example.articleapp.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;

import org.w3c.dom.Text;

public class ProfileRecyclerViewAdapter extends FirestoreRecyclerAdapter<Users, ProfileRecyclerViewAdapter.MyViewHolder> {

    OnItemClickListener listener;

    Context mContext;

    public ProfileRecyclerViewAdapter(@NonNull FirestoreRecyclerOptions<Users> options, Context mContext) {
        super(options);

        this.mContext = mContext;
    }

    @Override
    protected void onBindViewHolder(@NonNull MyViewHolder holder, int position, @NonNull Users model) {

        holder.date.setText("-" + model.getAddedDate());
        holder.title.setText(model.getTitle());

        Glide.with(mContext).load(model.getArticleImageUri()).into(holder.articleImage);

        holder.cardView.setAnimation(AnimationUtils.loadAnimation(mContext,R.anim.layout_anim));

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(mContext).inflate(R.layout.my_posts_in_profile_fragment, parent, false);

        return new MyViewHolder(v);
    }

    private void deleteItem(int adapterPosition) {

        final int position = adapterPosition;

        new AlertDialog.Builder(mContext)
                .setTitle("Are You Sure To Delete This Masterpiece of Yours?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        getSnapshots().getSnapshot(position).getReference().delete();
                        Toast.makeText(mContext, "Article Deleted...", Toast.LENGTH_SHORT).show();

                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create().show();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public interface OnItemClickListener {
        void onClickItem(DocumentSnapshot documentSnapshot, int position);
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

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                    deleteItem(getAdapterPosition());

                    return true;
                }
            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    if (pos!=RecyclerView.NO_POSITION&&listener!=null){
                        listener.onClickItem(getSnapshots().getSnapshot(pos),pos);
                    }
                }
            });


        }
    }

}
