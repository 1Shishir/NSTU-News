package com.example.nstunews;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
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

import java.util.ArrayList;

public class AdapterRvCategoryNews extends FirebaseRecyclerAdapter<dataNews, ViewHolderTrends> {
    Context context;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    String uid = FirebaseAuth.getInstance().getUid();
    CSV csv=new CSV();

    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public AdapterRvCategoryNews(@NonNull FirebaseRecyclerOptions<dataNews> options, Context context) {
        super(options);
        this.context=context;
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolderTrends holder, int position, @NonNull dataNews model) {
        String reactCount ="0";
        final boolean[] react = {false};
        String commentCount="0";

        String headLine=model.headline;
        String newsImg=model.newsPic;
        String newsBody=model.body;
        String category = model.category;
        String newsId = model.newsId;
        String reporterUid=model.owner_uid;

        //
        DatabaseReference ref = database.getReference("profile").child(reporterUid).child("profile");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    holder.name.setText(String.valueOf(snapshot.child("lastName").getValue()));
                    Glide.with(context).load(String.valueOf(snapshot.child("profileImage").getValue())).into(holder.profileImg);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //
        holder.newsHead.setText(headLine);
        Glide.with(context).load(newsImg).into(holder.newsImg);


        DatabaseReference ref1 = database.getReference("category").child(category).child("news").child(newsId).child("react").child(uid);
        ref1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    react[0]=true;
                    holder.love.setImageResource(R.drawable.love_shape_fill);
                }
                else {
                    react[0]=false;
                    holder.love.setImageResource(R.drawable.love_shape);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        DatabaseReference reactC = database.getReference("category").child(category).child("news").child(newsId).child("react");
        reactC.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    holder.loveCount.setText(String.valueOf(snapshot.getChildrenCount()));
                }
                else {
                    holder.loveCount.setText("0");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        DatabaseReference commentC = database.getReference("category").child(category).child("news").child(newsId).child("comment");
        commentC.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    holder.cmntCount.setText(String.valueOf(snapshot.getChildrenCount()));
                }
                else {
                    holder.cmntCount.setText("0");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


      //  holder.loveCount.setText(reactCount);
        holder.cmntCount.setText(commentCount);

                    Intent intent = new Intent(context, DetailsNewsActivity.class);
                    intent.putExtra("headline", headLine);
                    intent.putExtra("body", newsBody);
                    intent.putExtra("newsPic", newsImg);
                    intent.putExtra("newsId", newsId);
                    intent.putExtra("category", category);

                    holder.newsImg.setOnClickListener(new View.OnClickListener() {
                        @RequiresApi(api = Build.VERSION_CODES.O)
                        @Override
                        public void onClick(View view) {
                            context.startActivity(intent);
                            csv.createCSV(context,category,newsId,1);
                        }
                    });

        holder.comment.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                holder.newsImg.callOnClick();

            }
        });

        holder.newsHead.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                holder.newsImg.callOnClick();

            }
        });
        
        holder.love.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {

                if (Constrains.role.equals("guest")) {
                    Toast.makeText(context, "Please login first", Toast.LENGTH_SHORT).show();
                } else {

                    if (react[0]) {
                        react[0] = false;
                        holder.love.setImageResource(R.drawable.love_shape);
                        removeReact(category, newsId);
                        csv.createCSV(context, category, newsId, 2);
                    } else {
                        react[0] = true;
                        holder.love.setImageResource(R.drawable.love_shape_fill);
                        Log.d("love", String.valueOf(false));
                        updateReact(category, newsId, true);
                        csv.createCSV(context, category, newsId, 2);
                    }
                }

            }
        });
    }

    void updateReact(String category,String newsId,boolean react) {
                DatabaseReference ref = database.getReference("category").child(category).child("news").child(newsId).child("react").child(uid);
                dataReact data = new dataReact(react, uid);
                ref.setValue(data);
    }

    void removeReact(String category,String newsId){
        DatabaseReference ref = database.getReference("category").child(category).child("news").child(newsId).child("react").child(uid);
        ref.removeValue();
    }




    @NonNull
    @Override
    public ViewHolderTrends onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem= layoutInflater.inflate(R.layout.rv_item_trending_news, parent, false);
        ViewHolderTrends viewholerTrend= new ViewHolderTrends(listItem);
        return viewholerTrend;
    }
}
