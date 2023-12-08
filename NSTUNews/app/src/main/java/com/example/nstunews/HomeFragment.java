package com.example.nstunews;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.interfaces.ItemClickListener;
import com.denzcoskun.imageslider.models.SlideModel;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    RecyclerView rvCategory;
    AdapterRvCategory adapterCategory;


    TextView dateTV;
    ViewFlipper viewFlipper;
    RecyclerView rv_trend;
    AdapterRvTrendings adapterRvTrendings;
    ArrayList<SlideModel> sliderImg;
    ArrayList<dataNews> headlineNewsAl;
    ImageSlider imageSlider;




    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_home, container, false);
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("dd, MMMM yy", Locale.getDefault());

        String[] heading={"‘নাই’ বলে দিলেই কী গেস্টরুমে নির্যাতনের বিভীষিকা মুছে ফেলা যাবে","বাংলাদেশে যেমন কাটল দিল্লি-কাশ্মীরের শিক্ষার্থীদের ঈদ","হলিউডে ধর্মঘট, আলোচনা চালিয়ে যেতে সম্মত অভিনেতাদের সংগঠন ও স্টুডিওগুলো"};
        int[] newsImg={R.drawable.n1,R.drawable.n2,R.drawable.n3};

        //breaking news
        Query queryMyBreakingNews = FirebaseDatabase.getInstance().getReference("tag").child("Breaking news").child("news");
        FirebaseRecyclerOptions<dataTag> optionsBreaking = new FirebaseRecyclerOptions.Builder<dataTag>()
                .setQuery(queryMyBreakingNews, dataTag.class)
                .build();
        //


        rvCategory=view.findViewById(R.id.rv_category_home);
        Query query = FirebaseDatabase.getInstance().getReference("category");
        FirebaseRecyclerOptions<dataCategory> optionsCategory = new FirebaseRecyclerOptions.Builder<dataCategory>()
                .setQuery(query, dataCategory.class)
                .build();

        //
        rvCategory.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false));
        adapterCategory=new AdapterRvCategory(optionsCategory,getContext(),0);
        rvCategory.setAdapter(adapterCategory);

        dateTV=view.findViewById(R.id.reader_home_date);
        rv_trend=view.findViewById(R.id.rv_home_trendings);

        dateTV.setText(sdf.format(date));

        rv_trend.setLayoutManager(new LinearLayoutManager(getContext()));
        //get trending from firebase

        Query queryMyNews = FirebaseDatabase.getInstance().getReference("tag").child("Trending news").child("news");
        FirebaseRecyclerOptions<dataTag> optionsTrending = new FirebaseRecyclerOptions.Builder<dataTag>()
                .setQuery(queryMyNews, dataTag.class)
                .build();
        //
        adapterRvTrendings=new AdapterRvTrendings(optionsTrending,getContext());
        rv_trend.setAdapter(adapterRvTrendings);

         sliderImg = new ArrayList<SlideModel>();

         getBreakingNewsFromFirebase();
         imageSlider = view.findViewById(R.id.imageSlider);
         headlineNewsAl=new ArrayList<dataNews>();

            return view;
    }


    void getBreakingNewsFromFirebase(){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("tag").child("Breaking news").child("news");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    sliderImg.clear();
                    int totalTasks = (int) snapshot.getChildrenCount();
                    AtomicInteger completedTasks = new AtomicInteger(0);

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String categoryStr=String.valueOf(dataSnapshot.child("category").getValue());
                    String newsIdStr=String.valueOf(dataSnapshot.child("newsId").getValue());
                    //
                    FirebaseDatabase database = FirebaseDatabase.getInstance();

                    DatabaseReference ref = database.getReference("category").child(categoryStr).child("news").child(newsIdStr);
                    ref.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.exists()){

                                String headlineStr=String.valueOf(snapshot.child("headline").getValue());
                                String imageStr=String.valueOf(snapshot.child("newsPic").getValue());
                                sliderImg.add(new SlideModel(imageStr,headlineStr, ScaleTypes.FIT));

                                String owner_uid=String.valueOf(snapshot.child("owner_uid").getValue());
                                String tagStr=String.valueOf(snapshot.child("tag").getValue());
                                headlineNewsAl.add(new dataNews(newsIdStr,headlineStr,String.valueOf(snapshot.child("body").getValue()),imageStr,categoryStr,owner_uid,tagStr));


                                int completed = completedTasks.incrementAndGet();
                                if (completed == totalTasks) {

                                    imageSlider.setImageList(sliderImg);
                                    imageSlider.startSliding(3000);

                                    imageSlider.setItemClickListener(new ItemClickListener() {
                                        @Override
                                        public void onItemSelected(int i) {
                                            Intent intent=new Intent(getContext(),DetailsNewsActivity.class);
                                            intent.putExtra("headline",sliderImg.get(i).getTitle());
                                            intent.putExtra("body",headlineNewsAl.get(i).body);
                                            intent.putExtra("newsPic",sliderImg.get(i).getImageUrl());
                                            intent.putExtra("newsId",headlineNewsAl.get(i).newsId);
                                            intent.putExtra("category",headlineNewsAl.get(i).category);
                                            getContext().startActivity(intent);
                                        }

                                        @Override
                                        public void doubleClick(int i) {

                                        }
                                    });
                                }

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }

            }
            else {
                //no data in fbase
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
        adapterRvTrendings.startListening();
        adapterCategory.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapterRvTrendings.startListening();
        adapterCategory.startListening();
    }
}