package com.example.nstunews;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.ReceiverCallNotAllowedException;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;

public class AdapterRvCategory extends FirebaseRecyclerAdapter<dataCategory, ViewHolderCategory>{
    RecyclerView categoryNews;
    AdapterRvCategoryNews adapterCategoryNews;
    Context context;
    boolean first=true;
    int mode;
    Activity activity;

    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public AdapterRvCategory(@NonNull FirebaseRecyclerOptions<dataCategory> options,Context context,int mode) {
        super(options);
        this.context=context;
        this.mode=mode;
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolderCategory holder, @SuppressLint("RecyclerView") int position, @NonNull dataCategory model) {
//0 - home
//1- category
        if(mode==0){
            holder.category.setText(model.name);
            holder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    FragmentTransaction transaction = ((FragmentActivity)context).getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.frameLayout, CategoryFragment.newInstance("",""));
                    transaction.addToBackStack(null);
                    transaction.commit();
                }
            });
        }
        else if(mode==1) {

            categoryNews = ((Activity) context).findViewById(R.id.rv_category_news);
            categoryNews.setLayoutManager(new LinearLayoutManager(context));
            holder.category.setText(model.name);



            if (first) {
                adapterCategoryNews = new AdapterRvCategoryNews(getNews(model.name), context);
                adapterCategoryNews.startListening();
                categoryNews.setAdapter(adapterCategoryNews);
                first = false;
            }

            holder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    adapterCategoryNews = new AdapterRvCategoryNews(getNews(model.name), context);
                    adapterCategoryNews.startListening();
                    categoryNews.setAdapter(adapterCategoryNews);

                }
            });
        }

    }



    FirebaseRecyclerOptions<dataNews> getNews(String category) {
        Query queryMyNews = FirebaseDatabase.getInstance().getReference("category").child(category).child("news");
        FirebaseRecyclerOptions<dataNews> optionsMyNews = new FirebaseRecyclerOptions.Builder<dataNews>()
                .setQuery(queryMyNews, dataNews.class)
                .build();
        return optionsMyNews;
    }



    @NonNull
    @Override
    public ViewHolderCategory onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem= layoutInflater.inflate(R.layout.rv_item_category, parent, false);
        ViewHolderCategory viewHolderCategory= new ViewHolderCategory(listItem);
        return viewHolderCategory;
    }
}

class ViewHolderCategory extends RecyclerView.ViewHolder{
    CardView cardView;
    TextView category;
    public ViewHolderCategory(@NonNull View itemView) {
        super(itemView);
        category=itemView.findViewById(R.id.rv_category_text);
        cardView=itemView.findViewById(R.id.cardView);
    }
}
