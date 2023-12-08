package com.example.nstunews;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.AlignmentSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AddNewsActivity extends AppCompatActivity {

    RecyclerView category;
    RecyclerView tag;
    AdapterRvAddNewsCategory adapterCategory;
    AdapterRvAddNewsCategory adapterTag;

    TextInputEditText headline;
    TextInputEditText body;

    ImageView leftAlign;
    ImageView rightAlign;
    ImageView centerAlign;
    ImageView justifyAlign;

    ImageView cancelBtn;
    ImageView backBtn;

    CardView addImgBtn;
    CardView postBtn;

    TextView selectedCategory;
    TextView selectedTag;

    ImageView newsImage;
    Uri uri;

    String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_news);

        category=findViewById(R.id.recyclerViewNewsCategory);
        tag=findViewById(R.id.recyclerView2);

        headline=findViewById(R.id.edit);
        body=findViewById(R.id.editNewsBody);
        leftAlign=findViewById(R.id.left);
        rightAlign=findViewById(R.id.right);
        centerAlign=findViewById(R.id.centerAlign);
        justifyAlign=findViewById(R.id.justify);
        cancelBtn=findViewById(R.id.clear);
        backBtn=findViewById(R.id.backbtn1Img);
        addImgBtn=findViewById(R.id.cardView3);
        selectedCategory=findViewById(R.id.categoryTxtView);
        selectedTag=findViewById(R.id.tagTxtView);
        postBtn=findViewById(R.id.postBtn);
        newsImage=findViewById(R.id.preview_img);

        newsImage.setVisibility(View.GONE);

        category.setLayoutManager(new LinearLayoutManager(AddNewsActivity.this,LinearLayoutManager.HORIZONTAL,false));
        tag.setLayoutManager(new LinearLayoutManager(AddNewsActivity.this,LinearLayoutManager.HORIZONTAL,false));

        //loadcategory
        Query query = FirebaseDatabase.getInstance().getReference("category");

        FirebaseRecyclerOptions<dataCategory> optionsCategory = new FirebaseRecyclerOptions.Builder<dataCategory>()
                .setQuery(query, dataCategory.class)
                .build();
        //

        adapterCategory=new AdapterRvAddNewsCategory(optionsCategory,AddNewsActivity.this,"category");

        //load tag
        Query queryTag = FirebaseDatabase.getInstance().getReference("tag");

        FirebaseRecyclerOptions<dataCategory> optionsTag = new FirebaseRecyclerOptions.Builder<dataCategory>()
                .setQuery(queryTag, dataCategory.class)
                .build();

        adapterTag=new AdapterRvAddNewsCategory(optionsTag,AddNewsActivity.this,"tag");

        category.setAdapter(adapterCategory);
        tag.setAdapter(adapterTag);

        uid=FirebaseAuth.getInstance().getUid();

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        addImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImagePicker.with(AddNewsActivity.this).crop(16f,9f)
                        .compress(1024)
                        .maxResultSize(1080, 1080)
                        .start();
            }
        });


        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                body.setText("");
            }
        });

        leftAlign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alignText(Layout.Alignment.ALIGN_NORMAL);
            }
        });

        rightAlign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alignText(Layout.Alignment.ALIGN_OPPOSITE);
            }
        });

        centerAlign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alignText(Layout.Alignment.ALIGN_CENTER);
            }
        });

        justifyAlign.setVisibility(View.GONE);

        postBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                if (uri.toString().isEmpty() ||
                        headline.getText().toString().isEmpty() ||
                        body.getText().toString().isEmpty() ||
                        selectedCategory.getText().toString().equals("Selected category") ||
                        selectedTag.getText().toString().equals("selected tag")) {

                    Toast.makeText(AddNewsActivity.this, "Fill all field including image", Toast.LENGTH_SHORT).show();

                }
                else {
                    String selectedCategoryStr = selectedCategory.getText().toString();
                    String newsId=timeStamp();
                    uploadImageToFirebase(uri, selectedCategoryStr,newsId, uid);
                }
