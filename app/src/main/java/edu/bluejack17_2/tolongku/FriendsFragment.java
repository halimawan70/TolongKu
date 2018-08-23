package edu.bluejack17_2.tolongku;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Vector;


/**
 * A simple {@link Fragment} subclass.
 */
public class FriendsFragment extends Fragment {

    ListView lvFriendList;
    DatabaseReference dbRef;
    Vector<String> ids;
    Vector<User> users;
    String phoneNumber;
    CustomAdapter ca;

    final int MY_PERMISSIONS_REQUEST_PHONE_CALL = 0;

    public void populateFriendList()
    {
        dbRef.child("UserFriend").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(final DataSnapshot ds : dataSnapshot.getChildren())
                {

                    final String s = ds.getValue(String.class);

                    ids.add(s);

                    FirebaseDatabase.getInstance().getReference().child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for(DataSnapshot dsb : dataSnapshot.getChildren())
                            {

                                if(dsb.child("userID").getValue().equals(s))
                                {
                                    users.add(dsb.getValue(User.class));
                                    Log.d("FriendsFragment","user count : "+users.size());
                                    ca.notifyDataSetChanged();
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });


                }



            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {



        switch(requestCode) {
            case MY_PERMISSIONS_REQUEST_PHONE_CALL:
                dbRef.child("userContactNumber").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        phoneNumber = dataSnapshot.getValue(String.class);

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + phoneNumber));
                startActivity(callIntent);
                break;
        }
    }


    public FriendsFragment() {



    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ids = new Vector<>();
        users = new Vector<>();
        lvFriendList = getView().findViewById(R.id.lvFriendList);
        ca = new CustomAdapter();
        lvFriendList.setAdapter(ca);
        Toast.makeText(getActivity().getApplicationContext(),MainActivity.authID,Toast.LENGTH_SHORT).show();
        dbRef = FirebaseDatabase.getInstance().getReference().child("Users").child(MainActivity.authID);

        populateFriendList();




    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_friends, container, false);
    }

    class CustomAdapter extends BaseAdapter
    {
        @Override
        public int getCount() {
            return users.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(final int i, View view, ViewGroup viewGroup) {
            Log.d("FriendsFragment","populating LV : "+i);
            view = getLayoutInflater().inflate(R.layout.friendlistlayout,null);
            ImageView imgFriendList = view.findViewById(R.id.imgFriendList);
            TextView txtFriendListName = view.findViewById(R.id.txtfriendListName);
            Button btnFriendListChat = view.findViewById(R.id.btnFriendListChat);
            Button btnFriendListCall = view.findViewById(R.id.btnFriendListCall);

            imgFriendList.setImageResource(R.drawable.default_profile);
            txtFriendListName.setText(users.get(i).getUserName());
            final String currUserName  = users.get(i).getUserName();
            final String currID = users.get(i).getUserID();

            btnFriendListChat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(getActivity().getApplicationContext(), ChatActivity.class);
                    i.putExtra("username",currUserName);
                    i.putExtra("id",currID);
                    Toast.makeText(getActivity().getApplicationContext(),"initiate",Toast.LENGTH_SHORT).show();
                    Log.d("FriendsFragment","initiate");
                    startActivity(i);
                }
            });

            btnFriendListCall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED)
                    {

                        ActivityCompat.requestPermissions(getActivity(),
                                new String[]{Manifest.permission.CALL_PHONE},
                                MY_PERMISSIONS_REQUEST_PHONE_CALL);
                    }
                    else
                    {

                        Intent callIntent = new Intent(Intent.ACTION_CALL);
                        callIntent.setData(Uri.parse("tel:"+users.get(i).getUserContactNumber()));
                        startActivity(callIntent);
                    }

                }
            });



            return view;
        }
    }
}
