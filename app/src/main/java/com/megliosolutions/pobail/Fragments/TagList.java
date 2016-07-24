package com.megliosolutions.pobail.Fragments;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.megliosolutions.pobail.Adapters.TagListAdapter;
import com.megliosolutions.pobail.MainActivity;
import com.megliosolutions.pobail.Objects.TagObject;
import com.megliosolutions.pobail.R;

import java.util.ArrayList;

/**
 * Created by Meglio on 7/22/16.
 */
public class TagList extends Fragment {

    public static final String TAG = TagList.class.getSimpleName();
    public ListView tag_listView;
    public TagListAdapter listAdapter;
    public ArrayList<TagObject> tagObjectArrayList = new ArrayList<>();

    public DatabaseReference mTags;
    public DatabaseReference mDatabase;
    public DataSnapshot mDataSnapShot;
    public FirebaseAuth mAuth;
    public FirebaseUser mUser;
    public String currentUser;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_taglist, container,false);

        initializeVariables(view);
        getTags();

        tag_listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TagObject tag = tagObjectArrayList.get(position);
                //Go to fragment
                Settings settings = new Settings();
                FragmentManager fragmentManager = getFragmentManager();
                //Replace intent with Bundle and put it in the transaction
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.main_FrameLayout, settings);
                fragmentTransaction.commit();
                getActivity().setTitle("Tag Properties");
                AppCompatActivity activity = new AppCompatActivity();
                if (activity.getSupportActionBar() != null) {
                    activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                }
            }
        });

        return view;
    }

    private void initializeVariables(View view) {
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mTags = mDatabase.child("tags");
        currentUser = mUser.getUid();
        tag_listView = (ListView) view.findViewById(R.id.tag_list);
        Log.d(TAG, "initializeVariables: LISTVIEW SET!");

    }

    private void getTags() {
        mTags.child(mUser.getUid()).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                mDataSnapShot = dataSnapshot;
                TagObject tagCount = mDataSnapShot.getValue(TagObject.class);
                tagObjectArrayList.add(tagCount);
                Log.d(TAG, "onChildAdded: " + tagObjectArrayList.size());
                setUpListview();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                mDataSnapShot = dataSnapshot;
                TagObject tagCount = mDataSnapShot.getValue(TagObject.class);
                tagObjectArrayList.add(tagCount);
                Log.d(TAG, "onChildAdded: " + tagObjectArrayList.size());
                setUpListview();
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void setUpListview() {
        //ListView stuff
        if(tagObjectArrayList.size()>0){
            listAdapter = new TagListAdapter(getContext(),tagObjectArrayList);
            tag_listView.setAdapter(listAdapter);
            listAdapter.notifyDataSetChanged();
        }
        else {
            Toast.makeText(getActivity(),"No Tags",Toast.LENGTH_SHORT).show();
        }
    }


}