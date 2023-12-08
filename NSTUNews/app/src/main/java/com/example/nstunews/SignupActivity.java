package com.example.nstunews;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

public class SignupActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;

    TextView heading;
    TextView login;

    TextInputEditText firstName;
    TextInputEditText lastName;
    TextInputEditText email;
    TextInputEditText password;
    TextInputEditText phone;
    Button signup;
    Button changeRole;

    private String role="reader";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        mAuth=FirebaseAuth.getInstance();

        firstName=findViewById(R.id.sign_fname);
        lastName=findViewById(R.id.sign_lname);
        email=findViewById(R.id.sign_email);
        password=findViewById(R.id.sign_password);
        phone=findViewById(R.id.sign_phone);

        signup=findViewById(R.id.sign_signup);
        changeRole=findViewById(R.id.sign_role);

        login=findViewById(R.id.nevigateLogin);
        heading=findViewById(R.id.textView);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignupActivity.this,LoginActivity.class));
            }
        });


        changeRole.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(role.equals("reader")){
                    role="writer";
                    heading.setText("Register as writer");
                    changeRole.setText("Register as reader");
                }
                else {
                    role="reader";
                    heading.setText("Register as reader");
                    changeRole.setText("Register as writer");
                }
            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(firstName.getText().toString().isEmpty()||
                        lastName.getText().toString().isEmpty()||
                        email.getText().toString().isEmpty()||
                        phone.getText().toString().isEmpty()||
                        password.getText().toString().isEmpty()){

                    Toast.makeText(SignupActivity.this, "Please fill all field", Toast.LENGTH_SHORT).show();

                }
                else {
                    createUser(email.getText().toString(),password.getText().toString());
                }
            }
        });

    }
    void createUser(String emailStr, String passwordStr){

        mAuth.createUserWithEmailAndPassword(emailStr,passwordStr).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Bitmap bmp= BitmapFactory.decodeResource(getResources(), R.drawable.dp);
                    uploadProfilePic(mAuth.getCurrentUser().getUid().toString(),getImageUri(SignupActivity.this,bmp));
                }
                else {
                    firstName.setText("");
                    lastName.setText("");
                    phone.setText("");
                    email.setText("");
                    password.setText("");
                    Toast.makeText(SignupActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });


    }
    void uploadProfilePic(String uid,Uri uri){
        StorageReference storageRef= FirebaseStorage.getInstance().getReference().child("profilePic").child(uid);
        storageRef.putFile(uri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if(task.isSuccessful()){
                    storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            uploadUserData(uid,uri.toString());
                        }
                    });
                }
                else {
                    firstName.setText("");
                    lastName.setText("");
                    phone.setText("");
                    email.setText("");
                    password.setText("");
                    Toast.makeText(SignupActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });


    }
    void uploadUserData(String uid,String profilePictureUri){


        dataProfile profile=new dataProfile(firstName.getText().toString(),
                                            lastName.getText().toString(),
                                            email.getText().toString(),
                                            uid,
                                            phone.getText().toString(),
                                            role,
                                            profilePictureUri
                                           );
        FirebaseDatabase db=FirebaseDatabase.getInstance();
        DatabaseReference node =db.getReference("profile").child(uid).child("profile");
        node.setValue(profile);

        Toast.makeText(this, "User Created", Toast.LENGTH_SHORT).show();

        this.firstName.setText("");
        this.lastName.setText("");
        this.email.setText("");
        this.phone.setText("");
        this.password.setText("");

        startActivity(new Intent(SignupActivity.this,LoginActivity.class));
        finish();

        }
    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }
}