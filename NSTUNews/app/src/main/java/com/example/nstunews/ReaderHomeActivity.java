package com.example.nstunews;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class ReaderHomeActivity extends AppCompatActivity {

    NavigationView nav;
    ArrayList<Object> idsList;
    ArrayList<Object> strList;
    ActionBarDrawerToggle toggle;
    DrawerLayout drawerLayout;

    TextView userName;
    ImageView profileImg;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reader_home);
        mDatabase = FirebaseDatabase.getInstance().getReference();


//python
                if (! Python.isStarted()) {
                    Python.start(new AndroidPlatform(ReaderHomeActivity.this));
                }
                Python py = Python.getInstance();
        final PyObject[] module = new PyObject[1];

     //python



        Toolbar toolbar=(Toolbar)findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);

        nav=(NavigationView)findViewById(R.id.navmenu);
        drawerLayout=(DrawerLayout)findViewById(R.id.drawer);

        toggle=new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.navbar_open,R.string.navbar_close);

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        toolbar.setNavigationIcon(R.drawable.menu_1);


        //drawer header
        View headerView = nav.getHeaderView(0);
        userName= (TextView) headerView.findViewById(R.id.user_name);
        profileImg=(ImageView) headerView.findViewById(R.id.profile_img);
        openFragment(HomeFragment.newInstance("", ""));

        //
        MenuItem editProfileMenu = nav.getMenu().findItem(R.id.menu_editProfile);
        MenuItem signInMenu = nav.getMenu().findItem(R.id.menu_signin);
        MenuItem signOutMenu = nav.getMenu().findItem(R.id.menu_signout);

        MenuItem addNewsMenu = nav.getMenu().findItem(R.id.menu_add_news);
        MenuItem addCategoryMenu = nav.getMenu().findItem(R.id.menu_add_category);


        editProfileMenu.setVisible(false);

        if(Constrains.role.equals("guest"))
        {
            signOutMenu.setVisible(false);

            addNewsMenu.setVisible(false);
            addCategoryMenu.setVisible(false);
        }
        else if(Constrains.role.equals("reader")){
            signInMenu.setVisible(false);

            addNewsMenu.setVisible(false);
            addCategoryMenu.setVisible(false);

        }
        else {
            signInMenu.setVisible(false);
        }

        nav.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId())
                {
                    case R.id.menu_home:
                        openFragment(HomeFragment.newInstance("", ""));
                        drawerLayout.closeDrawer(GravityCompat.START);
                        break;

                    case R.id.menu_category:
                        openFragment(CategoryFragment.newInstance("", ""));
                        drawerLayout.closeDrawer(GravityCompat.START);
                        break;

                    case R.id.menu_add_news:
                        startActivity(new Intent(ReaderHomeActivity.this,AddNewsActivity.class));
                        drawerLayout.closeDrawer(GravityCompat.START);
                        break;

                    case R.id.menu_add_category:
                        startActivity(new Intent(ReaderHomeActivity.this,AddCategoryActivity.class));
                        drawerLayout.closeDrawer(GravityCompat.START);
                        break;

                    case R.id.menu_signin:
                        finish();
                        startActivity(new Intent(ReaderHomeActivity.this,LoginActivity.class));
                        drawerLayout.closeDrawer(GravityCompat.START);
                        break;

                    case R.id.menu_signout:
                        FirebaseAuth.getInstance().signOut();
                        finish();
                        startActivity(new Intent(ReaderHomeActivity.this,LoginActivity.class));
                        break;

                    case R.id.menu_run_python:
                         module[0] = py.getModule("script");
                         PyObject ids=module[0].callAttr("output_id");
                         PyObject str=module[0].callAttr("output_str");

                        idsList = new ArrayList<>(ids.asList());
                        strList = new ArrayList<>(str.asList());
                        System.out.println("py run");
                        break;

                    case R.id.menu_upload:
                        uploadWeightToFbase();
                        break;


                }

                return true;
            }
        });






    }
    public void openFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        transaction.replace(R.id.frameLayout, fragment);

        transaction.addToBackStack(null);
        transaction.commit();
    }
    void uploadWeightToFbase(){
        String uid=FirebaseAuth.getInstance().getUid().toString();
        String Path="/data/data/com.example.nstunews/cache/"+uid+"/userData.csv";
       //  upload to firebse

        if(idsList.isEmpty() || strList.isEmpty()){
            Toast.makeText(this, "List is empty", Toast.LENGTH_SHORT).show();
        }
        else {
            for (int i = 0; i < idsList.size(); i++) {
                mDatabase.child("weight").child(uid).child(idsList.get(i).toString()).setValue(strList.get(i).toString());
            }

            //delete csv
                    try {
            File fileToDelete = new File(Path);

            if (fileToDelete.delete()) {
                System.out.println("file deleted");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        }





//                String filePath = "/data/data/com.example.nstunews/cache/weighted_data.csv";


//                ArrayList<String> postIds = new ArrayList<>();
//                ArrayList<Double> eventStrengths = new ArrayList<>();



//                try {
//                    File file = new File(filePath);
//                    Scanner scanner = new Scanner(file);
//                    if (scanner.hasNextLine()) {
//                        scanner.nextLine();
//                        // Skip the header line
//                    }
//                    // Process the file line by line
//                    while (scanner.hasNextLine()) {
//                        String line = scanner.nextLine();
//                        String[] values = line.split(",");
//
//                        if (values.length == 2) {
//                            postIds.add(values[0]);
//                            eventStrengths.add(Double.parseDouble(values[1]));
//                        }
//                    }
//                    scanner.close();
//                } catch (FileNotFoundException e) {
//                    throw new RuntimeException(e);
//                }
        // upload to firebse
//        for(int i=0;i<postIds.size();i++){
//            mDatabase.child("weight").child(uid).child(postIds.get(i)).setValue(eventStrengths.get(i).toString());
//        }


//        try {
//            File fileToDelete = new File(filePath);
//
//            if (fileToDelete.delete()) {
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

            }


}





