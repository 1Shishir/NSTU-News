package com.example.nstunews;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AdapterRvBreakingNews extends FirebaseRecyclerAdapter<dataTag, ViewHolderTrends> {
    String category;
    String newsId;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    ArrayList<SlideModel> sliderImg = new ArrayList<SlideModel>();
    ImageSlider imageSlider;
    Context context;
    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public AdapterRvBreakingNews(@NonNull FirebaseRecyclerOptions<dataTag> options,Context context) {
        super(options);
        this.context=context;
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolderTrends holder, int position, @NonNull dataTag model) {

        category=model.category;
        newsId=model.newsId;

        DatabaseReference ref = database.getReference("category").child(category).child("news").child(newsId);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){

                    String headlineStr=String.valueOf(snapshot.child("headline").getValue());
                    String imageStr=String.valueOf(snapshot.child("newsPic").getValue());
                    sliderImg.add(new SlideModel(imageStr,headlineStr, ScaleTypes.FIT));



//                    if() {
//                        imageSlider = ((Activity) context).findViewById(R.id.imageSlider);
//                        imageSlider.setImageList(sliderImg);
//                        imageSlider.startSliding(3000);
//                    }

//                    Intent intent=new Intent(context,DetailsNewsActivity.class);
//                    intent.putExtra("headline",headlineStr);
//                    intent.putExtra("body",String.valueOf(snapshot.child("body").getValue()));
//                    intent.putExtra("newsPic",String.valueOf(snapshot.child("newsPic").getValue()));
//                    intent.putExtra("newsId",String.valueOf(snapshot.child("newsId").getValue()));
//                    intent.putExtra("category",String.valueOf(snapshot.child("category").getValue()));

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });




    }

    @NonNull
    @Override
    public ViewHolderTrends onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }
}
