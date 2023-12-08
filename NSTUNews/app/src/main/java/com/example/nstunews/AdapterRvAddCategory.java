package com.example.nstunews;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.ReceiverCallNotAllowedException;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class AdapterRvAddCategory extends FirebaseRecyclerAdapter<dataCategory, ViewHolderAddCategory> {
    Context context;

    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public AdapterRvAddCategory(@NonNull FirebaseRecyclerOptions<dataCategory> options,Context context) {
        super(options);
        this.context=context;
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolderAddCategory holder, int position, @NonNull dataCategory model) {
        holder.category.setText(model.name);


        holder.delet.setVisibility(View.GONE);

        holder.delet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showAlert(model.name);

            }
        });
    }

    @NonNull
    @Override
    public ViewHolderAddCategory onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem= layoutInflater.inflate(R.layout.rv_item_add_category, parent, false);
        ViewHolderAddCategory viewHolderAddCategory= new ViewHolderAddCategory(listItem);
        return viewHolderAddCategory;
    }

    void removeFromDb(String category){

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("category");
        Query delQuery = ref.child(category);

        delQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    dataSnapshot.getRef().removeValue();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }



    void showAlert(String selectedCategory){
        AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
        builder1.setMessage("Do you sure to delete category?");
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        removeFromDb(selectedCategory);
                        dialog.cancel();
                    }
                });

        builder1.setNegativeButton(
                "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

}



class ViewHolderAddCategory extends RecyclerView.ViewHolder{

    TextView category;
    ImageView delet;
    ConstraintLayout constraintLayout;

    public ViewHolderAddCategory(@NonNull View itemView) {
        super(itemView);
        category=itemView.findViewById(R.id.textView9);
        delet=itemView.findViewById(R.id.imageView4);
        constraintLayout=itemView.findViewById(R.id.cl_add_category);
    }
}




























//    String[] category;
//    Context context;
//
//    public AdapterRvAddCategory(String[] category, Context context) {
//        this.category = category;
//        this.context = context;
//    }
//
//    @NonNull
//    @Override
//    public ViewHolderAddCategory onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
//        View listItem= layoutInflater.inflate(R.layout.rv_item_add_category, parent, false);
//        ViewHolderAddCategory viewHolderAddCategory= new ViewHolderAddCategory(listItem);
//        return viewHolderAddCategory;
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull ViewHolderAddCategory holder, int position) {
//        holder.category.setText(category[position]);
//    }
//
//    @Override
//    protected void onBindViewHolder(@NonNull ViewHolderAddCategory holder, int position, @NonNull dataCategory model) {
//
//    }
//
//    @Override
//    public int getItemCount() {
//        return category.length;
//    }
//}
