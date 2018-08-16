package edu.bluejack17_2.tolongku;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
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

public class searchFriendActivity extends AppCompatActivity implements View.OnClickListener{

    ListView lvSearch;
    EditText txtSearch;
    Button btnSearch;
    Vector<User> users;
    TextView lblSearchFriendID;
    CustomAdapter ca;

    RecyclerView rvSearch;

    DatabaseReference dbRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_friend);
        lvSearch = (ListView) findViewById(R.id.lvSearch);
        btnSearch = findViewById(R.id.btnSearch);
        btnSearch.setOnClickListener(this);
        txtSearch = findViewById(R.id.txtSearch);
        //rvSearch=findViewById(R.id.rvSearch);

        users = new Vector<>();
        dbRef = FirebaseDatabase.getInstance().getReference().child("Users");
        if(lvSearch == null)
        {
            Log.d("searchFriendActivity","null euy");
        }
        txtSearch = findViewById(R.id.txtSearch);
        //String[] strs = {"a","B","c"};
        //users.add(new User(strs,"test","test","test","test","test","test","test","test"));
        ca = new CustomAdapter();
        lvSearch.setAdapter(ca);

    }

    void doSearch()
    {

        users.clear();
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren())
                {
                    User u = ds.getValue(User.class);
                    if(u != null)
                    {
                        if(u.getUserName().contains(txtSearch.getText().toString()))
                        {
                            Log.d("searchFriendActivity","users count : "+users.size());
                            users.add(u);

                        }
                    }


                }
                ca.notifyDataSetChanged();
                //CustomAdapter ca = new CustomAdapter();
                //lvSearch.setAdapter(ca);
                //rvSearch.setAdapter(new CustomRAdapter(getApplicationContext(),users));

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



    }


    @Override
    public void onClick(View v) {
        switch(v.getId())
        {
            case R.id.btnSearch:
                doSearch();
                break;
        }
    }




    class CustomAdapter extends BaseAdapter
    {

        Vector<String> ids = new Vector<>();
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

        @SuppressLint("SetTextI18n")
        @Override
        public View getView(final int i, View view, ViewGroup viewGroup) {
            Log.d ("searchFriendActivity","populate : "+i);
            view = getLayoutInflater().inflate(R.layout.searchfriendlayout,null);
            //setContentView(R.layout.searchfriendlayout);
            ImageView imgSearchFriend = view.findViewById(R.id.imgAddFriend);
            TextView lblSearchFriend = view.findViewById(R.id.lblAddFriend);
            lblSearchFriendID = view.findViewById(R.id.lblAddFriendID);
            Button btnSearchFriend = view.findViewById(R.id.btnAddFriend);


            imgSearchFriend.setImageResource(R.drawable.default_profile);
            lblSearchFriendID.setText(users.get(i).getUserID());
            lblSearchFriend.setText(users.get(i).getUserName());
            btnSearchFriend.setText("add as friend");

            ids.add(users.get(i).getUserID());

            btnSearchFriend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    Toast.makeText(getApplicationContext(),MainActivity.authID,Toast.LENGTH_SHORT).show();
                    dbRef.child(MainActivity.authID).addListenerForSingleValueEvent(new ValueEventListener() {
                        boolean flag = true;
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Toast.makeText(getApplicationContext(),MainActivity.authID,Toast.LENGTH_SHORT).show();
                            dbRef.child(MainActivity.authID).child("UserFriend").addListenerForSingleValueEvent(new ValueEventListener() {


                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    for(DataSnapshot ds : dataSnapshot.getChildren())
                                    {
                                        Log.d("searchFriendActivity",ds.getValue().toString()+":"+ids.get(i));
                                        if(ds.getValue().toString().equals(ids.get(i)))
                                        {
                                            Log.d("searchFriendActivity","false");

                                            flag = false;
                                        }
                                    }
                                    if(flag)
                                    {
                                        dbRef.child(MainActivity.authID).child("UserFriend").push().setValue(ids.get(i));
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });


                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                }
            });

            return view;
        }



        public void addToFriend()
        {



        }

    }

}
