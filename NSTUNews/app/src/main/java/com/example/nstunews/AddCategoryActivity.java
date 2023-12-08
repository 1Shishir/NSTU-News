package com.example.nstunews;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.ValueEventListener;

public class AddCategoryActivity extends AppCompatActivity {
RecyclerView category;
AdapterRvAddCategory adapteCategory;
TextInputEditText editTextCategory;
Button addCategoryButton;
ImageView backBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_category);

        category=findViewById(R.id.rv_add_category);
        editTextCategory=findViewById(R.id.addCategory);
        addCategoryButton=findViewById(R.id.button);

        backBtn=findViewById(R.id.backbtnCategoryimg);

        category.setLayoutManager(new LinearLayoutManager(AddCategoryActivity.this));

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

      //get data from fbase
        Query query = FirebaseDatabase.getInstance().getReference("category");

        FirebaseRecyclerOptions<dataCategory> options = new FirebaseRecyclerOptions.Builder<dataCategory>()
                .setQuery(query, dataCategory.class)
                .build();
        //

        addCategoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!editTextCategory.getText().toString().isEmpty()){
                    addToFirebase(editTextCategory.getText().toString());
                }

            }
        });

        adapteCategory=new AdapterRvAddCategory(options,this);
        category.setAdapter(adapteCategory);


    }

    void addToFirebase(String category){

        dataCategory data=new dataCategory(category);

        FirebaseDatabase db=FirebaseDatabase.getInstance();
        DatabaseReference node =db.getReference("category").child(category);
        node.setValue(data);

        editTextCategory.setText("");
        Toast.makeText(this, "Category added", Toast.LENGTH_SHORT).show();

    }


    protected void onStart() {
        super.onStart();
        adapteCategory.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapteCategory.startListening();
    }


}