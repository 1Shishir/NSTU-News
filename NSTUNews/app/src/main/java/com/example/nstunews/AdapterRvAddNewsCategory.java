package com.example.nstunews;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

public class AdapterRvAddNewsCategory extends FirebaseRecyclerAdapter<dataCategory, ViewHolderCategory> {
    Context context;
    String name;
    TextView selectedCategory;
    TextView selectedTag;
    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public AdapterRvAddNewsCategory(@NonNull FirebaseRecyclerOptions<dataCategory> options,Context context,String name) {
        super(options);
        this.context=context;
        this.name=name;
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolderCategory holder, int position, @NonNull dataCategory model) {
        holder.category.setText(model.name);
        if(name.equals("category")){
            selectedCategory=((Activity) context).findViewById(R.id.categoryTxtView);
        }
        else{
            selectedTag=((Activity) context).findViewById(R.id.tagTxtView);
        }

        holder.category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (name.equals("category")) {
                    selectedCategory.setText(model.name);
                } else {
                    selectedTag.setText(model.name);
                }
            }
        });
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





//
//
//    public AdapterRvAddNewsCategory(String[] category, Context context,String name) {
//        this.category = category;
//        this.context = context;
//        this.name=name;
//    }
//
//    @NonNull
//    @Override
//    public ViewHolderCategory onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull ViewHolderCategory holder, int position) {
//
//

//        });
//
//    }
//
//    @Override
//    public int getItemCount() {
//        return category.length;
//    }
//}
