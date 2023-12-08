package com.example.nstunews;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    TextInputEditText email;
    TextInputEditText password;
    TextView register;
    TextView forgetPass;
    Button login;
    Button guestLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        email=findViewById(R.id.login_email);
        password=findViewById(R.id.login_password);
        register=findViewById(R.id.login_signup);
        forgetPass=findViewById(R.id.login_forget);
        login=findViewById(R.id.login_Login);
        guestLogin=findViewById(R.id.login_guest);


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(email.getText().toString().isEmpty() || password.getText().toString().isEmpty()){
                    Toast.makeText(LoginActivity.this, "Please provide email and password", Toast.LENGTH_SHORT).show();
                }
                else {
                        loginUsingEmail(email.getText().toString(),password.getText().toString());
                }
            }
        });

        guestLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginUsingGuestMode();
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this,SignupActivity.class));
            }
        });

        forgetPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(email.getText().toString().isEmpty()){
                    Toast.makeText(LoginActivity.this, "Fill email for reset the password", Toast.LENGTH_SHORT).show();
                }
                else {
                    resetPassword(email.getText().toString());
                }
            }
        });

    }


    void loginUsingEmail(String email, String password){
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()){
                    checkRole(mAuth.getCurrentUser().getUid());
                }
                else{
                    Toast.makeText(LoginActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    void checkRole(String uid){

        final String[] role = {null};

        FirebaseDatabase.getInstance().getReference("profile").child(uid).child("profile").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                role[0] =snapshot.child("role").getValue(String.class);
                if(role[0].equals("reader")){
                    Constrains.uid=uid;
                    Constrains.role="reader";
                    startActivity(new Intent(LoginActivity.this,ReaderHomeActivity.class));
                    finish();
                }
                else if (role[0].equals("writer")){
                    Constrains.uid=uid;
                    Constrains.role="writer";
                    startActivity(new Intent(LoginActivity.this,ReaderHomeActivity.class));
                    finish();

                }
                else {

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

            void loginUsingGuestMode(){
        mAuth.signInAnonymously().addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(!task.isSuccessful()){
                    Toast.makeText(LoginActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
                else {
                    Constrains.uid="guest";
                    Constrains.role="guest";
                    startActivity(new Intent(LoginActivity.this,ReaderHomeActivity.class));
                    finish();
                    }
            }
        });
    }

    void resetPassword(String email) {
        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(LoginActivity.this, "Password reset successful, Please check email", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(LoginActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}