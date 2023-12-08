package com.example.nstunews;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
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

public class AdapterRvTrendings extends FirebaseRecyclerAdapter<dataTag, ViewHolderTrends> {
    Context context;
    String category;
    String newsId;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    final boolean[] react = {false};
    String uid;
    CSV csv=new CSV();
    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public AdapterRvTrendings(@NonNull FirebaseRecyclerOptions<dataTag> options,Context context) {
        super(options);
        this.context=context;
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolderTrends holder, int position, @NonNull dataTag model) {
       category=model.category;
       newsId=model.newsId;
       uid= FirebaseAuth.getInstance().getUid();

        DatabaseReference ref = database.getReference("category").child(category).child("news").child(newsId);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    String headlineStr=String.valueOf(snapshot.child("headline").getValue());
                    holder.newsHead.setText(headlineStr);
                    Glide.with(context).load(String.valueOf(snapshot.child("newsPic").getValue())).into(holder.newsImg);

                    Intent intent=new Intent(context,DetailsNewsActivity.class);
                    intent.putExtra("headline",headlineStr);
                    intent.putExtra("body",String.valueOf(snapshot.child("body").getValue()));
                    intent.putExtra("newsPic",String.valueOf(snapshot.child("newsPic").getValue()));
                    intent.putExtra("newsId",String.valueOf(snapshot.child("newsId").getValue()));
                    intent.putExtra("category",String.valueOf(snapshot.child("category").getValue()));

                    DatabaseReference ref = database.getReference("profile").child(String.valueOf(snapshot.child("owner_uid").getValue())).child("profile");
                    ref.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            if(snapshot.exists()){

                                holder.name.setText(String.valueOf(snapshot.child("lastName").getValue()));
                                Glide.with(context).load(String.valueOf(snapshot.child("profileImage").getValue())).into(holder.profileImg);

                                holder.newsImg.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        context.startActivity(intent);
                                       // csv.createCSV(context,category,newsId,1);
                                    }
                                });

                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

                }
               }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        holder.comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.newsImg.callOnClick();
            }
        });

        holder.newsHead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.newsImg.callOnClick();
            }
        });

        //count like
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
        //comment count
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

        //If am liked or not
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
//change like
//        holder.love.setOnClickListener(new View.OnClickListener() {
//            @RequiresApi(api = Build.VERSION_CODES.O)
//            @Override
//            public void onClick(View view) {
//
//                if (Constrains.role.equals("guest")) {
//                    Toast.makeText(context, "Please login first", Toast.LENGTH_SHORT).show();
//                } else {
//
//                    if (react[0]) {
//                        react[0] = false;
//                        holder.love.setImageResource(R.drawable.love_shape);
//                        removeReact(category, newsId);
//                        //csv.createCSV(context, category, newsId, 2);
//                    } else {
//                        react[0] = true;
//                        holder.love.setImageResource(R.drawable.love_shape_fill);
//                        Log.d("love", String.valueOf(false));
//                        updateReact(category, newsId, true);
//                       // csv.createCSV(context, category, newsId, 2);
//                    }
//                }
//
//            }
//        });

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


//            holder.love.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    if(Constrains.role.equals("guest")) {
//                        Toast.makeText(context, "Please Login for React and Comment", Toast.LENGTH_SHORT).show();
//                    }
//                    else {
//
//                   //react Mechanism
//                    if (love) {
//                        love = false;
//                        holder.love.setImageResource(R.drawable.love_shape);
//                    } else {
//                        love = true;
//                        holder.love.setImageResource(R.drawable.love_shape_fill);
//                    }
//                }
//
//                }
//            });
//
//        holder.newsHead.setText(newshead[position]);
//        holder.newsImg.setImageResource(newsImg[position]);
//
//    }

class ViewHolderTrends extends RecyclerView.ViewHolder{
    ImageView newsImg;
    TextView  newsHead;
    ImageView love;
    ImageView comment;
    ImageView profileImg;
    TextView name;
    TextView loveCount;
    TextView cmntCount;
    ConstraintLayout cl;
    public ViewHolderTrends(@NonNull View itemView) {
        super(itemView);
        newsImg=itemView.findViewById(R.id.imageView);
        newsHead=itemView.findViewById(R.id.trending_heading);
        love=itemView.findViewById(R.id.imageView2);
        comment=itemView.findViewById(R.id.imageView3);
        profileImg=itemView.findViewById(R.id.reporter_profile);
        name=itemView.findViewById(R.id.textView7);
        cl=itemView.findViewById(R.id.cl_tr);
        loveCount=itemView.findViewById(R.id.love_count);
        cmntCount=itemView.findViewById(R.id.cmnt_count);
    }
}