//                else {
//
//                    String selectedCategoryStr = selectedCategory.getText().toString();
//                    DatabaseReference mDatabaseRef = FirebaseDatabase.getInstance().getReference("category");
//
//                    mDatabaseRef.child(selectedCategoryStr).child("news").orderByKey().limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(@NonNull DataSnapshot snapshot) {
//                            String newsId = "0";
//                            if (snapshot.exists()) {
//                                newsId = snapshot.getChildren().iterator().next().getKey().toString();
//                                newsId = String.valueOf(Integer.parseInt(newsId) + 1);
//                            }
//                            uploadImageToFirebase(uri, selectedCategoryStr, newsId, uid);
//                        }
//
//                        @Override
//                        public void onCancelled(@NonNull DatabaseError error) {
//
//                        }
//                    });
//
//                }

            }
        });

    }

    void uploadImageToFirebase(Uri imgUri,String category,String newsId,String uid){
        StorageReference storageRef= FirebaseStorage.getInstance().getReference().child("category").child(category).child(newsId);
        storageRef.putFile(imgUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if(task.isSuccessful()){
                    storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            uploadDataToFirebase(category,newsId,uri.toString(),uid);
                        }
                    });
                }
                else {
                    Toast.makeText(AddNewsActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
    });

    }
    void uploadDataToFirebase(String category,String newsId,String newsImg,String uid){
        String selectedTagStr=selectedTag.getText().toString();
        dataNews data=new dataNews(newsId,headline.getText().toString(),
                body.getText().toString(),newsImg,uid,category,selectedTagStr);

        FirebaseDatabase db=FirebaseDatabase.getInstance();
        DatabaseReference node =db.getReference("category").child(category).child("news").child(newsId);
        node.setValue(data);

        uploadMyNews(uid,selectedTagStr,category,newsId);


    }
    void uploadTag(String tag,String category,String newsId){
        dataTag data=new dataTag(newsId,category);

        FirebaseDatabase db=FirebaseDatabase.getInstance();
        DatabaseReference node =db.getReference("tag").child(tag).child("news").child(category+newsId);
        node.setValue(data);

        headline.setText("");
        body.setText("");
        newsImage.setVisibility(View.GONE);
        selectedCategory.setText("Selected category");
        selectedTag.setText("Selected tag");

        Toast.makeText(AddNewsActivity.this, "News added", Toast.LENGTH_SHORT).show();
    }

    void uploadMyNews(String uid,String tag,String category,String newsId){
        dataTag data=new dataTag(newsId,category);

        FirebaseDatabase db=FirebaseDatabase.getInstance();
        DatabaseReference node =db.getReference("profile").child(uid).child("my news").child(category+newsId);
        node.setValue(data);

        if(tag.equals("General news")){
            headline.setText("");
            body.setText("");
            newsImage.setVisibility(View.GONE);
            selectedCategory.setText("Selected category");
            selectedTag.setText("Selected tag");

            Toast.makeText(AddNewsActivity.this, "News added", Toast.LENGTH_SHORT).show();
        }

        else {
            uploadTag(tag,category,newsId);
        }
    }

    protected void onStart() {
        super.onStart();
        adapterCategory.startListening();
        adapterTag.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapterCategory.startListening();
        adapterTag.startListening();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        newsImage.setVisibility(View.VISIBLE);
        uri=data.getData();
        newsImage.setImageURI(uri);


    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    String timeStamp(){
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyMMddHHmmss");
        String timestamp = now.format(formatter);
        return timestamp;
    }
    void alignText(Layout.Alignment alignment) {
        int startSelection = body.getSelectionStart();
        int endSelection = body.getSelectionEnd();
        SpannableStringBuilder builder = new SpannableStringBuilder(body.getText());
        AlignmentSpan span = new AlignmentSpan.Standard(alignment);
        builder.setSpan(span, startSelection, endSelection, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        body.setText(builder);
    }

}
