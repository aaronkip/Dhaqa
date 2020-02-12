package com.kenova.store.Fragment;


import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kenova.store.Constants.BaseApp;
import com.kenova.store.Item.MessageItem;
import com.kenova.store.Constants.Functions;
import com.kenova.store.Constants.Constants;
import com.kenova.store.Activity.MainActivity;
import com.kenova.store.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.kenova.store.Models.MessageModels;

import java.util.ArrayList;
import java.util.Collections;


public class MessageFragment extends Fragment {


    View getView;
    Context context;

    RecyclerView inboxList;
    BaseApp baseApp;

    ArrayList<MessageModels> inboxArraylist;

    DatabaseReference rootRef;

    MessageItem inboxItem;

    boolean isviewCreated = false;

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getView = inflater.inflate(R.layout.fragment_recycleview, container, false);
        context = getContext();

        rootRef = FirebaseDatabase.getInstance().getReference();
        baseApp = BaseApp.getInstance();

        inboxList = getView.findViewById(R.id.recycleview);

        inboxArraylist =new ArrayList<>();

        inboxList = (RecyclerView) getView.findViewById(R.id.recycleview);
        LinearLayoutManager layout = new LinearLayoutManager(context);
        inboxList.setLayoutManager(layout);
        inboxList.setHasFixedSize(false);
        inboxItem = new MessageItem(context, inboxArraylist, new MessageItem.OnItemClickListener() {
            @Override
            public void onItemClick(MessageModels item) {

                if(check_ReadStoragepermission())
                    chatFragment(MainActivity.user_id,item.getId(),item.getName(),item.getPicture());


            }
        }, new MessageItem.OnLongItemClickListener() {
            @Override
            public void onLongItemClick(MessageModels item) {

            }
        });

        inboxList.setAdapter(inboxItem);



        getView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Functions.hideSoftKeyboard(getActivity());
            }
        });

        isviewCreated = true;
        return getView;
    }

    ValueEventListener valueEventListener;

    Query inboxQuery;

    @Override
    public void onStart() {
        super.onStart();
        if (baseApp.getIsLogin()) {
            inboxQuery = rootRef.child("Inbox").child(MainActivity.user_id).orderByChild("date");
            valueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    inboxArraylist.clear();

                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        MessageModels model = new MessageModels();
                        model.setId(ds.getKey());
                        model.setName(ds.child("name").getValue().toString());
                        model.setMessage(ds.child("msg").getValue().toString());
                        model.setTimestamp(ds.child("date").getValue().toString());
                        model.setStatus(ds.child("status").getValue().toString());
                        model.setPicture(ds.child("pic").getValue().toString());
                        inboxArraylist.add(model);
                    }
                    Collections.reverse(inboxArraylist);
                    inboxItem.notifyDataSetChanged();

                    if (inboxArraylist.isEmpty()) {
                        getView.findViewById(R.id.noresult).setVisibility(View.VISIBLE);
                    } else {
                        getView.findViewById(R.id.noresult).setVisibility(View.GONE);
                    }
                }


                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }


            };


            inboxQuery.addValueEventListener(valueEventListener);
        } else {
            getView.findViewById(R.id.noresult).setVisibility(View.VISIBLE);
        }

    }



    @Override
    public void onStop() {
        super.onStop();
        if(inboxQuery !=null)
            inboxQuery.removeEventListener(valueEventListener);
    }

    public void chatFragment(String senderid,String receiverid,String name,String picture){
        ChatFragment chat_fragment = new ChatFragment();
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        Bundle args = new Bundle();
        args.putString("Sender_Id",senderid);
        args.putString("Receiver_Id",receiverid);
        args.putString("picture",picture);
        args.putString("name",name);
        chat_fragment.setArguments(args);
        transaction.addToBackStack(null);
        transaction.replace(R.id.MainFragment, chat_fragment).commit();
    }

    private boolean check_ReadStoragepermission(){
        if (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED){
            return true;
        }
        else {
            try {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        Constants.permission_Read_data );
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }
        return false;
    }







}
