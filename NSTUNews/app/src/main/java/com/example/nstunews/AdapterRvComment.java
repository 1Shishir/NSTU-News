package com.example.nstunews;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AdapterRvComment extends FirebaseRecyclerAdapter<dataComment,viewHolderCmnt> {
    Context context;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    String uid = FirebaseAuth.getInstance().getUid();
    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public AdapterRvComment(@NonNull FirebaseRecyclerOptions<dataComment> options,Context context) {
        super(options);
        this.context=context;
    }

    @Override
    protected void onBindViewHolder(@NonNull viewHolderCmnt holder, int position, @NonNull dataComment model) {

        holder.comment.setText(model.comment);
        DatabaseReference ref = database.getReference("profile").child(model.uid).child("profile");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    holder.profileName.setText(String.valueOf(snapshot.child("lastName").getValue()));
                    Glide.with(context).load(String.valueOf(snapshot.child("profileImage").getValue())).circleCrop().into(holder.profilePic);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });




    }

    @NonNull
    @Override
    public viewHolderCmnt onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem= layoutInflater.inflate(R.layout.rv_item_comment, parent, false);
        viewHolderCmnt viewHolderCmnt= new viewHolderCmnt(listItem);
        return viewHolderCmnt;
    }
}

class viewHolderCmnt extends RecyclerView.ViewHolder{

    ImageView profilePic;
    TextView profileName;
    TextView comment;

    public viewHolderCmnt(@NonNull View itemView) {
        super(itemView);

        profilePic=itemView.findViewById(R.id.commentProfilepic);
        profileName=itemView.findViewById(R.id.commentProfileName);
        comment=itemView.findViewById(R.id.commentTxt);

    }
}
