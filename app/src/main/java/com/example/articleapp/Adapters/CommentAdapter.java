package com.example.articleapp.Adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.articleapp.Models.Comments;
import com.example.articleapp.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentAdapter extends FirestoreRecyclerAdapter<Comments, CommentAdapter.MyViewHolder> {

    static FirestoreRecyclerOptions<Comments> options;
    Context mContext;

    public CommentAdapter(@NonNull FirestoreRecyclerOptions<Comments> options, Context mContext) {
        super(options);
        this.options = options;
        this.mContext = mContext;
    }

    @Override
    protected void onBindViewHolder(@NonNull MyViewHolder holder, int position, @NonNull Comments model) {

        holder.username.setText(model.getUserName());
        holder.content.setText(model.getContent());
        holder.date.setText(model.getPostedDate() + "\n" + model.getPostedTime());

        Glide.with(mContext).load(model.getUserImg()).into(holder.userphoto);

        holder.comment_container.setAnimation(AnimationUtils.loadAnimation(mContext, R.anim.layout_anim));
        holder.userphoto.setAnimation(AnimationUtils.loadAnimation(mContext, R.anim.animate_slide_in_left));
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(mContext).inflate(R.layout.comment_layout, parent, false);

        return new MyViewHolder(v);
    }

    private void openDeleteDialog(int pos) {

        final int position = pos;

        new AlertDialog.Builder(mContext)
                .setTitle("Delete this comment?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        getSnapshots().getSnapshot(position).getReference().delete();
                        Toast.makeText(mContext, "Comment Deleted...", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create().show();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView content, date, username;
        CardView cardView;
        CircleImageView userphoto;
        ConstraintLayout comment_container;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            content = itemView.findViewById(R.id.comment_content);
            date = itemView.findViewById(R.id.comment_date);
            username = itemView.findViewById(R.id.comment_username);
            cardView = itemView.findViewById(R.id.comment_cardview);
            userphoto = itemView.findViewById(R.id.comment_userphoto);
            comment_container = itemView.findViewById(R.id.comment_container);

            int total = itemView.getScrollBarSize();

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                    int pos = getAdapterPosition();

                    openDeleteDialog(pos);

                    return true;
                }
            });

        }

    }

}
