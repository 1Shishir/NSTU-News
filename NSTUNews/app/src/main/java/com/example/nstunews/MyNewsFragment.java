package com.example.nstunews;

import android.app.Dialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MyNewsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyNewsFragment extends Fragment {

    RecyclerView rv_myNews;
    FloatingActionButton fab_myNews;
    AdapterRvMyNews adapterRvMyNews;
    BottomFragment bottomFragment;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public MyNewsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MyNewsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MyNewsFragment newInstance(String param1, String param2) {
        MyNewsFragment fragment = new MyNewsFragment();
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
        View view= inflater.inflate(R.layout.fragment_my_news, container, false);
        rv_myNews=view.findViewById(R.id.rv_my_news);
        fab_myNews=view.findViewById(R.id.fab_add);

        bottomFragment=new BottomFragment();

        rv_myNews.setLayoutManager(new LinearLayoutManager(getContext()));

        adapterRvMyNews=new AdapterRvMyNews(getNewsFromFirebase(),getContext());
        rv_myNews.setAdapter(adapterRvMyNews);

        fab_myNews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomFragment.show(getActivity().getSupportFragmentManager(),bottomFragment.getTag());
            }
        });

        return view;
    }

    FirebaseRecyclerOptions<dataTag> getNewsFromFirebase(){
        Query queryMyNews = FirebaseDatabase.getInstance().getReference("profile").child(FirebaseAuth.getInstance().getUid()).child("my news");

        FirebaseRecyclerOptions<dataTag> optionsMyNews = new FirebaseRecyclerOptions.Builder<dataTag>()
                .setQuery(queryMyNews, dataTag.class)
                .build();
        return optionsMyNews;
    }

    @Override public void onStart()
    {
        super.onStart();
      //  adapterRvMyNews.startListening();

    }

    @Override public void onStop()
    {
        super.onStop();
    //    adapterRvMyNews.stopListening();

    }

}