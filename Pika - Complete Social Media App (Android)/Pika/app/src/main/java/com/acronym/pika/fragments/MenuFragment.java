package com.acronym.pika.fragments;


import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.acronym.pika.R;
import com.acronym.pika.adapters.MenuItemAdapter;
import com.acronym.pika.models.MenuItem;
import com.acronym.pika.models.User;
import com.acronym.pika.utils.GlideUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 */
public class MenuFragment extends Fragment implements View.OnClickListener {


    /*Member variables*/
    private RecyclerView mRecyclerView;
    private ArrayList<MenuItem> mMenuItems;
    private MenuItemAdapter mAdapter;
    private ImageView mProfilePhoto;
    private TextView mUsernameView;

    /*DatabaseReference*/
    private DatabaseReference mDatabaseRef;

    public MenuFragment() {
        // Required empty public constructor
    }


    /*Get user data*/
    private ValueEventListener listener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            loadUserData(dataSnapshot);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_menu, container, false);
        /*Initialize database*/
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("users");
        mRecyclerView = rootView.findViewById(R.id.menu_item_recycler);
        mProfilePhoto = rootView.findViewById(R.id.menu_profile_photo_view);
        mUsernameView = rootView.findViewById(R.id.menu_username_tv);
        mMenuItems = new ArrayList<>();
        mAdapter = new MenuItemAdapter(getActivity(),mMenuItems);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mDatabaseRef.child(getUid()).addValueEventListener(listener);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(mAdapter);
        initializeData();
    }

    private void loadUserData(DataSnapshot dataSnapshot) {
        User user = dataSnapshot.getValue(User.class);
        assert user !=  null;
        if (user.photo != null) {
            GlideUtils.loadDisplayPhoto(user.photo, mProfilePhoto, GlideUtils.CIRCLE_TRANSFORM);
        }
        mUsernameView.setText(user.name);
    }

    private void initializeData() {
        String[] itemNames = getResources().getStringArray(R.array.menuItemNames);
        TypedArray itemIcons = getResources().obtainTypedArray(R.array.menuItemIcons);

        mMenuItems.clear();

        for (int i = 0; i < itemNames.length; i++) {
            mMenuItems.add(new MenuItem(itemNames[i], itemIcons.getResourceId(i,0) ));
        }

        itemIcons.recycle();
    }

    private String getUid() {
        return Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
    }


    @Override
    public void onClick(View v) {
        int position = mRecyclerView.getChildLayoutPosition(v);
        Toast.makeText(getActivity(), "clicked " + position, Toast.LENGTH_SHORT).show();
    }
}
