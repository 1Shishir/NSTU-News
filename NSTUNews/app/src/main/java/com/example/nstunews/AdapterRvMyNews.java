package com.example.nstunews;

import android.annotation.SuppressLint;
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

import java.util.Locale;

public class AdapterRvMyNews extends FirebaseRecyclerAdapter<dataTag, ViewholderMyNews> {
    Context context;
    String category;
    String newsId;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    boolean react = false;
    String uid;
    CSV userDataCSV=new CSV();

    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public AdapterRvMyNews(@NonNull FirebaseRecyclerOptions<dataTag> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewholderMyNews holder, @SuppressLint("RecyclerView") int position, @NonNull dataTag model) {

        category = model.category;
        newsId = model.newsId;
        uid = FirebaseAuth.getInstance().getUid();

        DatabaseReference ref = database.getReference("category").child(category).child("news").child(model.newsId);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    String headlineStr = String.valueOf(snapshot.child("headline").getValue());
                    holder.title.setText(headlineStr);
                    Glide.with(context).load(String.valueOf(snapshot.child("newsPic").getValue())).into(holder.image);
                    holder.category.setText(String.valueOf(snapshot.child("category").getValue()));

                    Intent intent = new Intent(context, DetailsNewsActivity.class);
                    intent.putExtra("headline", headlineStr);
                    intent.putExtra("body", String.valueOf(snapshot.child("body").getValue()));
                    intent.putExtra("newsPic", String.valueOf(snapshot.child("newsPic").getValue()));
                    intent.putExtra("newsId", String.valueOf(snapshot.child("newsId").getValue()));
                    intent.putExtra("category", String.valueOf(snapshot.child("category").getValue()));

                    holder.constraintLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            context.startActivity(intent);
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        // react count
        DatabaseReference ref1 = database.getReference("category").child(category).child("news").child(newsId).child("react");
        ref1.addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String count=String.valueOf(snapshot.getChildrenCount());
                    holder.loveCount.setText(count);

                    if (snapshot.child(uid).child("react").getValue(Boolean.class)) {
                        Log.d("t", "onDataChange: "+snapshot.child(uid).child("react").getValue(Boolean.class));
                        react = true;
                        holder.love.setImageResource(R.drawable.love_shape_fill);
                    } else {
                        react = false;
                        holder.love.setImageResource(R.drawable.love_shape);
                    }
                }

                //change react
                holder.love.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (!react) {

                            react = true;

                            holder.love.setImageResource(R.drawable.love_shape_fill);
                            changeReactInFirebase(category,newsId);
                            holder.loveCount.setText(count("react", category, newsId));

                        } else {
                            react = false;
                            holder.love.setImageResource(R.drawable.love_shape);
                            removeReactInFirebase(uid,category, newsId);
                            holder.loveCount.setText(count("react",category,newsId));
                        }
                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        //cmnt count
        //cmnt
       DatabaseReference ref2 = database.getReference("category").child(category).child("news").child(newsId).child("comment");
        ref2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String cCount=String.valueOf(snapshot.getChildrenCount());
                holder.cmntCount.setText(cCount);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        holder.cmnt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.constraintLayout.callOnClick();
            }
        });


    }


    void changeReactInFirebase(String category,String newsId) {
        DatabaseReference ref = database.getReference("category").child(category).child("news").child(newsId).child("react").child(uid);
        Log.d("t", "changeReactInFirebase: "+category+newsId);
        dataReact data = new dataReact(react, uid);
        ref.setValue(data);
    }

    void removeReactInFirebase(String uid,String category,String newsId) {
        DatabaseReference ref = database.getReference("category").child(category).child("news").child(newsId).child("react").child(uid);
        Log.d("t", "removeReactInFirebase: "+category+newsId);
        ref.removeValue();
    }

    String count(String txt,String category,String newsId){
        final String[] react = {"0"};

        DatabaseReference ref = database.getReference("category").child(category).child("news").child(newsId).child(txt);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    react[0] = String.valueOf(snapshot.getChildrenCount());
                    Log.d("t", "count: "+category+newsId+react[0]);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return react[0];
    }

    @NonNull
    @Override
    public ViewholderMyNews onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.rv_item_my_news, parent, false);
        ViewholderMyNews viewholerMyNews = new ViewholderMyNews(listItem);
        return viewholerMyNews;
    }

}


class ViewholderMyNews extends RecyclerView.ViewHolder{
    ImageView image;
    TextView title;
    ImageView love;
    TextView loveCount;
    ImageView cmnt;
    TextView cmntCount;
    TextView category;
    ConstraintLayout constraintLayout;

    public ViewholderMyNews(@NonNull View itemView) {
        super(itemView);
        constraintLayout=itemView.findViewById(R.id.cl_mn);
        image=itemView.findViewById(R.id.imageView_mn);
        title=itemView.findViewById(R.id.trending_heading_mn);
        love=itemView.findViewById(R.id.imageView2_mn);
        loveCount=itemView.findViewById(R.id.love_count_mn);
        cmnt=itemView.findViewById(R.id.imageView3_mn);
        cmntCount=itemView.findViewById(R.id.love_cmnt_mn);
        category=itemView.findViewById(R.id.category);
    }
}