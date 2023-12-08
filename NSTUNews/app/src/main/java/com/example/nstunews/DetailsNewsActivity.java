package com.example.nstunews;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class DetailsNewsActivity extends AppCompatActivity {

    ImageView backBtn;
    TextView headline;
    TextView body;
    ImageView img;
    ImageView react;
    TextView reactCount;
    TextView commentCount;
    RecyclerView rv_comments;
    TextInputEditText commentEditText;
    ImageView makeCmntBtn;
    CardView makeReactBtn;
    Intent intent;
    FirebaseDatabase database;
    ConstraintLayout mainLayout;
    boolean setReact=false;
    String uid;

    AdapterRvComment adapterComment;
    CSV csv=new CSV();





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_news);

        backBtn=findViewById(R.id.backbtnImgDetails);
        headline=findViewById(R.id.detailsHeading);
        body=findViewById(R.id.detailsBody);
        img=findViewById(R.id.detailsImg);
        react=findViewById(R.id.imageView5);
        reactCount=findViewById(R.id.detaild_react_count);
        commentCount=findViewById(R.id.detaild_cmnt_count);
        rv_comments=findViewById(R.id.rv_commentBox);
        commentEditText=findViewById(R.id.comment);
        makeCmntBtn=findViewById(R.id.make_cmt);
        makeReactBtn=findViewById(R.id.cardView6);
        mainLayout=findViewById(R.id.cl_details);

        intent=getIntent();
        database= FirebaseDatabase.getInstance();
        uid= FirebaseAuth.getInstance().getUid();

        headline.setText(intent.getStringExtra("headline"));
        body.setText(intent.getStringExtra("body"));
        Glide.with(DetailsNewsActivity.this).load(intent.getStringExtra("newsPic")).into(img);

        String category=intent.getStringExtra("category");
        String newsId=intent.getStringExtra("newsId");
        FirebaseDatabase db=FirebaseDatabase.getInstance();

//back btn
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        rv_comments.setLayoutManager(new LinearLayoutManager(this));

        Query query = FirebaseDatabase.getInstance().getReference("category").child(category).child("news").child(newsId).child("comment");
        FirebaseRecyclerOptions<dataComment> options = new FirebaseRecyclerOptions.Builder<dataComment>()
                .setQuery(query, dataComment.class)
                .build();

        adapterComment=new AdapterRvComment(options,this);
        rv_comments.setAdapter(adapterComment);



        //react count
//        DatabaseReference ref = database.getReference("category").child(intent.getStringExtra("category")).child("news").child(intent.getStringExtra("newsId")).child("react");
//        ref.addValueEventListener(new ValueEventListener() {
//            @RequiresApi(api = Build.VERSION_CODES.N)
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                if (snapshot.exists()) {
//                    String count=String.valueOf(snapshot.getChildrenCount());
//                    reactCount.setText(count);
//
//                    if (Boolean.parseBoolean(snapshot.child(uid).child("react").getValue().toString())) {
//                        setReact = true;
//                        react.setImageResource(R.drawable.love_shape_fill);
//
//                    } else {
//                        setReact = false;
//                        react.setImageResource(R.drawable.love_shape);
//                    }
//                }
//                //change react
//                makeReactBtn.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//
//                        if (!setReact) {
//                            setReact = true;
//                            react.setImageResource(R.drawable.love_shape_fill);
//                            changeReactInFirebase(category,newsId);
//                            String c=count("react",category,newsId);
//                            reactCount.setText(c);
//
//                        } else {
//                            setReact= false;
//                            react.setImageResource(R.drawable.love_shape);
//                            removeReactInFirebase(uid,category,newsId);
//                            String c=count("react",category,newsId);
//                            reactCount.setText(c);
//                        }
//                    }
//                });
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                Toast.makeText(DetailsNewsActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        });
//
//
//
//        //
//
//        //comment count
//        ref = database.getReference("category").child(category).child("news").child(newsId).child("comment");
//        ref.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                String cCount=String.valueOf(snapshot.getChildrenCount());
//                commentCount.setText(cCount);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//

        makeCmntBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (Constrains.role.equals("guest")) {
                    Toast.makeText(DetailsNewsActivity.this, "Please login first", Toast.LENGTH_SHORT).show();
                } else {


                if(commentEditText.getText().toString().isEmpty()){
                    Toast.makeText(DetailsNewsActivity.this, "Please write comment", Toast.LENGTH_SHORT).show();
                }
                else{
                    //
                    DatabaseReference mDatabaseRef = FirebaseDatabase.getInstance().getReference("category").child(category).child("news").child(newsId).child("comment");
                    mDatabaseRef.orderByKey().limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
                        @RequiresApi(api = Build.VERSION_CODES.O)
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String cmntId = "0";
                            if (snapshot.exists()) {
                                cmntId = snapshot.getChildren().iterator().next().getKey().toString();
                                cmntId = String.valueOf(Integer.parseInt(cmntId) + 1);
                            }
                            uploadCmntToFirebase(category,newsId,cmntId);
                            csv.createCSV(DetailsNewsActivity.this,category,newsId,3);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });


                    //

                }

                }
            }
        });

    }

    void uploadCmntToFirebase(String category,String newsId,String cmntId){
        DatabaseReference node =FirebaseDatabase.getInstance().getReference("category").child(category).child("news").child(newsId).child("comment").child(cmntId);
        dataComment data=new dataComment("0",commentEditText.getText().toString(),uid);
        node.setValue(data);
        commentEditText.setText("");
        InputMethodManager imm=(InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mainLayout.getWindowToken(),0);
    }

    void changeReactInFirebase(String category,String newsId) {
        DatabaseReference ref = database.getReference("category").child(category).child("news").child(newsId).child("react").child(uid);
        dataReact data = new dataReact(setReact, uid);
        ref.setValue(data);
    }

    void removeReactInFirebase(String uid,String category,String newsId) {
        DatabaseReference ref = database.getReference("category").child(category).child("news").child(newsId).child("react").child(uid);
        ref.removeValue();
    }

//    String count(String txt,String category,String newsId){
//        final String[] react = {"0"};
//
//        DatabaseReference ref = database.getReference("category").child(category).child("news").child(newsId).child(txt);
//        ref.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                react[0] =String.valueOf(snapshot.getChildrenCount());
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//
//        return react[0];
//    }

    @Override
    public void onStop() {
        super.onStop();
        adapterComment.startListening();
    }

    @Override
    public void onStart() {
        super.onStart();
        adapterComment.startListening();
    }

}